package com.example.adventureland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class FaqExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> questionList;
    private HashMap<String, List<String>> answerMap;

    public FaqExpandableListAdapter(Context context, List<String> questionList, HashMap<String, List<String>> answerMap) {
        this.context = context;
        this.questionList = questionList;
        this.answerMap = answerMap;
    }

    @Override
    public int getGroupCount() {
        return questionList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return answerMap.get(questionList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return questionList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return answerMap.get(questionList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Question View
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String question = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.faq_group_item, parent, false);
        }

        TextView tvQuestion = convertView.findViewById(R.id.tvQuestion);
        ImageView arrowIcon = convertView.findViewById(R.id.arrow_icon);

        tvQuestion.setText(question);

        // تدوير السهم حسب الفتح
        if (isExpanded) {
            arrowIcon.setRotation(180f);
        } else {
            arrowIcon.setRotation(0f);
        }

        return convertView;
    }

    // Answer View
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        String answer = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.faq_child_item, parent, false);
        }

        TextView tvAnswer = convertView.findViewById(R.id.tvAnswer);
        tvAnswer.setText(answer);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
