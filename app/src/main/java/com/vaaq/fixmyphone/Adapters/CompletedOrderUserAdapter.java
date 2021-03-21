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
import com.vaaq.fixmyphone.models.ActiveOrder;

import java.util.ArrayList;
import java.util.Date;

public class CompletedOrderUserAdapter extends RecyclerView.Adapter<CompletedOrderUserAdapter.ActiveOrderHolder> {

    ArrayList<ActiveOrder> list;
    Context context;
    Activity activity;
    ClickListener clickListener;

    public CompletedOrderUserAdapter(ArrayList<ActiveOrder> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    public class ActiveOrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewOrderId;
        TextView textViewBrand;
        TextView textViewModel;
        TextView textViewDescription;
        TextView textViewShopName;
        TextView textViewQuote;
        TextView textViewMessage;
        TextView textViewTime;


        public ActiveOrderHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            textViewOrderId = itemView.findViewById(R.id.textViewItemActiveOrderId);
            textViewBrand = itemView.findViewById(R.id.textViewItemActiveOrderBrand);
            textViewModel = itemView.findViewById(R.id.textViewItemActiveOrderModel);
            textViewDescription = itemView.findViewById(R.id.textViewItemActiveOrderDescription);
            textViewShopName = itemView.findViewById(R.id.textViewItemActiveOrderShopName);
            textViewQuote = itemView.findViewById(R.id.textViewItemActiveOrderQuote);
            textViewMessage = itemView.findViewById(R.id.textViewItemActiveOrderMessage);
            textViewTime = itemView.findViewById(R.id.textViewItemActiveOrderTime);

        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ActiveOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_completed_order_user, parent, false);
        return new ActiveOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveOrderHolder holder, int position) {

        ActiveOrder activeOrder = list.get(position);

        Date date = new Date(activeOrder.getTime());

        holder.textViewOrderId.setText(activeOrder.getOrderId());
        holder.textViewBrand.setText(activeOrder.getBrand());
        holder.textViewModel.setText(activeOrder.getModel());
        holder.textViewDescription.setText(activeOrder.getDescription());
        holder.textViewShopName.setText(activeOrder.getShopName());
        holder.textViewQuote.setText(activeOrder.getQuote());
        holder.textViewMessage.setText(activeOrder.getMessage());
        holder.textViewTime.setText(date.toString());

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
