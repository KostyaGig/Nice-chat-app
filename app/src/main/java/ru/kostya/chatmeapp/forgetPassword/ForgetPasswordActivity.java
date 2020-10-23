package ru.kostya.chatmeapp.forgetPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ru.kostya.chatmeapp.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private EditText edEmail;
    private Button sendBtn;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initializeProgressDialog();

        edEmail = findViewById(R.id.edEmail);
        sendBtn = findViewById(R.id.sendBtn);

        mAuth = FirebaseAuth.getInstance();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();

                if (!TextUtils.isEmpty(email)){
                    //Отправляем письмо на почту
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgetPasswordActivity.this, "На ваш email было выслано письмо", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, "Письмо не было выслано : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Введите email", Toast.LENGTH_SHORT).show();
                }
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