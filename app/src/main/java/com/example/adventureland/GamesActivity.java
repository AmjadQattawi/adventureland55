package com.example.adventureland;



import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class GamesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private List<Game> gameList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games);

        ImageView backArrow = findViewById(R.id.back_games);
        backArrow.setOnClickListener(v -> finish());

        searchView = findViewById(R.id.games_sv_searchGames);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.games_rv_games);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // **قائمة الألعاب - قيم التقييم الأولية = -1 (يعني لم يتم تحميل التقييم بعد)**
        gameList = new ArrayList<>();
        gameList.add(new Game("Bumper Cars", 4, -1f, R.drawable.bumpercars,
                "Drive small, electric-powered cars equipped with rubber bumpers and collide safely with other players in a controlled arena. This game is perfect for kids and adults looking for friendly competition and lots of laughs.", 1.500));
        gameList.add(new Game("Ferris Wheel", 4, -1f, R.drawable.ferriswheel,
                "Enjoy a relaxing ride on a giant wheel with enclosed seats, offering a bird's-eye view of the amusement park and its surroundings. It's ideal for families and anyone seeking a peaceful yet memorable experience.", 1.250));
        gameList.add(new Game("Swing Ride", 8, -1f, R.drawable.swingride,
                "Feel the breeze as you soar through the air in a circular motion on this exciting swing ride. It’s thrilling yet comfortable, perfect for those seeking a mix of adrenaline and joy.", 1.250));
        gameList.add(new Game("Roller Coaster", 10, -1f, R.drawable.rollercoaster,
                "Experience the ultimate thrill with high-speed loops, drops, and turns on this exhilarating ride. Perfect for adrenaline lovers looking for a heart-racing adventure.", 2.000));
        gameList.add(new Game("Carousel", 3, -1f, R.drawable.carousel,
                "Ride beautifully crafted, brightly colored horses as they spin gently to cheerful music. This classic ride is perfect for younger children and those who love nostalgic attractions.", 1.250));
        gameList.add(new Game("Haunted House", 8, -1f, R.drawable.hauntedhouse,
                "Enter a spooky, thrilling adventure filled with eerie sounds and mysterious surprises. Ideal for those who enjoy a good scare.", 2.500));
        gameList.add(new Game("Teacup Ride", 4, -1f, R.drawable.teacup,
                "Spin around in colorful teacups that you can control, offering a fun, lighthearted ride for families and younger children.", 1.250));
        gameList.add(new Game("Drop Tower", 12, -1f, R.drawable.droptower,
                "Experience the heart-stopping sensation of a sudden free fall from great heights. A must-try for the bravest thrill-seekers.", 2.500));

        adapter = new GameAdapter(this, gameList, GameAdapter.TYPE_FULL);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        // **هنا نبدأ تحميل التقييمات من Firebase لكل لعبة**
        loadRatingsFromFirebase();
    }

    private void searchList(String newText) {
        List<Game> dataSearchList = new ArrayList<>();
        for (Game data : gameList) {
            if (data.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()) {
            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }
    }

    // دالة لتحميل التقييمات من Firebase وتحديث قائمة الألعاب
    private void loadRatingsFromFirebase() {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings");

        // عداد لعدد الألعاب التي تم تحميل تقييمها
        final int totalGames = gameList.size();
        final int[] loadedCount = {0};

        for (Game game : gameList) {
            String title = game.getTitle();

            ratingsRef.child(title).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalRating = 0;
                    int count = 0;
                    for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                        Double ratingValue = ratingSnapshot.getValue(Double.class);
                        if (ratingValue != null) {
                            totalRating += ratingValue;
                            count++;
                        }
                    }
                    float averageRating = count > 0 ? (float) (totalRating / count) : 0f;
                    game.setRating(averageRating);

                    loadedCount[0]++;
                    if (loadedCount[0] == totalGames) {
                        // تم تحميل كل التقييمات - حدث الـ adapter
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // لو حدث خطأ، على الأقل نزيد العد حتى لا يتوقف التحديث
                    loadedCount[0]++;
                    if (loadedCount[0] == totalGames) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
