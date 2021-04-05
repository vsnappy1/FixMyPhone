import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
admin.initializeApp();

const Stripe = require('stripe');
const stripe = Stripe('sk_test_51IVMDOACLamgLPNiyUdkvluIRINDTcZ6MLLwICcmRgWM3jQQ4QIBcGVS6F2uUPdfgBKHajypHC0D4wfAtwyzdzdK00Q51L8cD8');


/**************************************** Test Function ****************************************/

exports.helloWorld = functions.https.onRequest((request, response) => {
    functions.logger.info("request " + request, { structuredData: true });
    functions.logger.info("request body " + request.body, { structuredData: true });
    functions.logger.info("request body text " + request.body.data.text, { structuredData: true });
    functions.logger.info("request body push " + request.body.data.push, { structuredData: true });
    functions.logger.info("Hello logs!", { structuredData: true });

    response.status(200).send({ data: "{msg: Hello Kumar}" });
});


/**************************************** Payments ****************************************/

exports.stripePayment = functions.https.onRequest(async (request, response) => {

    const amt = request.body.data.amount;

    functions.logger.info("Stripe " + amt, { structuredData: true });

    const paymentIntent = await stripe.paymentIntents.create({
        amount: amt,
        currency: 'pkr',
    });
    const clientSecret = paymentIntent.client_secret;

    response.status(200).send({ data: "{clientSecret: " + clientSecret + "}" });
});



/**************************************** Notifications ****************************************/

//Send notification to vendor when a new order is arrived
export const sendNotificationToVendorActiveOrder = functions.database.ref('/activeOrder/{orderId}')
    .onCreate(async snapshot => {

        const order = snapshot.val();

        const leadsRef = admin.database().ref('vendor').child(order.vendorId).child('token');

        leadsRef.on("value", snap => {
            let token = snap.val();

            // This registration token comes from the client FCM SDKs.
            const registrationToken = token.toString();

            const message = {
                notification: {
                    title: 'New Order Received!',
                    body: `Quote Rs. ${order.quote}`,
                },
                token: registrationToken
            };

            // Send a message to the device corresponding to the provided
            // registration token.
            admin.messaging().send(message)
                .then((response) => {
                    functions.logger.info("New Order notification sent to " + order.vendorName, { structuredData: true });
                })
                .catch((error) => {
                    functions.logger.info("Error sending New Order notification to " + order.vendorName + " error " + error, { structuredData: true });
                });
        });
    });

//Send notification to vendor when payment is made
export const sendNotificationToVendorPaymentMade = functions.database.ref('/activeOrder/{orderId}')
    .onUpdate(async snapshot => {

        const order = snapshot.after.val();

        if (order.paymentStatus == "paid" && order.rateAndReviewStatus == "not rated") {

            const leadsRef = admin.database().ref('vendor').child(order.vendorId).child('token');

            leadsRef.on("value", snap => {
                let token = snap.val();

                // This registration token comes from the client FCM SDKs.
                const registrationToken = token.toString();

                const message = {
                    notification: {
                        title: 'Payment Received!',
                        body: `Rs. ${order.quote} from ${order.userName} `,
                    },
                    token: registrationToken
                };

                // Send a message to the device corresponding to the provided
                // registration token.
                admin.messaging().send(message)
                    .then((response) => {
                        functions.logger.info("Payment Received notification sent to " + order.vendorName, { structuredData: true });

                    })
                    .catch((error) => {
                        functions.logger.info("Error sending Payment Received notification to " + order.vendorName + " error " + error, { structuredData: true });
                    });
            });
        }
    });

//Send notification to vendor when a order is Completed and review is left
export const sendNotificationToVendorCompletedOrder = functions.database.ref('/completedOrder/{orderId}')
    .onCreate(async snapshot => {

        const order = snapshot.val();

        const leadsRef = admin.database().ref('vendor').child(order.vendorId).child('token');

        leadsRef.on("value", snap => {
            let token = snap.val();

            // This registration token comes from the client FCM SDKs.
            const registrationToken = token.toString();

            const message = {
                notification: {
                    title: 'Order completed!',
                    body: `${order.userName} left a review`,
                },
                token: registrationToken
            };

            // Send a message to the device corresponding to the provided
            // registration token.
            admin.messaging().send(message)
                .then((response) => {
                    // Response is a message ID string.
                    functions.logger.info("Completed notification sent to " + order.vendorName, { structuredData: true });

                })
                .catch((error) => {
                    functions.logger.info("Error sending Completed Order notification sent to " + order.vendorName + " error " + error, { structuredData: true });
                });
        });
    });
