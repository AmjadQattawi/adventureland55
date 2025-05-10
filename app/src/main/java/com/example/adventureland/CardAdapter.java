package com.example.adventureland;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adventureland.CheckBalanceActivity;
import com.example.adventureland.R;
import com.example.adventureland.RechargeCardActivity;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final Context context;
    private final List<CardItem> cardList;

    public CardAdapter(Context context, List<CardItem> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem card = cardList.get(position);
        holder.cardName.setText("Card: " + card.getCardId());
        holder.cardBalance.setText("Balance: " + card.getBalance() + " JOD");

        holder.viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CheckBalanceActivity.class);
            intent.putExtra("cardId", card.getCardId());
            intent.putExtra("balance", card.getBalance());
            intent.putExtra("lastUsage", card.getLastUsage());
            intent.putExtra("lastCharge", card.getLastCharge());
            context.startActivity(intent);
        });

        holder.rechargeButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, RechargeCardActivity.class);
            intent.putExtra("cardId", card.getCardId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public String getCardIdAt(int position) {
        return cardList.get(position).getCardId();
    }

    public void removeAt(int position) {
        cardList.remove(position);
        notifyItemRemoved(position);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardName, cardBalance;
        Button viewButton, rechargeButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.card_name);
            cardBalance = itemView.findViewById(R.id.card_balance);
            viewButton = itemView.findViewById(R.id.button_view);
            rechargeButton = itemView.findViewById(R.id.button_recharge);
        }
    }

    public static class CardItem {
        private final String cardId, balance, lastUsage, lastCharge;

        public CardItem(String cardId, String balance, String lastUsage, String lastCharge) {
            this.cardId = cardId;
            this.balance = balance;
            this.lastUsage = lastUsage;
            this.lastCharge = lastCharge;
        }

        public String getCardId() {
            return cardId;
        }

        public String getBalance() {
            return balance;
        }

        public String getLastUsage() {
            return lastUsage;
        }

        public String getLastCharge() {
            return lastCharge;
        }
    }
}

