package com.example.adventureland;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FAQactivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private FaqExpandableListAdapter adapter;
    private List<String> questionList;
    private HashMap<String, List<String>> answerMap;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        expandableListView = findViewById(R.id.expandableListView);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupFaqData();

        adapter = new FaqExpandableListAdapter(this, questionList, answerMap);
        expandableListView.setAdapter(adapter);
    }

    private void setupFaqData() {
        questionList = new ArrayList<>();
        answerMap = new HashMap<>();

        questionList.add("What is the play card?");
        questionList.add("Can I add more than one card to my account?");
        questionList.add("How do I earn points?");
        questionList.add("Can I celebrate my birthday at the Adventure Land?");

        answerMap.put(questionList.get(0), Collections.singletonList("The play card is a prepaid card that allows you to access and enjoy games and other services within the app."));
        answerMap.put(questionList.get(1), Collections.singletonList("Yes, you can manage multiple cards in your account. Each card will display its own balance and transaction history."));
        answerMap.put(questionList.get(2), Collections.singletonList("You can earn points through the following activities:\n" +
                "\n" +
                "Daily login to your account\n" +
                "\n" +
                "Rating games in the app\n" +
                "\n" +
                "Recharging your play card through the app\n" +
                "\n"));
        answerMap.put(questionList.get(3), Collections.singletonList("Yes! Adventure Land offers special Birthday Packages that include games, decorations, and exclusive discounts. You can check the “Birthday Offers” section in the app or contact our team to book your party.\n" +
                "\n"));
    }

}
