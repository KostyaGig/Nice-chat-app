package ru.kostya.chatmeapp.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ru.kostya.chatmeapp.R;

//ИЗ ЗА ЭТОГО КЛЛАССА Я ПРЕКРАТИЛ РАЗРАБОТКУ ЭТОГО ПРИЛОЖЕНИЯ,НО В ЭТОМ ПРИЛОЖЕНИИ ВСЕ РАВНО ЕСТЬ МНОГО ЧЕГО ИНТЕРЕСНОГО
public class ViewFriendActivity extends AppCompatActivity {


    //id  юзера на которого мы нажали в findfriendactivity
    private String userId;
    //Переменная для тогоч тобы знать нажали ли мы на согласиться или нет,если нет,то чекай if на 86 строчке кода
    private String currentState = "nothing_happen";


    private String profession;
    //Request с англ. - Запрос,тоесть requestRef - мы создаем ссылку для запросов в друзья
    private DatabaseReference userRef,requestRef,friendRef;
    private FirebaseUser currentUser;

    private ImageView profileImage;
    private TextView userName,userLocation,userProfession;
    private Button acceptBtn,rejectBtn;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        init();
        initializeProgressDialog();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        userId = getIntent().getStringExtra("userKey");

        loadUser();

        //Провекра юзера на существование Existance от слова exist - существование
        checkUserExistance();
    }

    private void init() {
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        requestRef = FirebaseDatabase.getInstance().getReference("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference("Friends");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userName = findViewById(R.id.userName);
        userLocation = findViewById(R.id.userLocation);
        userProfession = findViewById(R.id.userProfession);

        //C английского (согласиться),тоесть добавить
        acceptBtn = findViewById(R.id.acceptBtn);
        //С АНГЛИЙСКОГО отклонить
        rejectBtn = findViewById(R.id.rejectBtn);

        profileImage = findViewById(R.id.profileImage);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptAction();
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFriend();
            }
        });


    }

    private void deleteFriend() {

        if (currentState.equals("friend")){
            friendRef.child(currentUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        friendRef.child(userId).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    currentState = "nothing_happen";
                                    acceptBtn.setText("Написать сообщение");
                                    rejectBtn.setVisibility(View.GONE);
                                    Toast.makeText(ViewFriendActivity.this, "Ваш друг с uid : " + userId + " а также вы у него были успешно удалены", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ViewFriendActivity.this, "При удалении друга произошла ошибка : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        if (currentState.equals("he_send_pending")){
            HashMap<String,Object> map = new HashMap<>();
            map.put("status","decline");
            requestRef.child(userId).child(currentUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "С вами отказались дружить", Toast.LENGTH_SHORT).show();
                        currentState = "he_sent_decline";
                        acceptBtn.setVisibility(View.GONE);
                        rejectBtn.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    private void checkUserExistance() {
        friendRef.child(currentUser.getUid()).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
//                    Если существует
                    currentState = "friend";
                    acceptBtn.setText("Отправить сообщение");
                    rejectBtn.setText("Удалить из друзей");
                    rejectBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendRef.child(userId).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
//                    Если существует
                    currentState = "friend";
                    acceptBtn.setText("Отправить сообщение");
                    rejectBtn.setText("Удалить из друзей");
                    rejectBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestRef.child(currentUser.getUid()).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("status").getValue().toString().equals("pending")){
                        currentState = "i_sent_pending";
                        acceptBtn.setText("Отменить запрос на дружбу");
                        rejectBtn.setVisibility(View.GONE);
                    }

                    if (snapshot.child("status").getValue().toString().equals("decline")){
                        currentState = "i_sent_decline";
                        acceptBtn.setText("Отменить запрос на дружбу");
                        rejectBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(userId).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("status").getValue().toString().equals("pending"));{
                        currentState = "he_sent_pending";
                        acceptBtn.setText("Принять запрос на дружбу");
                        rejectBtn.setText("Отклонить запрос на дружбу");
                        rejectBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (currentState.equals("nothing_happen")){
            currentState = "nothing_happen";
            acceptBtn.setText("Отправить запрос на дружбу");
            rejectBtn.setVisibility(View.GONE);
        }
    }

    private void acceptAction() {
        //Описал вначале данной активити
        if (currentState.equals("nothing_happen")){
            //Создаем ветку наш uid.child(uid юзера,который был нажат в recyclerview)
            //В этой ссылке будут храниться uid всех юзеров,которым мы предложили дружить
            HashMap<String,Object> map = new HashMap<>();
            //Pending с англ - В ожидании
            map.put("status","pending");

            requestRef.child(currentUser.getUid()).child(userId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "Ваше предложение дружить было отправлено!", Toast.LENGTH_SHORT).show();
                        rejectBtn.setVisibility(View.GONE);
                        currentState = "i_sent_pending";
                        acceptBtn.setText("Отменить запрос на дружбу");
                    }    else {
                        Toast.makeText(ViewFriendActivity.this, "Ваше предложение дружить не было отправлено: " +task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //decline с англ - отказаться
        //Если состояние равно я отправил запрос или я отказываюсь добавлять,
        // то удаляем из наших запросов uid юзера на которого мы нажимали в recyclerview
        if (currentState.equals("i_sent_pending") || currentState.equals("i_sent_decline")){
            requestRef.child(currentUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "Вы отменили заявку в друзья!", Toast.LENGTH_SHORT).show();
                        currentState = "nothing_happen";
                        acceptBtn.setText("Предложить дружить");
                        rejectBtn.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "При отказе дружить произошла ошибка: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Если он предлагет дружить
        if (currentState.equals("he_sent_pending")){
            requestRef.child(userId).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        final HashMap<String,Object> map = new HashMap<>();
                        map.put("status","friend");
                        map.put("userName",userName);
                        map.put("profession",profession);
                        map.put("profileImage",profileImage);

                        friendRef.child(currentUser.getUid()).child(userId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    friendRef.child(userId).child(currentUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ViewFriendActivity.this, "Вы добавили друга", Toast.LENGTH_SHORT).show();
                                            currentState = "friend";
                                            acceptBtn.setText("Отправить сообщение");
                                            rejectBtn.setText("Удалить из друзей");
                                            rejectBtn.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

        }

        if (currentState.equals("friend")){
            //
        }
    }

    private void loadUser() {
        pd.show();

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Получаем все нужные данные  юезара,на которого мы нажали в FindFriendActivty

                    String name = snapshot.child("Name").getValue().toString();
                    String country = snapshot.child("Country").getValue().toString();
                    String city = snapshot.child("City").getValue().toString();
                    String profession = snapshot.child("Profession").getValue().toString();
                    String imageUrl = snapshot.child("ProfileImage").getValue().toString();

                    //Устанавливаем данные View
                    userName.setText(name);
                    userLocation.setText(country + "," + city);
                    userProfession.setText(profession);

                    Glide.with(ViewFriendActivity.this).load(imageUrl).into(profileImage);

                    //Устанавливаем имя юезра в тулбар
                    getSupportActionBar().setTitle(name);
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста подождите...");
    }
}