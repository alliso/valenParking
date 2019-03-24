package com.upv.dadm.valenparking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText)  findViewById(R.id.login_editTextEmail);
        editTextPassword = (EditText)  findViewById(R.id.login_editTextPassword);

        TextView forgotPassword = (TextView) findViewById(R.id.login_textViewForgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        Button btnLogIn = (Button) findViewById(R.id.login_buttonLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(isValidEmailAndPassword()){
                    logInByEmail(email,password);
                }
            }
        });

        Button btnCreateAccount = (Button) findViewById(R.id.login_buttonCreateAccount);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

    }

    private void logInByEmail(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "logeado", Toast.LENGTH_SHORT).show();
                            if(mAuth.getCurrentUser().isEmailVerified()){

                            }else{
                                Toast.makeText(LoginActivity.this, getString(R.string.login_email_verified_string), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_authentication_failed_string), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmailAndPassword(){
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        if(email.equals("") || password.equals("")) return false;
        return true;
    }
}
