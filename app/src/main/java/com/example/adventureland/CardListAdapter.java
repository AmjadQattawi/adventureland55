package com.example.adventureland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<String> cardList;

    public CardListAdapter(Context context, ArrayList<String> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_choose_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        String card = cardList.get(position);
        holder.cardName.setText(card); // افتراض أن كل بطاقة تحتوي على اسم
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView cardName;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.card_name); // افتراض أنك تستخدم TextView لعرض اسم البطاقة
        }
    }
}
