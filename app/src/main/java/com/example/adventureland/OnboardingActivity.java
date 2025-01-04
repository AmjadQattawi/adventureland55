package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageButton btnNext, btnBack;
    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
//        TabLayout tabLayout = findViewById(R.id.tabLayout);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        btnSkip = findViewById(R.id.btnSkip);

        // Create onboarding items
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(

                R.drawable.onboarding,
                "Exclusive Offers!",
                "Discover special offers, discounts, and promotions right inside the app!"

        ));
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding2,
                "Games & Rewards",
                "Collect points and unlock rewards as you play and explore the park!"
        ));
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding3,
                "Track & Top Up Your Game Card!",
                "Discover special offers, discounts, and promotions right inside the app!"
        ));

        // Set up adapter
        OnboardingAdapter adapter = new OnboardingAdapter(this, onboardingItems);
        viewPager.setAdapter(adapter);

//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        // Next button click
        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < onboardingItems.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Back button click
        btnBack.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        // Skip button click
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Update visibility of buttons based on the current page
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                btnBack.setVisibility(position == 0 ? ImageButton.INVISIBLE : ImageButton.VISIBLE);
            }
        });
    }
}
