package com.upv.dadm.valenparking;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount account;
    private FirebaseFirestore db;
    private CollectionReference userDBRef;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private String email;
    private String password;
    public static final int RC_GOOGLE_SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mGoogleApiClient = getGoogleApiClient();
        db = FirebaseFirestore.getInstance();
        userDBRef = db.collection("users");

        editTextEmail = (EditText) findViewById(R.id.login_editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.login_editTextPassword);

        TextView forgotPassword = (TextView) findViewById(R.id.login_textViewForgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        Button btnLogIn = (Button) findViewById(R.id.login_buttonLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isValidEmailAndPassword()) {
                    logInByEmail(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_complete_data_string), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnLogInWithGoogle = (Button) findViewById(R.id.login_buttonLogInGoogle);
        btnLogInWithGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            }
        });


        Button btnCreateAccount = (Button) findViewById(R.id.login_buttonCreateAccount);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                account = result.getSignInAccount();
                loginByGoogleAccountIntoFirebase(account);
            }
        }
    }

    private void logInByEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            if (currentUser.isEmailVerified()) {
                                Query query = userDBRef.whereEqualTo("userID", currentUser.getUid());
                                final Task<QuerySnapshot> taskQuery = query.get();
                                taskQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() > 0) {
                                                String [] name = currentUser.getEmail().split("@");
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(name[0])
                                                        .setPhotoUri(Uri.parse("https://www.google.com/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwitzbigmK_hAhUBzhoKHZ8BDesQjRx6BAgBEAU&url=https%3A%2F%2Fcursodeyogaparaprincipiantes.com%2Fmember%2Fcurso-yoga-online-vieja%2F&psig=AOvVaw1Iv8L0wdiqUEqhcZniSm-p&ust=1554218208310296"))
                                                        .build();
                                                currentUser.updateProfile(profileUpdates);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                String [] name = currentUser.getEmail().split("@");
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(name[0])
                                                        .setPhotoUri(Uri.parse("android.resource://com.upv.dadm.valenparking/drawable/ic_photo_def]"))
                                                        .build();

                                                currentUser.updateProfile(profileUpdates);

                                                Map<String, Object> user = new HashMap<>();
                                                user.put("userID", currentUser.getUid());
                                                user.put("userName", name[0]);
                                                user.put("userEmail", currentUser.getEmail());
                                                user.put("userPicture", "");
                                                user.put("userFavourites", "");
                                                userDBRef.add(user);

                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.login_email_verified_string), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_authentication_failed_string), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmailAndPassword() {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        if (email.equals("") || password.equals("")) return false;
        return true;
    }

    private GoogleApiClient getGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("652442408718-ip4d1930e84ulpbcovjr1fppa96cqcq8.apps.googleusercontent.com")
                .requestEmail()
                .build();


        GoogleApiClient newGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_googleApiClient_failed_string), Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return newGoogleApiClient;
    }

    private void loginByGoogleAccountIntoFirebase(GoogleSignInAccount
                                                          googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_with_google_account), Toast.LENGTH_SHORT).show();
                    if (mGoogleApiClient.isConnected()) {
                        //Forzar a que no se cargue siempre la misma cuenta de google
                        Query query = userDBRef.whereEqualTo("userID", account.getId());
                        final Task<QuerySnapshot> taskQuery = query.get();
                        taskQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0) {
                                        //Forzar a que no inice automáticamente con la cuenta de siempre
                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("userID", account.getId());
                                        user.put("userName", account.getDisplayName());
                                        user.put("userEmail", account.getEmail());
                                        user.put("userPicture", account.getPhotoUrl().toString());
                                        user.put("userFavourites", "");

                                        userDBRef.add(user);

                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.signup_email_failed_string), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }

        });
    }
}
