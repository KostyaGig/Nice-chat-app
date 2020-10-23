package ru.kostya.chatmeapp.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ru.kostya.chatmeapp.R;
import ru.kostya.chatmeapp.model.Friend;
import ru.kostya.chatmeapp.utils.FriendViewHolder;

public class FriendActivity extends AppCompatActivity {

    //Этот класс предназначен для отображения наших друзей,которым мы кинули заявку и которые эту заявку приняли,ведь только в этом случае
    //Создастся ветка Friends,чекай класс ViewFriendActivity,там мы юзали переменную currentState,чтобы знать приняли ли нас в друзья,или нет

    private RecyclerView friendRecView;
    private FirebaseRecyclerOptions<Friend> options;
    private FirebaseRecyclerAdapter<Friend, FriendViewHolder> friendAdapter;

    private DatabaseReference friendRef;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Friends");

        friendRecView = findViewById(R.id.friendRecView);
        friendRecView.setLayoutManager(new LinearLayoutManager(this));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        loadFriends("");
    }

    private void loadFriends(String s) {
        //Без понятия,что это,Где endAt
        Query query = friendRef.child(currentUser.getUid()).orderByChild("userName").endAt(s+"\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Friend>().setQuery(query,Friend.class).build();
        friendAdapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull Friend model) {


                holder.userName.setText(model.getUserName());
                holder.userProfession.setText(model.getProfession());

                Glide.with(FriendActivity.this).load(model.getProfileImage()).into(holder.profileImage);

            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(FriendActivity.this).inflate(R.layout.friend_item,parent,false);

                return new FriendViewHolder(view);
            }
        };

        friendAdapter.startListening();
        friendRecView.setAdapter(friendAdapter);

    }

}