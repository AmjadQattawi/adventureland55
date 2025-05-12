package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckBalanceActivity extends AppCompatActivity {

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carddetails);

        // ✅ جلب المستخدم الحالي
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // ⬇️ جلب البيانات المرسلة
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
        CardView cardStatementButton = findViewById(R.id.card_statement_button);

        // ✅ التحقق من تسجيل الدخول قبل السماح باستخدام Charge Online
        chargeOnlineButton.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(CheckBalanceActivity.this, RechargeCardActivity.class);
                intent.putExtra("cardId", cardId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please log in to use this feature.", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ التحقق من تسجيل الدخول قبل السماح باستخدام Card Statement
        cardStatementButton.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(CheckBalanceActivity.this, CardStatementActivity.class);
                intent.putExtra("cardId", cardId);
                intent.putExtra("balance", balance);
                intent.putExtra("lastUsage", lastUsage);
                intent.putExtra("lastCharge", lastCharge);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please log in to use this feature.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
