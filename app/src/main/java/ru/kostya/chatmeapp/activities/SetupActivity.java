package ru.kostya.chatmeapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import ru.kostya.chatmeapp.R;

public class SetupActivity extends AppCompatActivity {

    private ImageView cardViewImageProfile;
    private EditText cardViewEdName,cardViewEdCity,cardViewEdCountry,cardViewEdProfession;
    private Button cardViewButtonSave;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private ProgressDialog pd;
    private String cardViewImageUrl;

    private Uri imageUri;
    public static final int REQUEST_IMAGE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        init();
        initializeProgressDialog();

        cardViewImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent,REQUEST_IMAGE);
            }
        });

        cardViewButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });

    }

    private void init() {
        cardViewImageProfile = findViewById(R.id.profileImage);
        cardViewEdName = findViewById(R.id.edName);
        cardViewEdCity = findViewById(R.id.edCity);
        cardViewEdCountry = findViewById(R.id.edCountry);
        cardViewEdProfession = findViewById(R.id.edProfession);

        cardViewButtonSave = findViewById(R.id.btnSave);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Profile image");
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE && data != null){
            imageUri = data.getData();
            cardViewImageProfile.setImageURI(imageUri);
        }
    }

    private void saveUserData() {
        pd.show();
        final String userName = cardViewEdName.getText().toString().trim();
        final String city = cardViewEdCity.getText().toString().trim();
        final String country = cardViewEdCountry.getText().toString().trim();
        final String profession = cardViewEdProfession.getText().toString().trim();

        //Сделали проверку на поля
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(country) &&!TextUtils.isEmpty(profession) && imageUri != null){

            storageReference.child(currentUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        //Если файл отправлен успешно,то получаем его uri таким образом
                        storageReference.child(currentUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Если все успешно преобразовываем uri  в url
                                //Присваиваем нашей переменной типа String
                                cardViewImageUrl = uri.toString();

                                //После успешного получения url фото сразу отправляме данные о юзере,включая url фото в reference
                                HashMap<String,Object> user = new HashMap<>();
                                user.put("Name",userName);
                                user.put("City",city);
                                user.put("Country",country);
                                user.put("Profession",profession);
                                user.put("ProfileImage",cardViewImageUrl);

                                reference.child(currentUser.getUid()).updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            pd.dismiss();
                                            Toast.makeText(SetupActivity.this, "Данные о пользователе были обновлены!", Toast.LENGTH_SHORT).show();

                                            Intent mainActivity = new Intent(SetupActivity.this,MainActivity.class);
                                            startActivity(mainActivity);
                                            finish();
                                        } else {
                                            pd.dismiss();
                                            Toast.makeText(SetupActivity.this, "В обновлении данных произошла ошибка : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });

        } else {
            pd.dismiss();
            Toast.makeText(this, "Заполните поля и установите фото", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста подождите...");
    }
}