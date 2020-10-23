package ru.kostya.chatmeapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import ru.kostya.chatmeapp.R;
import ru.kostya.chatmeapp.auth.LoginActivity;
import ru.kostya.chatmeapp.auth.RegisterActivity;

public class StartActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srart);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentUser !=null){
                    //Если наш текщий юзер не пустой,следовательно он прошел все регистрации,включая в SetupActivity (CardView)
                    //То фото,имя,город,страну,профессию мы добавляли в reference.child(currentuser.getuId())
                    //Короче говоря,если мы как то отправили наши данные в reference,значит мы уже зареганы и можем спокойно запускать MainActivity
                    reference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Если есть ветка,то уже есть данные по нашему юзеру,которы е он вводил в setupactivity
                            //Сразу направляем на mainActivity
                            if (dataSnapshot.exists()){
                                Intent mainActivity = new Intent(StartActivity.this,MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                        } else {
                                //Если ветки не существует,то юзеру следует заполинть данные в setupActivity
                                Intent setupActivity = new Intent(StartActivity.this, SetupActivity.class);
                                startActivity(setupActivity);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Intent loginActivity = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }
        };
        new Handler().postDelayed(runnable,2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}