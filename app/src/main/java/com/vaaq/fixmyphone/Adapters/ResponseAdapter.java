package com.vaaq.fixmyphone.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.GetQuote;
import com.vaaq.fixmyphone.models.Quote;
import com.vaaq.fixmyphone.utils.FormatDateAndTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ResponseHolder> {

    ArrayList<Quote> list;
    Context context;
    Activity activity;
    ClickListener clickListener;

    public ResponseAdapter(ArrayList<Quote> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    public class ResponseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewShopName;
        TextView textViewQuote;
        TextView textViewTime;
        TextView textViewDescription;

        public ResponseHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewShopName = itemView.findViewById(R.id.textViewItemResponseShopName);
            textViewQuote = itemView.findViewById(R.id.textViewItemResponseQuote);
            textViewTime = itemView.findViewById(R.id.textViewItemResponseTime);
            textViewDescription = itemView.findViewById(R.id.textViewItemResponseDescription);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ResponseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_response, parent, false);
        return new ResponseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseHolder holder, int position) {

        Quote getQuote = list.get(position);
        holder.textViewShopName.setText(getQuote.getShopName());
        holder.textViewQuote.setText("Rs. "+NumberFormat.getNumberInstance(Locale.US).format(Long.parseLong(getQuote.getQuote())));
        holder.textViewDescription.setText(getQuote.getMessage());
        holder.textViewTime.setText(new FormatDateAndTime().getFormattedDate(getQuote.getTime()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}
