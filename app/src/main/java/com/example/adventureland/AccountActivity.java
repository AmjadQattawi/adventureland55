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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
            new AlertDialog.Builder(AccountActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> promptPasswordAndDeleteAccount())
                    .setNegativeButton("No", null)
                    .show();
        });

    }



    private void promptPasswordAndDeleteAccount() {
        EditText passwordInput = new EditText(this);
        passwordInput.setHint("Enter your password");

        new AlertDialog.Builder(AccountActivity.this)
                .setTitle("Confirm Password")
                .setView(passwordInput)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String password = passwordInput.getText().toString().trim();
                    if (password.isEmpty()) {
                        Toast.makeText(AccountActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reauthenticateAndDeleteAccount(password);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void reauthenticateAndDeleteAccount(String password) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (userKey != null) {
                    usersRef.child(userKey).removeValue().addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            user.delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    Toast.makeText(AccountActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                                    // امسح بيانات SharedPreferences بعد الحذف
                                    SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                    sp.edit().clear().apply();

                                    // ارجع لصفحة تسجيل الدخول ونظف التاريخ
                                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(AccountActivity.this, "Failed to delete account: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(AccountActivity.this, "Failed to delete user data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(AccountActivity.this, "User key not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AccountActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
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
            sendVerificationCodeForUpdate("+962" + newPhone, newName, newPhone);
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

            // تحديث رقم الهاتف في Firebase Authentication
            updatePhoneNumberInAuth(fullNewPhone);
        }

        Toast.makeText(this, updated ? "User data updated successfully" : "No changes detected", Toast.LENGTH_SHORT).show();
    }

    private void updatePhoneNumberInAuth(String fullNewPhone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(fullNewPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.updatePhoneNumber(credential)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AccountActivity.this, "Phone number updated in Authentication", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(AccountActivity.this, "Failed to update phone in Authentication: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(AccountActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // عادة هنا يمكن تطلب من المستخدم إدخال الرمز، لكن لأنك تستخدم تحديث مباشر يمكن تخطيه
                        // أو يمكن بناء واجهة OTP إذا أردت تحكم يدوي
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }




}
