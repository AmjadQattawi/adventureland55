package com.example.adventureland;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FAQactivity extends AppCompatActivity {

    private ConstraintLayout[] faqItems;
    private TextView[] answers;
    private ImageView[] arrows;

    private boolean[] isExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq);

        faqItems = new ConstraintLayout[5];
        answers = new TextView[5];
        arrows = new ImageView[5];
        isExpanded = new boolean[5];

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        setupFAQItem(0, R.id.faq_item_1, R.id.answer_1, R.id.arrow_1);
        setupFAQItem(1, R.id.faq_item_2, R.id.answer_2, R.id.arrow_2);
        setupFAQItem(2, R.id.faq_item_3, R.id.answer_3, R.id.arrow_3);
        setupFAQItem(3, R.id.faq_item_4, R.id.answer_4, R.id.arrow_4);
        setupFAQItem(4, R.id.faq_item_5, R.id.answer_5, R.id.arrow_5);
    }


    private void setupFAQItem(final int index, int faqItemId, int answerId, int arrowId) {
        faqItems[index] = findViewById(faqItemId);
        answers[index] = findViewById(answerId);
        arrows[index] = findViewById(arrowId);
        isExpanded[index] = false;

        faqItems[index].setOnClickListener(v -> toggleFAQ(index));
    }

    private void toggleFAQ(int index) {
        if (isExpanded[index]) {

            answers[index].setVisibility(View.GONE);
            rotateArrow(arrows[index], 180f, 0f);
        } else {
            answers[index].setVisibility(View.VISIBLE);
            rotateArrow(arrows[index], 0f, 180f);
        }

        isExpanded[index] = !isExpanded[index];
    }


    private void rotateArrow(ImageView arrow, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        arrow.startAnimation(rotateAnimation);
    }
}
