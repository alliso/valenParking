package com.upv.dadm.valenparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        final EditText editTextEmail = (EditText) findViewById(R.id.forgot_editTextEmail) ;
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

        Button btnForgot = (Button) findViewById(R.id.forgot_buttonForgot);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(isValidEmail(email)){
                    mAuth.sendPasswordResetEmail(email);
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.forgot_email_string), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.forgot_email_failed_string), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isValidEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
