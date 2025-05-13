package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BalanceActivity extends AppCompatActivity {

    private EditText cardInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        ImageView backArrow = findViewById(R.id.back_card);
        backArrow.setOnClickListener(v -> finish());

        cardInput = findViewById(R.id.cardNumberInput); // تأكد أن الـ EditText موجود في XML بنفس الـ id
        setupCardNumberFormatter();

        Button checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(v -> checkCardInFirebase());
    }

    private void setupCardNumberFormatter() {
        cardInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        cardInput.addTextChangedListener(new TextWatcher() {
            boolean isFormatting;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String digitsOnly = s.toString().replaceAll("-", "");
                if (digitsOnly.length() > 16) digitsOnly = digitsOnly.substring(0, 16);

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append("-");
                    formatted.append(digitsOnly.charAt(i));
                }

                cardInput.setText(formatted.toString());
                cardInput.setSelection(formatted.length());
                isFormatting = false;
            }
        });
    }

    private void checkCardInFirebase() {
        String enteredCard = cardInput.getText().toString().replace("-", "").trim();

        if (enteredCard.length() != 16) {
            Toast.makeText(this, "Please enter a valid 16-digit card number", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    DataSnapshot cardSnap = userSnap.child("cards").child(enteredCard);
                    if (cardSnap.exists()) {
                        String balance = cardSnap.child("balance").exists() ? cardSnap.child("balance").getValue().toString() : "0.000";
                        String lastUsage = cardSnap.child("lastUsage").exists() ? cardSnap.child("lastUsage").getValue().toString() : "N/A";
                        String lastCharge = cardSnap.child("lastCharge").exists() ? cardSnap.child("lastCharge").getValue().toString() : "N/A";

                        Intent intent = new Intent(BalanceActivity.this, CardDetailsForNotLoginOrSignup.class);
                        intent.putExtra("balance", balance);
                        intent.putExtra("lastUsage", lastUsage);
                        intent.putExtra("lastCharge", lastCharge);
                        intent.putExtra("cardId", enteredCard);
                        startActivity(intent);
                        return;
                    }
                }

                Toast.makeText(BalanceActivity.this, "This card does not exist", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BalanceActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
