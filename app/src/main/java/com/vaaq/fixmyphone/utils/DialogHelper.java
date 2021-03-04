package com.vaaq.fixmyphone.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.vaaq.fixmyphone.R;

public class DialogHelper {

    Context context;
    Dialog dialog;

    public DialogHelper(Context context){
        this.context = context;
    }

    public void hideProgressDialog() {
        try{
            dialog.dismiss();
            dialog = null;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showProgressDialog(String message) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progressbar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = dialog.findViewById(R.id.textViewDialog);
        text.setText(message);
        dialog.show();
    }
}
