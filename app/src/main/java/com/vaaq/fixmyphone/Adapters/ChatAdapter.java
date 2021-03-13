package com.vaaq.fixmyphone.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.Message;
import com.vaaq.fixmyphone.utils.FormatDateAndTime;

import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ClickListener clickListener;
    private final int SENDER = 0, RECEIVER = 1;
    private ArrayList<Message> list;
    private String senderId;
    private Context context;

    public ChatAdapter(Context context, ArrayList<Message> list, String senderId) {
        this.context = context;
        this.list = list;
        this.senderId = senderId;
    }


    class ViewHolderSender extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView textViewMessage;
        TextView textViewTime;
        TextView textViewTime2;

        ConstraintLayout constraintLayoutImage;
        ImageView imageViewImage;

        public ViewHolderSender(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);

            textViewMessage = itemView.findViewById(R.id.textViewMessageSentBody);
            textViewTime = itemView.findViewById(R.id.textViewMessageSentTime);
            textViewTime2 = itemView.findViewById(R.id.textViewMessageSentTime2);
            constraintLayoutImage = itemView.findViewById(R.id.constraintLayoutMessageSent);
            imageViewImage = itemView.findViewById(R.id.imageViewMessageSentImage);


        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onLongClick(getAdapterPosition(), view);
            return false;
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemLongClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onLongClick(int position, View v);
    }

    class ViewHolderReceiver extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTime;
        TextView textViewTime2;
        ImageView imageViewProfile;
        ConstraintLayout constraintLayoutImage;
        ImageView imageViewImage;


        public ViewHolderReceiver(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewMessageReceivedName);
            textViewMessage = itemView.findViewById(R.id.textViewMessageReceivedBody);
            textViewTime = itemView.findViewById(R.id.textViewMessageReceivedTime);
            textViewTime2 = itemView.findViewById(R.id.textViewMessageReceivedTime2);
            constraintLayoutImage = itemView.findViewById(R.id.constraintLayoutMessageReceived);
            imageViewImage = itemView.findViewById(R.id.imageViewMessageReceivedImage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getSenderId().equals(senderId)) {
            return SENDER;
        } else {
            return RECEIVER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SENDER:
                View v1 = inflater.inflate(R.layout.item_message_sent, viewGroup, false);
                viewHolder = new ViewHolderSender(v1);
                break;
            case RECEIVER:
                View v2 = inflater.inflate(R.layout.item_message_received, viewGroup, false);
                viewHolder = new ViewHolderReceiver(v2);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (viewHolder.getItemViewType()) {

            case SENDER:
                ViewHolderSender viewHolderSender = (ViewHolderSender) viewHolder;
                Date date = new Date(list.get(i).getTime());

                if (list.get(i).isImage()) {
                    viewHolderSender.constraintLayoutImage.setVisibility(View.VISIBLE);
                    viewHolderSender.textViewTime2.setVisibility(View.VISIBLE);
                    viewHolderSender.textViewTime2.setText(new FormatDateAndTime().getFormattedDate(date.getTime()));

                    fetchImage(list.get(i).getUrl(), viewHolderSender.imageViewImage);
                } else {
                    viewHolderSender.constraintLayoutImage.setVisibility(View.GONE);
                    viewHolderSender.textViewTime2.setVisibility(View.GONE);
                    viewHolderSender.textViewMessage.setText(list.get(i).getMessage());
                }

                viewHolderSender.textViewTime.setText(new FormatDateAndTime().getFormattedDate(date.getTime()));
                break;

            case RECEIVER:
                ViewHolderReceiver viewHolderReceiver = (ViewHolderReceiver) viewHolder;
                Date date1 = new Date(list.get(i).getTime());

                // Load image

                if (list.get(i).isImage()) {
                    viewHolderReceiver.constraintLayoutImage.setVisibility(View.VISIBLE);
                    viewHolderReceiver.textViewTime2.setVisibility(View.VISIBLE);
                    viewHolderReceiver.textViewTime2.setText(new FormatDateAndTime().getFormattedDate(date1.getTime()));

                    fetchImage(list.get(i).getUrl(), viewHolderReceiver.imageViewImage);

                } else {
                    viewHolderReceiver.constraintLayoutImage.setVisibility(View.GONE);
                    viewHolderReceiver.textViewTime2.setVisibility(View.GONE);
                    viewHolderReceiver.textViewMessage.setText(list.get(i).getMessage());
                }

                viewHolderReceiver.textViewName.setText(list.get(i).getSenderName());
                viewHolderReceiver.textViewTime.setText(new FormatDateAndTime().getFormattedDate(date1.getTime()));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    private void fetchImage(String url, ImageView imageView) {
        try {
            //StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            Glide.with(context)
                    .load(url)
                    .into(imageView);
        } catch (Exception e) {
            Log.e("ERRORRRR", e.toString());
        }

    }


}
