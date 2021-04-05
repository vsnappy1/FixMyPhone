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

import java.util.ArrayList;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteHolder> {

    ArrayList<GetQuote> list;
    Context context;
    Activity activity;
    ClickListener clickListener;

    public QuoteAdapter(ArrayList<GetQuote> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    public class QuoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewBrand;
        TextView textViewModel;
        TextView textViewDescription;
        TextView textViewResponseCount;

        public QuoteHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewBrand = itemView.findViewById(R.id.textViewItemQuoteBrand);
            textViewModel = itemView.findViewById(R.id.textViewItemQuoteModel);
            textViewDescription = itemView.findViewById(R.id.textViewItemQuoteDescription);
            textViewResponseCount = itemView.findViewById(R.id.textViewItemQuoteResponseCount);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public QuoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quote, parent, false);
        return new QuoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteHolder holder, int position) {

        GetQuote getQuote = list.get(position);
        holder.textViewBrand.setText(getQuote.getBrand());
        holder.textViewModel.setText(getQuote.getModel());
        holder.textViewDescription.setText(getQuote.getDescription());
        holder.textViewResponseCount.setText(String.valueOf(getQuote.getList().size()));
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
