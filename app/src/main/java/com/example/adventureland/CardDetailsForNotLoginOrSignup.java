package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CardDetailsForNotLoginOrSignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carddetailsfornotloginorsignup);

        // جلب البيانات
        String balance = getIntent().getStringExtra("balance");
        String lastUsage = getIntent().getStringExtra("lastUsage");
        String lastCharge = getIntent().getStringExtra("lastCharge");

        TextView balanceTextView = findViewById(R.id.balance_amount);
        TextView lastUsageTextView = findViewById(R.id.last_usage_value);
        TextView lastChargeTextView = findViewById(R.id.last_charge_value);
        ImageView backButton = findViewById(R.id.back_button);

        balanceTextView.setText(balance + " JOD");
        lastUsageTextView.setText(lastUsage);
        lastChargeTextView.setText(lastCharge);

        backButton.setOnClickListener(v -> finish());
    }
}
