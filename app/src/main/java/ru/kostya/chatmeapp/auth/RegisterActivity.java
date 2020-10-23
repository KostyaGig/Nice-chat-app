package ru.kostya.chatmeapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import ru.kostya.chatmeapp.R;
import ru.kostya.chatmeapp.activities.MainActivity;
import ru.kostya.chatmeapp.activities.SetupActivity;
import ru.kostya.chatmeapp.activities.StartActivity;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText edEmail,edPassword,edConfirmPassword;
    private Button registerBtn;
    private TextView existAccount;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeProgressDialog();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);

        registerBtn = findViewById(R.id.btnReg);
        existAccount = findViewById(R.id.existAccount);

        existAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivity = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String confirmPassword = edConfirmPassword.getText().toString().trim();

                createUserAccount(email,password,confirmPassword);
            }
        });

    }

    private void createUserAccount(String email, String password, String confirmPassword) {
        pd.show();
        //Если все поля не пустые,а также пароль совпадает с полем повторить пароль,то регаем юзера
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && password.equals(confirmPassword)){
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "Ваш аккаунт был успешно создан!", Toast.LENGTH_SHORT).show();

                        Intent setupActivity = new Intent(RegisterActivity.this, SetupActivity.class);
                        startActivity(setupActivity);
                        finish();
                    }
                    else {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "Ошибка в создании аккаунта : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(this, "Если поля не пустые,проверьте совпадение паролей", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста подождите...");
    }
}