package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RechargeCardActivity extends AppCompatActivity {

    private EditText amountInput;
    private String cardId;
    private TextView balanceTextView;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rechargecard);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cards");

        amountInput = findViewById(R.id.amount_input);
        CardView rechargeButton = findViewById(R.id.recharge_button);
        RadioGroup paymentMethods = findViewById(R.id.payment_methods);
        balanceTextView = findViewById(R.id.current_balance_value);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        cardId = getIntent().getStringExtra("cardId");

        if (cardId != null) {
            loadCurrentBalance(cardId);
        }

        rechargeButton.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (!(amount < 20 || amount == 20 || amount == 30 || amount == 50 || amount == 75 || amount == 100)) {
                Toast.makeText(this, "Invalid amount. Please select a valid offer.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(RechargeCardActivity.this, PaymentDetailActivity.class);
            intent.putExtra("cardId", cardId);
            intent.putExtra("amount", String.valueOf(amount));
            startActivity(intent);
        });
    }

    private void loadCurrentBalance(String cardId) {
        databaseReference.child(cardId).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                if (value != null) {
                    balanceTextView.setText(value.toString() + " JOD");
                } else {
                    balanceTextView.setText("0.000 JOD");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RechargeCardActivity.this, "Failed to load balance", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
