package com.example.adventureland.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.example.adventureland.R;

public class HomeFragment extends Fragment {
    private ImageView gameIcon,cardIcon,game1;
    private TextView seeAll;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameIcon=view.findViewById(R.id.gamesIcon);
        cardIcon=view.findViewById(R.id.cardIcon);
        seeAll=view.findViewById(R.id.seeAll_txt);
        game1=view.findViewById(R.id.game1);

        gameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutContainer, new GamesFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        cardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutContainer, new CardFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutContainer, new GamesFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutContainer, new GameFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }
}
