package com.example.adventureland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<String> cardList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public CardListAdapter(Context context, ArrayList<String> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    public interface OnItemClickListener {
        void onItemClick(String cardName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_dialog, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        String card = cardList.get(position);
        holder.cardName.setText(card);

        boolean isSelected = (position == selectedPosition);

        holder.cardContainer.setBackgroundResource(isSelected
                ? R.drawable.card_item_selected
                : R.drawable.card_item);

        holder.cardName.setTextColor(context.getResources().getColor(
                isSelected ? android.R.color.black : android.R.color.black
        ));

        holder.cardContainer.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            int previousPosition = selectedPosition;
            selectedPosition = currentPosition;

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(cardList.get(currentPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardName;
        ConstraintLayout cardContainer;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.card_name);
            cardContainer = itemView.findViewById(R.id.card_container);
        }
    }
}
