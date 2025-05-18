package com.example.adventureland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.*;

import java.util.concurrent.TimeUnit;

public class AccountActivity extends AppCompatActivity {

    private EditText editTextDisplayName, editTextPhone;
    private AppCompatButton buttonUpdate, buttonChangePassword;
    private TextView textDeleteAccount;
    private ImageView backMore;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String userKey;

    private String currentPhoneFull; // الرقم القديم مع +962
    private String verificationId;

    private String originalName = "";
    private String originalPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        editTextDisplayName = findViewById(R.id.editTextDisplayName);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        textDeleteAccount = findViewById(R.id.textDeleteAccount);
        backMore = findViewById(R.id.back_More);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        backMore.setOnClickListener(view -> finish());

        loadUserData();

        buttonUpdate.setOnClickListener(view -> tryUpdateUserDataWithVerification());

        buttonChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        textDeleteAccount.setOnClickListener(view -> {
            Toast.makeText(AccountActivity.this, "Account deletion requested", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userKey = sharedPreferences.getString("userKey", null);

        if (userKey != null) {
            usersRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);

                        if (name != null) {
                            editTextDisplayName.setText(name);
                            originalName = name;
                        }
                        if (phone != null) {
                            currentPhoneFull = phone;
                            if (phone.startsWith("+962")) {
                                phone = phone.substring(4);
                            }
                            editTextPhone.setText(phone);
                            originalPhone = phone;
                        }
                    } else {
                        Toast.makeText(AccountActivity.this, "User data not found, please login again", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AccountActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not found, please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void tryUpdateUserDataWithVerification() {
        String newName = editTextDisplayName.getText().toString().trim();
        String newPhone = editTextPhone.getText().toString().trim();

        if (newName.isEmpty() && newPhone.isEmpty()) {
            Toast.makeText(this, "Please fill at least one field to update", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPhone.isEmpty()) {
            if (newPhone.length() != 9 || !(newPhone.startsWith("77") || newPhone.startsWith("78") || newPhone.startsWith("79"))) {
                Toast.makeText(this, "Phone must be 9 digits starting with 77, 78 or 79", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (userKey == null) {
            Toast.makeText(this, "User key missing, please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (!newPhone.isEmpty() && !newPhone.equals(originalPhone)) {
            if (currentPhoneFull == null) {
                Toast.makeText(this, "Current phone number not found, can't verify", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVerificationCodeForUpdate(currentPhoneFull, newName, newPhone);
            return;
        }

        boolean changedName = !newName.isEmpty() && !newName.equals(originalName);
        if (changedName) {
            usersRef.child(userKey).child("name").setValue(newName);
            originalName = newName;
        }

        Toast.makeText(this, changedName ? "Name updated successfully" : "No changes detected", Toast.LENGTH_SHORT).show();
    }

    private void sendVerificationCodeForUpdate(String phoneNumber, String newName, String newPhone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                updateDataAfterVerification(newName, newPhone);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(AccountActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(s, token);
                                verificationId = s;
                                showOtpDialog(newName, newPhone);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showOtpDialog(String newName, String newPhone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OTP");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText otpInput = new EditText(this);
        otpInput.setHint("OTP Code");
        layout.addView(otpInput);

        builder.setView(layout);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String code = otpInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtpAndUpdate(code, newName, newPhone);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void verifyOtpAndUpdate(String code, String newName, String newPhone) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateDataAfterVerification(newName, newPhone);
            } else {
                Toast.makeText(this, "OTP Verification failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDataAfterVerification(String newName, String newPhone) {
        boolean updated = false;
        if (!newName.isEmpty()) {
            usersRef.child(userKey).child("name").setValue(newName);
            originalName = newName;
            updated = true;
        }

        if (!newPhone.isEmpty()) {
            String fullNewPhone = "+962" + newPhone;
            usersRef.child(userKey).child("phone").setValue(fullNewPhone);
            currentPhoneFull = fullNewPhone;
            originalPhone = newPhone;
            updated = true;

            // تحديث SharedPreferences برقم الهاتف الجديد
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phone", newPhone);
            editor.apply();
        }

        Toast.makeText(this, updated ? "User data updated successfully" : "No changes detected", Toast.LENGTH_SHORT).show();
    }
}
