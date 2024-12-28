package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.adventureland.fragments.HomeFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignup;
    private ImageView googleLogin, facebookLogin, twitterLogin, DrawerIcon;
    private DrawerLayout drawerLayout;
    private ImageView closeDrawer;
    private LinearLayout checkBalanceSection, aboutSection;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvSignup = findViewById(R.id.tv_signup);
        googleLogin = findViewById(R.id.google_login);
        facebookLogin = findViewById(R.id.facebook_login);
        twitterLogin = findViewById(R.id.twitter_login);
        DrawerIcon = findViewById(R.id.drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        closeDrawer = findViewById(R.id.close_drawer);
        checkBalanceSection = findViewById(R.id.check_balance_section);
        aboutSection = findViewById(R.id.about_section);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Set Drawer Actions
        setupDrawerActions();

        // Set Login Button Action
        btnLogin.setOnClickListener(v -> handleLogin());

        // Set Forgot Password Action
        tvForgotPassword.setOnClickListener(v -> openForgotPasswordDialog());


    }

    private void setupDrawerActions() {
        // Open the drawer when DrawerIcon is clicked
        DrawerIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Close the drawer when closeDrawer is clicked
        closeDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Navigate to BalanceActivity when "Check your card balance" is clicked
        checkBalanceSection.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, BalanceActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after navigation
        });

        // Navigate to AboutActivity when "About" is clicked
        aboutSection.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after navigation
        });

        // Navigate to SignupActivity
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Social Media Logins
        googleLogin.setOnClickListener(v -> Toast.makeText(this, "Google Login Clicked", Toast.LENGTH_SHORT).show());
        facebookLogin.setOnClickListener(v -> Toast.makeText(this, "Facebook Login Clicked", Toast.LENGTH_SHORT).show());
        twitterLogin.setOnClickListener(v -> Toast.makeText(this, "Twitter Login Clicked", Toast.LENGTH_SHORT).show());
    }



    private void handleLogin() {
        String phone = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            etPhoneNumber.setError("Please enter your phone number");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter your password");
            return;
        }

        String fullPhoneNumber = "+962" + phone;

        // Check user data in Realtime Database
        usersRef.orderByChild("phone").equalTo(fullPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);

                        if (dbPassword != null && dbPassword.equals(password)) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
////


                            Intent i=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(i);


////
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openForgotPasswordDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        final EditText input = new EditText(this);
        input.setHint("Enter your phone number");
        builder.setView(input);

        builder.setPositiveButton("Next", (dialog, which) -> {
            String phone = input.getText().toString().trim();

            if (TextUtils.isEmpty(phone) || phone.length() != 9) {
                Toast.makeText(this, "Enter a valid 9-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullPhoneNumber = "+962" + phone;

            usersRef.orderByChild("phone").equalTo(fullPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        sendVerificationCode(fullPhoneNumber);
                        showResetPasswordDialog(fullPhoneNumber);
                    } else {
                        Toast.makeText(LoginActivity.this, "Phone number not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showResetPasswordDialog(String phoneNumber) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText otpInput = new EditText(this);
        otpInput.setHint("Enter OTP");
        layout.addView(otpInput);

        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint("Enter New Password");
        layout.addView(newPasswordInput);

        builder.setView(layout);

        builder.setPositiveButton("Reset", (dialog, which) -> {
            String otp = otpInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (TextUtils.isEmpty(otp) || TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(otp, phoneNumber, newPassword);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
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
                                Toast.makeText(LoginActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(LoginActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(s, token);
                                verificationId = s;
                                Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void verifyCode(String code, String phoneNumber, String newPassword) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                usersRef.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            userSnapshot.getRef().child("password").setValue(newPassword);
                            Toast.makeText(LoginActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }





}
