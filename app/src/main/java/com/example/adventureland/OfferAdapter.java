package com.example.adventureland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private List<Offer> offerList;
    private String offerType;
    private Context context;

    public OfferAdapter(Context context, List<Offer> offerList, String offerType) {
        this.offerList = offerList;
        this.offerType = offerType;
        this.context = context;
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText;
        CardView card;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            card = itemView.findViewById(R.id.offerCard);
        }
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        holder.titleText.setText(offer.getTitle());
        holder.descriptionText.setText(offer.getDescription());

        int colorRes = R.color.lightPurple;
        switch (offerType) {
            case "school":
                colorRes = R.color.orange;
                break;
            case "birthday":
                colorRes = R.color.yellow;
                break;
        }

        holder.card.setCardBackgroundColor(ContextCompat.getColor(context, colorRes));
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }
}
