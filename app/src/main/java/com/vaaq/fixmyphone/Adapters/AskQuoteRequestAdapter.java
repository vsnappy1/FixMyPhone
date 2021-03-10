package com.vaaq.fixmyphone.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.VendorActivities.AskQuoteRequestsActivity;
import com.vaaq.fixmyphone.VendorActivities.SubmitQuoteActivity;
import com.vaaq.fixmyphone.models.GetQuote;

import java.util.ArrayList;

public class AskQuoteRequestAdapter extends RecyclerView.Adapter<AskQuoteRequestAdapter. AskQuoteRequestView> {

    ArrayList<GetQuote> list;
    Context context;
    Activity activity;
    ClickListener clickListener;

    public AskQuoteRequestAdapter(ArrayList<GetQuote> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;

    }

    public class AskQuoteRequestView extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewName;
        TextView textViewBrand;
        TextView textViewModel;
        TextView textViewDescription;
        public AskQuoteRequestView(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewName = itemView.findViewById(R.id.textViewItemAskQuoteUsername);
            textViewBrand = itemView.findViewById(R.id.textViewItemAskQuoteBrand);
            textViewModel = itemView.findViewById(R.id.textViewItemAskQuoteModel);
            textViewDescription = itemView.findViewById(R.id.textViewItemAskQuoteDescription);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public AskQuoteRequestAdapter.AskQuoteRequestView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ask_quote_request, parent, false);
        return new AskQuoteRequestView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AskQuoteRequestAdapter.AskQuoteRequestView holder, int position) {

        holder.textViewName.setText(list.get(position).getName());
        holder.textViewBrand.setText(list.get(position).getBrand());
        holder.textViewModel.setText(list.get(position).getModel());
        holder.textViewDescription.setText(list.get(position).getDescription());
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
