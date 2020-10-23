package ru.kostya.chatmeapp.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ru.kostya.chatmeapp.R;
import ru.kostya.chatmeapp.model.User;
import ru.kostya.chatmeapp.utils.FindUserViewHolder;

public class FindFriendActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private RecyclerView findFriendRecView;
    private FirebaseRecyclerOptions<User> options;
    private FirebaseRecyclerAdapter<User, FindUserViewHolder> findUserAdapter;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        initializeProgressDialog();
        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar));


        findFriendRecView = findViewById(R.id.findFriendRecView);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        //указываем пустое имя,значит отобразятся все юзеры
        findFriend("");
    }

    //Name - имя по котрому мы будем искать юзера
    private void findFriend(String userName) {
        pd.show();

        Query query = userRef.orderByChild("Name").startAt(userName);
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query,User.class).build();
        findUserAdapter = new FirebaseRecyclerAdapter<User, FindUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindUserViewHolder holder, final int position, @NonNull User model) {
                //СЕЙЧАС Я НЕ БУДУ ЭТО ЮЗАТЬ,Т.К НЕ ХОЧУ РЕГАТЬ НОВЫЙ АКК
                //Если Наш id НЕ совпадает с id из реферец,то...
//                if (!currentUser.getUid().equals(getRef(position).getKey().toString())){
//                    Glide.with(FindFriendActivity.this).load(model.getProfileImage()).into(holder.profileImage);
//                    holder.userName.setText(model.getName());
//                    holder.userProfession.setText(model.getProfession());
//                }else {
//                    //Если совпадает скрываем итем
//                    holder.itemView.setVisibility(View.GONE);
//                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
//                }

                Glide.with(FindFriendActivity.this).load(model.getProfileImage()).into(holder.profileImage);
                    holder.userName.setText(model.getName());
                    holder.userProfession.setText(model.getProfession());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent friendActivity = new Intent(FindFriendActivity.this, ViewFriendActivity.class);
                            //По нажатию на какого либо юзера мы передаем ключ этого юзера,тем саамым мы можем проводить самы разнообразные манипуляции в другом активити
                            //Например теперь мы можем получить все данные,имя,профессия,город и т.д
                            friendActivity.putExtra("userKey",getRef(position).getKey());
                            startActivity(friendActivity);
                        }
                    });
            }

            @NonNull
            @Override
            public FindUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(FindFriendActivity.this).inflate(R.layout.find_friend_item,parent,false);

                return new FindUserViewHolder(view);
            }
        };

        findFriendRecView.setLayoutManager(new LinearLayoutManager(this));
        findUserAdapter.startListening();
        findFriendRecView.setAdapter(findUserAdapter);

        pd.dismiss();
    }

    private void initializeProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста подождите...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_tem);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String userName) {
                findFriend(userName);
                return true;
            }
        });
        return true;

    }
}