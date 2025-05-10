package com.example.adventureland;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentDetailActivity extends AppCompatActivity {

    private String cardId;
    private double originalAmount;

    private EditText cardNumberInput, cvvInput;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_detalis);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cardId = getIntent().getStringExtra("cardId");
        if (cardId == null || cardId.isEmpty()) {
            Toast.makeText(this, "Missing card ID. Cannot continue.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String amountStr = getIntent().getStringExtra("amount");
        originalAmount = amountStr != null ? Double.parseDouble(amountStr) : 0.0;

        TextView totalAmountText = findViewById(R.id.total_amount_value);
        totalAmountText.setText(originalAmount + " JOD");

        Button nextButton = findViewById(R.id.next_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        cardNumberInput = findViewById(R.id.card_number_input);
        cvvInput = findViewById(R.id.cvv_input);

        setupCardNumberFormatter();

        Spinner monthSpinner = findViewById(R.id.expiration_month_spinner);
        Spinner yearSpinner = findViewById(R.id.expiration_year_spinner);
        RelativeLayout monthContainer = findViewById(R.id.expiration_month_container);
        RelativeLayout yearContainer = findViewById(R.id.expiration_year_container);
        TextView monthText = findViewById(R.id.expiration_month_text);
        TextView yearText = findViewById(R.id.expiration_year_text);

        monthContainer.setOnClickListener(v -> monthSpinner.performClick());
        yearContainer.setOnClickListener(v -> yearSpinner.performClick());

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"2025", "2026", "2027", "2028", "2029", "2030"});
        yearSpinner.setAdapter(yearAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthText.setText(parent.getItemAtPosition(position).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearText.setText(parent.getItemAtPosition(position).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        nextButton.setOnClickListener(v -> {
            if (!validateInputs()) return;
            processPayment();
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    private boolean validateInputs() {
        String cardNumber = cardNumberInput.getText().toString().replace("-", "").trim();
        String cvv = cvvInput.getText().toString().trim();
        String selectedMonth = ((TextView) findViewById(R.id.expiration_month_text)).getText().toString().trim();
        String selectedYear = ((TextView) findViewById(R.id.expiration_year_text)).getText().toString().trim();

        if (cardNumber.isEmpty() || selectedMonth.equals("Select Month") || selectedYear.equals("Select Year") || cvv.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cardNumber.length() != 16) {
            Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cvv.length() != 3) {
            Toast.makeText(this, "CVV must be exactly 3 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
                String digitsOnly = s.toString().replaceAll("-", "").replaceAll(" ", "");
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

    private void processPayment() {
        double addedBalance;
        if (originalAmount < 20) addedBalance = originalAmount;
        else if (originalAmount == 20) addedBalance = 60;
        else if (originalAmount == 30) addedBalance = 130;
        else if (originalAmount == 50) addedBalance = 250;
        else if (originalAmount == 75) addedBalance = 400;
        else if (originalAmount == 100) addedBalance = 600;
        else {
            Toast.makeText(this, "Invalid offer amount", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference cardRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("cards").child(cardId);

        cardRef.child("balance").get().addOnSuccessListener(snapshot -> {
            double currentBalance = 0;
            if (snapshot.exists()) {
                try {
                    currentBalance = Double.parseDouble(snapshot.getValue().toString());
                } catch (Exception ignored) {}
            }

            double updatedBalance = currentBalance + addedBalance;
            cardRef.child("balance").setValue(updatedBalance);
            String currentTime = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());
            cardRef.child("lastCharge").setValue(currentTime);
            cardRef.child("lastUsage").setValue(currentTime);


            final long[] rewardPoints = {0};
            if (originalAmount == 50) rewardPoints[0] = 100;
            else if (originalAmount == 75) rewardPoints[0] = 150;
            else if (originalAmount == 100) rewardPoints[0] = 220;
            else if (originalAmount == 20 || originalAmount == 30) rewardPoints[0] = 30;

            if (rewardPoints[0] > 0) {
                DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("points");
                pointsRef.get().addOnSuccessListener(snap -> {
                    long currentPoints = snap.exists() ? snap.getValue(Long.class) : 0;
                    pointsRef.setValue(currentPoints + rewardPoints[0]);

                    DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("transactions").push();
                    transactionRef.setValue(new Transaction("earned", "Recharge Reward", (int) rewardPoints[0], getCurrentTimestamp()));
                });
            }


            showSuccessDialog();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to retrieve current balance.", Toast.LENGTH_SHORT).show();
        });
    }
    private String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("Your card was recharged successfully!");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            Intent intent = new Intent(PaymentDetailActivity.this, MainActivity.class);
            intent.putExtra("navigateTo", "card");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        builder.show();
    }
}
