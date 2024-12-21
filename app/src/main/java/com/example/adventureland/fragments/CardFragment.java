package com.example.adventureland.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.adventureland.CheckBalanceActivity;
import com.example.adventureland.LoginActivity;
import com.example.adventureland.MainActivity;
import com.example.adventureland.R;

public class CardFragment extends Fragment {
    private ImageView backIcon,checkBalanceIcon;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backIcon=view.findViewById(R.id.back_card);
        checkBalanceIcon=view.findViewById(R.id.checkBalancrIcon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        checkBalanceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), CheckBalanceActivity.class);
                startActivity(i);
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card, container, false);
    }
}
