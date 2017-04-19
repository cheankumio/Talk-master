package com.swagath.talk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chatroom extends AppCompatActivity {

    FirebaseDatabase fdb;
    DatabaseReference dbref;
    ArrayList<String> displayItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView lv;
    public static final String MESSAGE_DATA ="message";
    public static final String USER_DATA ="user";
    EditText edit1;//使用者文字輸入框
    String userName;
    String userUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //取得從Login.java傳來的UID String
        Intent mintent = this.getIntent();
        userUID = mintent.getStringExtra("UID");
        Log.d("MYLOG", "User UID is: " + userUID);

        //設定ListView及adapter，承接database的文字流
        lv = (ListView)findViewById(R.id.chatRoomListView);
        adapter = new ArrayAdapter<String>(this, R.layout.message,R.id.message_text, displayItems);
        lv.setAdapter(adapter);

        //開啟FirebaseDatabase，並取得控制方法
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference();
        Log.d("MYLOG", "Value is: " + dbref.toString());

        edit1 = (EditText)findViewById(R.id.editText);  //綁定xml的editText

        //取得使用者ID
        getUserNames();

        //對dbref設定監聽
        dbref.child(MESSAGE_DATA).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //將資料庫的文字流新增至adapter
                adapter.add(dataSnapshot.getValue().toString());
                //令ListView自動滑動至底部
                scrollMyListViewToBottom();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("MYLOG","Database change: "+s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MYLOG","Database onCancell "+databaseError);
            }
        });
    }

    //按鈕功能
    public void mybtn(View view){
        //判斷EditText是否有輸入文字
        if(edit1.length()>0){
            dbref.child(MESSAGE_DATA).push().setValue(userName+": "+edit1.getText().toString(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("MYLOG", "Data could not be saved. " +databaseError.getMessage());
                    } else {

                        Log.d("MYLOG", "Data saved successfully.");
                    }
                }
            });
            edit1.setText("");
        }else{
            Log.d("MYLOG", "Please input any Word");
        }
    }

    //設定ListView自動滾動至底部
    private void scrollMyListViewToBottom() {
        lv.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lv.setSelection(lv.getCount() - 1);
            }
        });
    }
    
    //取得使用者名稱
    private void getUserNames(){
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child(USER_DATA).child(userUID).getValue().toString();
                Log.d("MYLOG", "Get User Name:"+userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
