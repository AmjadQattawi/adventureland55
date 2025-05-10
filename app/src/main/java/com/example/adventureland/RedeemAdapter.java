package com.example.adventureland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RedeemAdapter extends RecyclerView.Adapter<RedeemAdapter.RedeemViewHolder> {

    private Context context;
    private ArrayList<RedeemItem> redeemItemList;
    private RedeemActivity redeemActivity; // إشارة إلى النشاط ليتمكن من فتح الـ Dialog

    public RedeemAdapter(Context context, ArrayList<RedeemItem> redeemItemList) {
        this.context = context;
        this.redeemItemList = redeemItemList;
        this.redeemActivity = (RedeemActivity) context; // الحصول على النشاط الحالي
    }

    @Override
    public RedeemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.redeem_item, parent, false);
        return new RedeemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RedeemViewHolder holder, int position) {
        RedeemItem currentItem = redeemItemList.get(position);

        holder.cardTitle.setText(currentItem.getTitle());
        holder.cardPoints.setText(currentItem.getPoints());
        holder.cardImage.setImageResource(currentItem.getImageResource());

        long rewardCost = Long.parseLong(currentItem.getPoints());


        holder.redeemButton.setOnClickListener(v -> redeemActivity.loadUserCardNames(currentItem.getTitle(), rewardCost));
    }

    @Override
    public int getItemCount() {
        return redeemItemList.size();
    }

    public class RedeemViewHolder extends RecyclerView.ViewHolder {

        TextView cardTitle, cardPoints;
        ImageView cardImage;
        CardView redeemButton;

        public RedeemViewHolder(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.card_title_1);
            cardPoints = itemView.findViewById(R.id.card_points_1);
            cardImage = itemView.findViewById(R.id.card_icon_1);
            redeemButton = itemView.findViewById(R.id.redeem_button);
        }
    }
}
