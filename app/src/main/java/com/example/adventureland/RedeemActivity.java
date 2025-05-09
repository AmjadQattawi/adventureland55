package com.example.adventureland;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class RedeemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RedeemAdapter redeemAdapter;
    private ArrayList<RedeemItem> redeemItemList;
    private DatabaseReference userCardsRef, userPointsRef;
    private String userId;
    private ArrayList<String> cardNames;
    private Long userPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem);

        // إعداد Firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userCardsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cards");
        userPointsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("points_balance");

        // تهيئة قائمة المكافآت
        redeemItemList = new ArrayList<>();

        // تهيئة الـ RecyclerView و الـ Adapter
        recyclerView = findViewById(R.id.recyclerViewOffers);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        redeemAdapter = new RedeemAdapter(this, redeemItemList);
        recyclerView.setAdapter(redeemAdapter);

        // تحميل المكافآت من Firebase
        loadRewardsFromFirebase();
        loadUserPoints();

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void loadRewardsFromFirebase() {
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
                userPoints = dataSnapshot.getValue(Long.class);
                if (userPoints == null) {
                    userPoints = 0L;
                }
                if (userPoints == 0) {
                    Toast.makeText(RedeemActivity.this, "You have no points.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RedeemActivity.this, "Error loading points", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showCardSelectionDialog(String selectedReward, long rewardCost) {
        if (userPoints < rewardCost) {
            Toast.makeText(RedeemActivity.this, "Not enough points for this reward", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardNames == null || cardNames.isEmpty()) {
            Toast.makeText(RedeemActivity.this, "No cards available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] cardOptions = cardNames.toArray(new String[0]);

        // استخدام التصميم المخصص لـ AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_choose_card, null);
        builder.setView(dialogView);

        // إعداد RecyclerView لعرض البطاقات
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewCards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // إعداد بيانات البطاقات
        ArrayList<String> cardList = new ArrayList<>();
        cardList.add("Card 1: 100 Points");
        cardList.add("Card 2: 200 Points");

        CardListAdapter adapter = new CardListAdapter(this, cardList);
        recyclerView.setAdapter(adapter);

        Button selectButton = dialogView.findViewById(R.id.button_positive);
        Button cancelButton = dialogView.findViewById(R.id.button_negative);

        selectButton.setOnClickListener(v -> {
            String selectedCard = cardOptions[0];
            addRewardToCard(selectedCard, selectedReward, rewardCost);
        });

        cancelButton.setOnClickListener(v -> {
            AlertDialog dialog = builder.create();
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addRewardToCard(String selectedCard, String selectedReward, long rewardCost) {
        userCardsRef.child(selectedCard.toLowerCase().replace(" ", "_") + "_card").child("balance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long balance = dataSnapshot.getValue(Long.class);
                        if (balance == null) {
                            balance = 0L;
                        }

                        balance += rewardCost;

                        userCardsRef.child(selectedCard.toLowerCase().replace(" ", "_") + "_card").child("balance")
                                .setValue(balance);

                        userPoints -= rewardCost;
                        userPointsRef.setValue(userPoints);

                        Toast.makeText(RedeemActivity.this, "تم إضافة " + selectedReward + " إلى " + selectedCard, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(RedeemActivity.this, "Error updating card balance", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
