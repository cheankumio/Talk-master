package com.swagath.talk;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Login";

    private EditText uNameFld, pwdFld;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void logThemIn (View view) {
        uNameFld = (EditText)findViewById(R.id.uName);
        pwdFld = (EditText)findViewById(R.id.pwd);

        if(uNameFld.length()>0 && pwdFld.length()>0) {
            mAuth.signInWithEmailAndPassword(uNameFld.getText().toString(), pwdFld.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Log.d("MYLOG", "signInWithEmail:onComplete:" + task.isSuccessful());
                                Intent channelIntent = new Intent(Login.this, Chatroom.class);
                                channelIntent.putExtra("UID", mAuth.getCurrentUser().getUid());
                                startActivity(channelIntent);
                            } else {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(Login.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



}