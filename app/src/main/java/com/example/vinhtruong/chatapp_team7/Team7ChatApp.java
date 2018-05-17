package com.example.vinhtruong.chatapp_team7;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by vinhtruong on 4/21/2018.
 */

public class Team7ChatApp extends Application {
    public static DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//
//        Picasso.Builder builder=new Picasso.Builder(this);
//        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
//        Picasso build=builder.build();
//        build.setIndicatorsEnabled(true);
//        build.setLoggingEnabled(true);
//        Picasso.setSingletonInstance(build);
        mAuth=FirebaseAuth.getInstance();
        //bắt sự kiên người dùng offline
        if(mAuth.getCurrentUser()!=null){
            mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null){
                        mUsersDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }
}
