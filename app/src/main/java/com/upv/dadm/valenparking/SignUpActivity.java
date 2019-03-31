package com.upv.dadm.valenparking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private String email;
    private String password;
    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText)  findViewById(R.id.sign_up_editTextEmail);
        editTextPassword = (EditText)  findViewById(R.id.sign_up_editTextPassword);
        editTextConfirmPassword = (EditText)  findViewById(R.id.sign_up_editTextConfirmPassword);

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                email = editTextEmail.getText().toString();
                if(!isValidEmail(email)){
                    editTextEmail.setError(getString(R.string.signup_email_error_string));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                password = editTextPassword.getText().toString();
                if(!isValidPassword(password)){
                    editTextPassword.setError(getString(R.string.signup_password_error_string));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                confirmPassword = editTextConfirmPassword.getText().toString();
                if(!isValidConfirmPassword(password, confirmPassword)){
                    editTextConfirmPassword.setError(getString(R.string.signup_confirmpass_error_string));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        Button btnSignUp = (Button) findViewById(R.id.sign_up_buttonSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password,confirmPassword)){
                    signUpByEmail(email, password);
                }else{
                    Toast.makeText(SignUpActivity.this, getString(R.string.signup_data_fill_failed_string), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signUpByEmail(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, getString(R.string.signup_email_sent_string), Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(SignUpActivity.this, getString(R.string.signup_email_failed_string), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this, getString(R.string.signup_email_failed_string), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password){
        // Necesita Contener -->    1 Num    1 Minuscula  1 Mayuscula  Min Caracteres 6
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        return pattern.matcher(password).matches();
    }

    private boolean isValidConfirmPassword(String password, String confirmPassword){
        if(password.equals(confirmPassword)) return true;
        return false;
    }
}
