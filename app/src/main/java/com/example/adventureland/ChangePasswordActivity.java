package com.example.adventureland;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private CardView updatePasswordButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private String userKey;
    private String verificationId;
    private String userPhone;  // رقم الهاتف المخزن، للـ OTP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextCurrentPassword = findViewById(R.id.current_password_input);
        editTextNewPassword = findViewById(R.id.new_password_input);
        editTextConfirmPassword = findViewById(R.id.confirm_password_input);
        updatePasswordButton = findViewById(R.id.update_password_button);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userKey = sp.getString("userKey", null);
        userPhone = sp.getString("phone", null);

        updatePasswordButton.setOnClickListener(v -> startPasswordChangeProcess());
    }

    private void startPasswordChangeProcess() {
        String currentPass = editTextCurrentPassword.getText().toString().trim();
        String newPass = editTextNewPassword.getText().toString().trim();
        String confirmPass = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "User not authenticated, please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (userPhone == null) {
            Toast.makeText(this, "User phone not found, please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 1. إعادة التحقق من كلمة السر القديمة
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPass);
        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 2. إرسال رمز OTP
                sendVerificationCode("+962" + userPhone);
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // تحقق تلقائي (اختياري) - يمكن تخطيه هنا
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ChangePasswordActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verifId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(verifId, token);
                                verificationId = verifId;
                                showOtpDialog();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showOtpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OTP");

        EditText otpInput = new EditText(this);
        otpInput.setHint("OTP Code");
        builder.setView(otpInput);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String code = otpInput.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtpAndChangePassword(code);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void verifyOtpAndChangePassword(String code) {
        if (verificationId == null) {
            Toast.makeText(this, "Verification ID not found, please request code again.", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // عند التحقق الناجح، نحدث كلمة السر
                updatePassword();
            } else {
                Toast.makeText(this, "Invalid OTP code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword() {
        String newPass = editTextNewPassword.getText().toString().trim();

        currentUser.updatePassword(newPass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // تحديث كلمة السر في قاعدة بيانات Realtime Database
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                if (userKey != null) {
                    usersRef.child(userKey).child("password").setValue(newPass);
                }
                Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
