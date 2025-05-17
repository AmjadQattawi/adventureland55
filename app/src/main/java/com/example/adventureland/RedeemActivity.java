package com.example.adventureland;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

import android.graphics.drawable.ColorDrawable;

public class RedeemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RedeemAdapter redeemAdapter;
    private ArrayList<RedeemItem> redeemItemList;
    private DatabaseReference userCardsRef, userPointsRef, transactionRef;
    private String userId;
    private ArrayList<String> cardNames;
    private long userPoints;
    private TextView pointsBalanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userCardsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cards");
        userPointsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("points");
        transactionRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("transactions");

        pointsBalanceText = findViewById(R.id.points_value);
        redeemItemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewOffers);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        redeemAdapter = new RedeemAdapter(this, redeemItemList);
        recyclerView.setAdapter(redeemAdapter);

        loadRewards();
        loadUserPoints();

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    public long getUserPoints() {
        return userPoints;
    }

    private void loadRewards() {
        redeemItemList.add(new RedeemItem("5 JOD Play card", "100", R.drawable.giftcard));
        redeemItemList.add(new RedeemItem("10 JOD Play card", "200", R.drawable.giftcard));
        redeemItemList.add(new RedeemItem("25 JOD Play card", "400", R.drawable.giftcard));
        redeemItemList.add(new RedeemItem("50 JOD Play card", "700", R.drawable.giftcard));
        redeemAdapter.notifyDataSetChanged();
    }

    private void loadUserPoints() {
        userPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long points = dataSnapshot.getValue(Long.class);
                userPoints = (points != null) ? points : 0;
                pointsBalanceText.setText(String.valueOf(userPoints));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RedeemActivity.this, "Error loading points", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadUserCardNames(String selectedReward, long rewardCost) {
        cardNames = new ArrayList<>();
        userCardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot cardSnap : snapshot.getChildren()) {
                    cardNames.add(cardSnap.getKey());
                }
                showCardSelectionDialog(selectedReward, rewardCost);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RedeemActivity.this, "Error loading cards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showCardSelectionDialog(String selectedReward, long rewardCost) {
        if (userPoints < rewardCost) {
            Toast.makeText(this, "Not enough points", Toast.LENGTH_SHORT).show();
            return;
        }

        userCardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<String> cardList = new ArrayList<>();
                for (DataSnapshot cardSnap : snapshot.getChildren()) {
                    cardList.add(cardSnap.getKey());
                }

                if (cardList.isEmpty()) {
                    Toast.makeText(RedeemActivity.this, "No cards available", Toast.LENGTH_SHORT).show();
                    return;
                }

                View dialogView = LayoutInflater.from(RedeemActivity.this).inflate(R.layout.alert_dialog_choose_card, null);
                RecyclerView cardsRecycler = dialogView.findViewById(R.id.recyclerViewCards);
                cardsRecycler.setLayoutManager(new LinearLayoutManager(RedeemActivity.this));
                CardListAdapter adapter = new CardListAdapter(RedeemActivity.this, cardList);
                cardsRecycler.setAdapter(adapter);

                final String[] selectedCard = {null};

                adapter.setOnItemClickListener(cardName -> selectedCard[0] = cardName);

                Button selectButton = dialogView.findViewById(R.id.button_positive);
                Button cancelButton = dialogView.findViewById(R.id.button_negative);

                AlertDialog dialog = new AlertDialog.Builder(RedeemActivity.this)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();

                selectButton.setOnClickListener(v -> {
                    if (selectedCard[0] == null) {
                        Toast.makeText(RedeemActivity.this, "Please select a card", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double amountToAdd = 0;
                    if (selectedReward.contains("5 JOD")) amountToAdd = 5;
                    else if (selectedReward.contains("10 JOD")) amountToAdd = 10;
                    else if (selectedReward.contains("25 JOD")) amountToAdd = 25;
                    else if (selectedReward.contains("50 JOD")) amountToAdd = 50;

                    dialog.dismiss();
                    showConfirmRedeemDialog(selectedCard[0], selectedReward, rewardCost, amountToAdd);
                });

                cancelButton.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RedeemActivity.this, "Failed to load cards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmRedeemDialog(String selectedCard, String selectedReward, long rewardCost, double amountToAdd) {
        View view = LayoutInflater.from(this).inflate(R.layout.confirmredeem, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        TextView message = view.findViewById(R.id.tv_confirm_redeem);
        message.setText("Are you sure you want to redeem " + rewardCost + " points for " + selectedReward + "?");

        Button okButton = view.findViewById(R.id.button_add);
        Button cancelButton = view.findViewById(R.id.button_cancel);

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            applyRewardToCard(selectedCard, selectedReward, rewardCost, amountToAdd);
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void showSuccessDialog(String selectedReward, long rewardCost) {
        View view = LayoutInflater.from(this).inflate(R.layout.sucessredeem, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        TextView message = view.findViewById(R.id.tv_success_redeem);
        message.setText("You’ve successfully redeemed " + rewardCost + " points for " + selectedReward + ".");

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void applyRewardToCard(String selectedCard, String selectedReward, long rewardCost, double amountToAdd) {
        userCardsRef.child(selectedCard).child("balance").get().addOnSuccessListener(snapshot -> {
            double balance = snapshot.exists() ? Double.parseDouble(snapshot.getValue().toString()) : 0;
            double newBalance = balance + amountToAdd;

            String currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date());

            userCardsRef.child(selectedCard).child("balance").setValue(newBalance);
            userCardsRef.child(selectedCard).child("lastCharge").setValue(currentTime);
            userCardsRef.child(selectedCard).child("lastUsage").setValue(currentTime);

            DatabaseReference globalCardRef = FirebaseDatabase.getInstance()
                    .getReference("cards").child(selectedCard);
            globalCardRef.child("balance").setValue(newBalance);
            globalCardRef.child("lastCharge").setValue(currentTime);
            globalCardRef.child("lastUsage").setValue(currentTime);

            userPointsRef.setValue(userPoints - rewardCost);

            Transaction transaction = new Transaction("spent", selectedReward, (int) rewardCost, getCurrentTimestamp());
            transactionRef.push().setValue(transaction);

            DatabaseReference cardTransactionRef = userCardsRef.child(selectedCard).child("transactions");
            CardTransaction cardTx = new CardTransaction("redeem", "Reward: " + selectedReward, (int) amountToAdd, getCurrentTimestamp());
            cardTransactionRef.push().setValue(cardTx);

            // ✅ أضف أيضًا إلى transactions في المسار العام
            DatabaseReference globalTxRef = FirebaseDatabase.getInstance().getReference("cards").child(selectedCard).child("transactions");
            globalTxRef.push().setValue(cardTx);

            showSuccessDialog(selectedReward, rewardCost);
        });
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
