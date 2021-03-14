package com.vaaq.fixmyphone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.RateAndReview;

import java.util.ArrayList;

public class RateReviewAdapter extends RecyclerView.Adapter<RateReviewAdapter.ViewHolder> {


    Context context;
    ArrayList<RateAndReview> list;
    int filledStar, unfilledStar;


    public RateReviewAdapter(Context context, ArrayList<RateAndReview> list) {
        this.context = context;
        this.list = list;
        filledStar = R.drawable.ic_star;
        unfilledStar = R.drawable.ic_star_border;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  textViewUsername;
        TextView  textViewReview;
        ImageView imageViewStar1;
        ImageView imageViewStar2;
        ImageView imageViewStar3;
        ImageView imageViewStar4;
        ImageView imageViewStar5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUsername   = itemView.findViewById(R.id.textViewItemRARuserName);
            textViewReview   = itemView.findViewById(R.id.textViewItemRARReview);
            imageViewStar1   = itemView.findViewById(R.id.imageViewStart1);
            imageViewStar2   = itemView.findViewById(R.id.imageViewStart2);
            imageViewStar3   = itemView.findViewById(R.id.imageViewStart3);
            imageViewStar4   = itemView.findViewById(R.id.imageViewStart4);
            imageViewStar5   = itemView.findViewById(R.id.imageViewStart5);

        }
    }

    @NonNull
    @Override
    public RateReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_rate_review,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateReviewAdapter.ViewHolder holder, int position) {

        RateAndReview item = list.get(position);

        holder.textViewUsername.setText(item.getUserName());
        holder.textViewReview.setText(item.getReview());
        long starCount = list.get(position).getStarCount();


        if(starCount == 1){
            holder.imageViewStar1.setBackgroundResource(filledStar);
            holder.imageViewStar2.setBackgroundResource(unfilledStar);
            holder.imageViewStar3.setBackgroundResource(unfilledStar);
            holder.imageViewStar4.setBackgroundResource(unfilledStar);
            holder.imageViewStar5.setBackgroundResource(unfilledStar);
        }
        else if(starCount == 2){
            holder.imageViewStar1.setBackgroundResource(filledStar);
            holder.imageViewStar2.setBackgroundResource(filledStar);
            holder.imageViewStar3.setBackgroundResource(unfilledStar);
            holder.imageViewStar4.setBackgroundResource(unfilledStar);
            holder.imageViewStar5.setBackgroundResource(unfilledStar);
        }
        else if(starCount == 3){
            holder.imageViewStar1.setBackgroundResource(filledStar);
            holder.imageViewStar2.setBackgroundResource(filledStar);
            holder.imageViewStar3.setBackgroundResource(filledStar);
            holder.imageViewStar4.setBackgroundResource(unfilledStar);
            holder.imageViewStar5.setBackgroundResource(unfilledStar);
        }
        else if(starCount == 4){
            holder.imageViewStar1.setBackgroundResource(filledStar);
            holder.imageViewStar2.setBackgroundResource(filledStar);
            holder.imageViewStar3.setBackgroundResource(filledStar);
            holder.imageViewStar4.setBackgroundResource(filledStar);
            holder.imageViewStar5.setBackgroundResource(unfilledStar);
        }
        else if(starCount == 5){
            holder.imageViewStar1.setBackgroundResource(filledStar);
            holder.imageViewStar2.setBackgroundResource(filledStar);
            holder.imageViewStar3.setBackgroundResource(filledStar);
            holder.imageViewStar4.setBackgroundResource(filledStar);
            holder.imageViewStar5.setBackgroundResource(filledStar);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
