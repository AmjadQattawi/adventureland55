package com.example.adventureland.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import com.example.adventureland.R;

public class GameFragment extends Fragment {
    ImageView favIcon,purpleIcon,backIcon,star1,star2,star3,star4,star5;
    private boolean isStarred1 = false,isStarred2=false,isStarred3=false,isStarred4=false,isStarred5=false; // To track the state

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favIcon=view.findViewById(R.id.loveIcon);
        purpleIcon=view.findViewById(R.id.lovePurpleIcon);
        backIcon=view.findViewById(R.id.backgame);
        star1=view.findViewById(R.id.star1);
        star2=view.findViewById(R.id.star2);
        star3=view.findViewById(R.id.star3);
        star4=view.findViewById(R.id.star4);
        star5=view.findViewById(R.id.star5);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //when click to fav icon change it's color to purple and when click again change it to be white
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favIcon.setVisibility(View.INVISIBLE);
                purpleIcon.setVisibility(View.VISIBLE);
            }
        });
        purpleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favIcon.setVisibility(View.VISIBLE);
                purpleIcon.setVisibility(View.INVISIBLE);
            }
        });

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStarred1){
                    star1.setImageResource(R.drawable.star);
                }else{
                    star1.setImageResource(R.drawable.filledstar);
                }
                isStarred1=!isStarred1; //toggle the state of star
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStarred2){
                    star2.setImageResource(R.drawable.star);
                }else{
                    star2.setImageResource(R.drawable.filledstar);
                }
                isStarred2=!isStarred2; //toggle the state of star
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStarred3){
                    star3.setImageResource(R.drawable.star);
                }else{
                    star3.setImageResource(R.drawable.filledstar);
                }
                isStarred3=!isStarred3; //toggle the state of star
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStarred4){
                    star4.setImageResource(R.drawable.star);
                }else{
                    star4.setImageResource(R.drawable.filledstar);
                }
                isStarred4=!isStarred4; //toggle the state of star
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStarred5){
                    star5.setImageResource(R.drawable.star);
                }else{
                    star5.setImageResource(R.drawable.filledstar);
                }
                isStarred5=!isStarred5; //toggle the state of star
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_design, container, false);
    }
}
