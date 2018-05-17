package com.example.vinhtruong.chatapp_team7.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vinhtruong.chatapp_team7.R;
import com.example.vinhtruong.chatapp_team7.Sqlite.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class StatusActivity extends AppCompatActivity {
    //layout
    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSavebtn;
    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;
    //Progress Dialog
    private ProgressDialog mProgress;
    //Current user id
    private String current_uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //Ánh xạ
        AnhXa();
        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.statusToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //History từ màn hình setting
        String status_value = getIntent().getStringExtra("status_value");
        mStatus.getEditText().setText(status_value);


        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();
                String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "There was some error in saving Changes.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                MainActivity.database.QueryData("INSERT INTO History VALUES(null,'"+status+"','"+currentDate+"')");

            }
        });
    }
    private void AnhXa() {
        mStatus = (TextInputLayout) findViewById(R.id.inputStatusSettings);
        mSavebtn = (Button) findViewById(R.id.btnSaveStatus);
    }
}