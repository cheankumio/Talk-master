package com.swagath.talk;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    FirebaseDatabase fdb;
    DatabaseReference dbref;
    private EditText uNameFld;
    private EditText pwd1Fld;
    private EditText pwd2Fld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("MYLOG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("MYLOG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void onStart() {
        super.onStart();
        ShimmerTextView tv = (ShimmerTextView)findViewById(R.id.shimmer_tv);
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(1300)
                .setStartDelay(300);
        shimmer.start(tv);
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createAccount(View view) {
        uNameFld = (EditText)findViewById(R.id.uName);
        pwd1Fld = (EditText)findViewById(R.id.pwd1);
        pwd2Fld = (EditText)findViewById(R.id.pwd);
        final String uName = uNameFld.getText().toString();
        final String pwd1 = pwd1Fld.getText().toString();
        final String myName = pwd2Fld.getText().toString();


        mAuth.createUserWithEmailAndPassword(uName, pwd1)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       Log.d("MYLOG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                       if (!task.isSuccessful()) {
                           Toasty.info(SignUpActivity.this, "請正確填寫E-mail，密碼需6字元以上", 9, true).show();
                       }else{
                           Toasty.success(SignUpActivity.this, "註冊成功", Toast.LENGTH_SHORT, true).show();
                           dbref.child("user").child(mAuth.getCurrentUser().getUid()).setValue(myName);
                           finish();
                       }
                   }
             });
    }
}
