package com.example.adventureland.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.adventureland.CheckBalanceActivity;
import com.example.adventureland.R;
import com.example.adventureland.RechargeCardActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardFragment extends Fragment {

    private ImageView backIcon;
    private CardView checkBalanceIcon;
    private EditText cardNumberInput;
    private LinearLayout yourCardsContainer; // NEW
    private DatabaseReference databaseReference;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backIcon = view.findViewById(R.id.back_button);
        checkBalanceIcon = view.findViewById(R.id.check_button);
        cardNumberInput = view.findViewById(R.id.card_number_input);
        yourCardsContainer = view.findViewById(R.id.your_cards_container); // NEW

        databaseReference = FirebaseDatabase.getInstance().getReference("cards");

        backIcon.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        checkBalanceIcon.setOnClickListener(v -> {
            String cardNumber = cardNumberInput.getText().toString().trim();

            if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
                Toast.makeText(getContext(), "Please enter a valid 16-digit card number", Toast.LENGTH_SHORT).show();
                return;
            }

            checkCardInFirebase(cardNumber);
        });

        loadUserCards(); // NEW
    }

    private void checkCardInFirebase(String cardNumber) {
        databaseReference.child(cardNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String balance = snapshot.child("balance").getValue(String.class);
                    String lastUsage = snapshot.child("lastUsage").getValue(String.class);
                    String lastCharge = snapshot.child("lastCharge").getValue(String.class);

                    Intent intent = new Intent(getContext(), CheckBalanceActivity.class);
                    intent.putExtra("balance", balance != null ? balance : "0.000");
                    intent.putExtra("lastUsage", lastUsage != null ? lastUsage : "0000/00/00 00:00");
                    intent.putExtra("lastCharge", lastCharge != null ? lastCharge : "0000/00/00 00:00");
                    startActivity(intent);
                } else {
                    showCardNotFoundDialog(cardNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCardNotFoundDialog(String cardNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.cardnotfound, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button addButton = dialogView.findViewById(R.id.button_add);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);

        addButton.setOnClickListener(v -> {
            databaseReference.child(cardNumber).child("balance").setValue("0.000");
            databaseReference.child(cardNumber).child("lastUsage").setValue("0000/00/00 00:00");
            databaseReference.child(cardNumber).child("lastCharge").setValue("0000/00/00 00:00");

            Toast.makeText(getContext(), "Card added successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadUserCards(); // NEW → refresh the list after adding
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loadUserCards() { // NEW
        yourCardsContainer.removeAllViews();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    String cardId = cardSnapshot.getKey();
                    String balance = cardSnapshot.child("balance").getValue(String.class);

                    View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, yourCardsContainer, false);
                    TextView cardNameText = cardView.findViewById(R.id.card_name);
                    TextView balanceText = cardView.findViewById(R.id.card_balance);
                    Button viewButton = cardView.findViewById(R.id.button_view);
                    Button rechargeButton = cardView.findViewById(R.id.button_recharge);

                    cardNameText.setText("Card: " + cardId);
                    balanceText.setText("Balance: " + balance + " JOD");

                    viewButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), CheckBalanceActivity.class);
                        intent.putExtra("balance", balance);
                        intent.putExtra("cardId", cardId);
                        startActivity(intent);
                    });

                    rechargeButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), RechargeCardActivity.class);
                        intent.putExtra("cardId", cardId);
                        startActivity(intent);
                    });

                    yourCardsContainer.addView(cardView);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Error loading cards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card, container, false);
    }
}
