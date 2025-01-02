package com.example.adventureland.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import com.example.adventureland.R;

public class GameFragment extends Fragment {
    ImageView favIcon, purpleIcon, backIcon;
    private boolean isFavorite = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favIcon = view.findViewById(R.id.loveIcon);
        purpleIcon = view.findViewById(R.id.lovePurpleIcon);
        backIcon = view.findViewById(R.id.backgame);

        updateFavoriteIcons();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = true; //
                updateFavoriteIcons();
            }
        });

        purpleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = false;
                updateFavoriteIcons();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_design, container, false);
    }

    private void updateFavoriteIcons() {
        if (isFavorite) {
            favIcon.setVisibility(View.INVISIBLE);
            purpleIcon.setVisibility(View.VISIBLE);
        } else {
            favIcon.setVisibility(View.VISIBLE);
            purpleIcon.setVisibility(View.INVISIBLE);
        }
    }
}