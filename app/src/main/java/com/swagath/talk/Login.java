package com.swagath.talk;

import android.animation.Animator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Login";

    private EditText uNameFld, pwdFld;
    Shimmer shimmer;


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
                    Intent channelIntent = new Intent(Login.this, Chatroom.class);
                    channelIntent.putExtra("UID", mAuth.getCurrentUser().getUid());
                    startActivity(channelIntent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        shimmer = new Shimmer();
        shimmer.setDuration(1500)
                .setStartDelay(300)
                .setDirection(Shimmer.ANIMATION_DIRECTION_RTL);
        ShimmerTextView txs = (ShimmerTextView)findViewById(R.id.shimmer_t);
        shimmer.start(txs);

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
                                Toasty.success(Login.this, "登入成功", Toast.LENGTH_SHORT, true).show();
                                Log.d("MYLOG", "signInWithEmail:onComplete:" + task.isSuccessful());

                            } else {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toasty.info(Login.this, "請正確填寫E-mail與密碼", 6, true).show();
                            }
                        }
                    });
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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