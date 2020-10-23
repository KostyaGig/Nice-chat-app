package ru.kostya.chatmeapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.kostya.chatmeapp.R;
import ru.kostya.chatmeapp.activities.MainActivity;
import ru.kostya.chatmeapp.activities.SetupActivity;
import ru.kostya.chatmeapp.activities.StartActivity;
import ru.kostya.chatmeapp.forgetPassword.ForgetPasswordActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText edEmail,edPassword;
    private Button loginBtn;
    private TextView notExistAccount,forgetPassword;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);

        loginBtn = findViewById(R.id.btnLog);
        notExistAccount = findViewById(R.id.notExistAccount);
        forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgetPasswordActivity = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(forgetPasswordActivity);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();

                loginUser(email,password);
            }
        });

        notExistAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });


    }

    private void initializeProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста подождите...");
    }

    private void loginUser(String email, String password) {
        pd.show();

        //Если поля не пустые,входим
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        pd.dismiss();

                        Toast.makeText(LoginActivity.this, "Успешный вход в систему!", Toast.LENGTH_SHORT).show();
                        Intent setupActivity = new Intent(LoginActivity.this, SetupActivity.class);
                        startActivity(setupActivity);
                        finish();
                    } else {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "Ошибка при входе: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(this, "Заполинте пустые поля!", Toast.LENGTH_SHORT).show();
        }
    }

}