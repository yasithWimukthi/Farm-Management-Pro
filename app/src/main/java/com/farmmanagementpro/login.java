package com.farmmanagementpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sdsmdg.tastytoast.TastyToast;

public class login extends AppCompatActivity {

    private Button loginBtn;
    private Button signUp;
    private EditText userEmailEditText;;
    private EditText passwordEditText;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.login_btn);
        userEmailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    TastyToast.makeText(
                            getApplicationContext(),
                            "Please enter a valid email !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(password)){
                    TastyToast.makeText(
                            getApplicationContext(),
                            "Please enter your password !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (password.length()<6){
                    TastyToast.makeText(
                            getApplicationContext(),
                            "Password should contain at least 6 characters !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.INFO
                    );
                }else{
                    loginWithEmailAndPassword(email, password);
                }
            }
        });
    }

    private void loginWithEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidCredentialsException){
                            TastyToast.makeText(
                                    getApplicationContext(),
                                    "Invalid password. Please try again !",
                                    TastyToast.LENGTH_LONG,
                                    TastyToast.ERROR
                            );
                        }else if(e instanceof FirebaseAuthInvalidUserException){
                            TastyToast.makeText(
                                    getApplicationContext(),
                                    "Incorrect email address. Please try again !",
                                    TastyToast.LENGTH_LONG,
                                    TastyToast.ERROR
                            );
                        }else{
                            TastyToast.makeText(
                                    getApplicationContext(),
                                    e.getLocalizedMessage(),
                                    TastyToast.LENGTH_LONG,
                                    TastyToast.ERROR
                            );
                        }
                    }
                });
    }
}