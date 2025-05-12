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
        questionList.add("How can I recharge my play card?");
        questionList.add("What offers are available for individual users?");
        questionList.add("Can the card be used for multiple children?");

        answerMap.put(questionList.get(0), Collections.singletonList("The play card is a prepaid card that gives you access to games and services within the park."));
        answerMap.put(questionList.get(1), Collections.singletonList("You can recharge your play card through the app or at the park counters."));
        answerMap.put(questionList.get(2), Collections.singletonList("Seasonal and weekend offers are available for individual users."));
        answerMap.put(questionList.get(3), Collections.singletonList("The card is intended for a single child and cannot be shared."));
    }

}
