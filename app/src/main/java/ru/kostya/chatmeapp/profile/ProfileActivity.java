package ru.kostya.chatmeapp.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ru.kostya.chatmeapp.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText edUserName,edCity,edCountry,edProfession;
    private Button updateButton;

    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        initializeProgressDialog();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                String userName = edUserName.getText().toString().trim();
                String city = edCity.getText().toString().trim();
                String country = edCountry.getText().toString().trim();
                String profession = edProfession.getText().toString().trim();

                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(country) && !TextUtils.isEmpty(profession)){
                    updateUserData(userName,city,country,profession);
                } else {
                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this, "Заполните пусые поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserData(String userName, String city, String country, String profession) {

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("Name",userName);
        userMap.put("City",city);
        userMap.put("Country",country);
        userMap.put("Profession",profession);
        userMap.put("ProfileImage","");

        userRef.child(currentUser.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this, "Данные были обновлены", Toast.LENGTH_SHORT).show();
                } else {
                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this, "При обновлении данных произошла ошибка : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void init() {
        profileImage = findViewById(R.id.profileImage);
        edUserName = findViewById(R.id.edUserName);
        edCity = findViewById(R.id.edCity);
        edCountry = findViewById(R.id.edCountry);
        edProfession = findViewById(R.id.edProfession);

        updateButton = findViewById(R.id.updateBtn);

        userRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initializeProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста подождите...");
    }
}