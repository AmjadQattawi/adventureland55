package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CheckBalanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carddetails);

        String balance = getIntent().getStringExtra("balance");
        String lastUsage = getIntent().getStringExtra("lastUsage");
        String lastCharge = getIntent().getStringExtra("lastCharge");
        String cardId = getIntent().getStringExtra("cardId");

        TextView balanceTextView = findViewById(R.id.balance_amount);
        TextView lastUsageTextView = findViewById(R.id.last_usage_value);
        TextView lastChargeTextView = findViewById(R.id.last_charge_value);

        balanceTextView.setText(balance + " JOD");
        lastUsageTextView.setText(lastUsage);
        lastChargeTextView.setText(lastCharge);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        CardView chargeOnlineButton = findViewById(R.id.charge_online_button);
        chargeOnlineButton.setOnClickListener(v -> {
            Intent intent = new Intent(CheckBalanceActivity.this, RechargeCardActivity.class);
            intent.putExtra("cardId", cardId);
            startActivity(intent);
        });
    }
}
