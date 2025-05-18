package com.example.adventureland;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adventureland.fragments.PasswordChangeFragment;
import com.example.adventureland.fragments.VerifiactionFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ChangePasswordActivity extends AppCompatActivity implements VerifiactionFragment.VerificationListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String userId;
    private String userPhone;
    private VerifiactionFragment verificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        userId = firebaseAuth.getCurrentUser().getUid();

        loadUserPhoneAndStartVerification();
    }

    private void loadUserPhoneAndStartVerification() {
        usersRef.child(userId).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userPhone = snapshot.getValue(String.class);
                if (userPhone != null) {
                    showVerificationFragment();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "User phone not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChangePasswordActivity.this, "Failed to load phone", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showVerificationFragment() {
        verificationFragment = new VerifiactionFragment();
        verificationFragment.setVerificationListener(this);
        verificationFragment.setUserPhone(userPhone);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, verificationFragment)
                .commit();
    }

    @Override
    public void onVerificationSuccess() {
        getSupportFragmentManager().beginTransaction().remove(verificationFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        // استبدل هذا بالسلوك الذي تريده عند نجاح التحقق (مثلاً فتح واجهة تغيير كلمة المرور)
        // حالياً الكود يستدعي PasswordChangeFragment - تأكد من وجوده أو استبدله بواجهة مناسبة
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PasswordChangeFragment())
                .commit();
    }

    public void updatePassword(String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
