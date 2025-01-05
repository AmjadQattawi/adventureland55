package com.example.adventureland.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.adventureland.Game;
import com.example.adventureland.GameAdapter;
import com.example.adventureland.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private ImageView backIcon;
    private RecyclerView recyclerView;
    private GameAdapter gameAdapter;
    private List<Game> favoriteGames = new ArrayList<>();
    private DatabaseReference databaseFavorites;
    private FirebaseAuth mAuth;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        databaseFavorites = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);

        recyclerView = view.findViewById(R.id.recyclerFav);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        gameAdapter = new GameAdapter(getContext(), favoriteGames, GameAdapter.TYPE_FULL);
        recyclerView.setAdapter(gameAdapter);

        backIcon = view.findViewById(R.id.back_Fav);
        backIcon.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        loadFavoriteGames();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite, container, false);
    }

    private void loadFavoriteGames() {
        databaseFavorites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteGames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Game game = snapshot.getValue(Game.class);
                    favoriteGames.add(game);
                }
                gameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load favorite games", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
