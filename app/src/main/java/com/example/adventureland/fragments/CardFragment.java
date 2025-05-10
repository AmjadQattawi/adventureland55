package com.example.adventureland.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardFragment extends Fragment {

    private ImageView backIcon;
    private CardView checkBalanceIcon;
    private EditText cardNumberInput;
    private LinearLayout yourCardsContainer;
    private DatabaseReference userCardsRef;
    private String userId;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userCardsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cards");

        backIcon = view.findViewById(R.id.back_button);
        checkBalanceIcon = view.findViewById(R.id.check_button);
        cardNumberInput = view.findViewById(R.id.card_number_input);
        yourCardsContainer = view.findViewById(R.id.your_cards_container);

        backIcon.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        checkBalanceIcon.setOnClickListener(v -> {
            String cardNumber = cardNumberInput.getText().toString().replace("-", "").trim();
            if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
                Toast.makeText(getContext(), "Please enter a valid 16-digit card number", Toast.LENGTH_SHORT).show();
                return;
            }
            checkCardInFirebase(cardNumber);
        });

        setupCardNumberFormatter();
        loadUserCards();
    }

    private void setupCardNumberFormatter() {
        cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            boolean isFormatting;
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;
                String digitsOnly = s.toString().replaceAll("-", "");
                if (digitsOnly.length() > 16) digitsOnly = digitsOnly.substring(0, 16);
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append("-");
                    formatted.append(digitsOnly.charAt(i));
                }
                cardNumberInput.setText(formatted.toString());
                cardNumberInput.setSelection(formatted.length());
                isFormatting = false;
            }
        });
    }

    private void checkCardInFirebase(String cardNumber) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                boolean found = false;

                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    DataSnapshot cardSnapshot = userSnapshot.child("cards").child(cardNumber);
                    if (cardSnapshot.exists()) {
                        String balance = String.valueOf(cardSnapshot.child("balance").getValue());
                        String lastUsage = cardSnapshot.child("lastUsage").exists()
                                ? cardSnapshot.child("lastUsage").getValue().toString()
                                : "0000/00/00 00:00";

                        String lastCharge = cardSnapshot.child("lastCharge").exists()
                                ? cardSnapshot.child("lastCharge").getValue().toString()
                                : "0000/00/00 00:00";


                        Intent intent = new Intent(getContext(), CheckBalanceActivity.class);
                        intent.putExtra("balance", balance != null ? balance : "0.000");
                        intent.putExtra("lastUsage", lastUsage != null ? lastUsage : "0000/00/00 00:00");
                        intent.putExtra("lastCharge", lastCharge != null ? lastCharge : "0000/00/00 00:00");
                        intent.putExtra("cardId", cardNumber);  // تمرير رقم البطاقة
                        startActivity(intent);
                        found = true;
                        break;
                    }
                }

                if (!found) {
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
            userCardsRef.child(cardNumber).child("balance").setValue("0.000");
            String currentTime = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());

            userCardsRef.child(cardNumber).child("lastUsage").setValue(currentTime);
            userCardsRef.child(cardNumber).child("lastCharge").setValue(currentTime);

            Toast.makeText(getContext(), "Card added successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadUserCards();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadUserCards() {
        yourCardsContainer.removeAllViews();
        userCardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    String cardId = cardSnapshot.getKey();
                    String balance = String.valueOf(cardSnapshot.child("balance").getValue());
                    String lastUsage = cardSnapshot.child("lastUsage").exists()
                            ? cardSnapshot.child("lastUsage").getValue().toString()
                            : "0000/00/00 00:00";

                    String lastCharge = cardSnapshot.child("lastCharge").exists()
                            ? cardSnapshot.child("lastCharge").getValue().toString()
                            : "0000/00/00 00:00";


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
                        intent.putExtra("lastUsage", lastUsage != null ? lastUsage : "0000/00/00 00:00");
                        intent.putExtra("lastCharge", lastCharge != null ? lastCharge : "0000/00/00 00:00");
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

            @Override public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Error loading cards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card, container, false);
    }
}
