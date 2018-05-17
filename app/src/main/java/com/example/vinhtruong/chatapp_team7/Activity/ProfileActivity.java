package com.example.vinhtruong.chatapp_team7.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinhtruong.chatapp_team7.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;


public class ProfileActivity extends AppCompatActivity {
    //Layout
    private ImageView imgProfile;
    private TextView txtNameProfile, txtStatusProfile, txtTotalFriends;
    private Button btnAddFriend,btnDecline;
    //Progress Dialog
    private ProgressDialog progressDialog;
    //Trạng thái kết bạn
    private String currentState;
    //Firebase
    private FirebaseUser currentUser;
    private DatabaseReference profileDatabase;
    private DatabaseReference friendsDatabase;
    private DatabaseReference friendsRequestDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Ánh xạ
        AnhXa();
        //Ẩn nút từ chối
        btnDecline.setVisibility(View.INVISIBLE);
        btnDecline.setEnabled(false);
        //Trạng thái kết bạn
        currentState="notFriends";
        //Progress Dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading Users Data");
        progressDialog.setMessage("Please wait while we load users data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //Friend Id từ Users
        final String userId = getIntent().getStringExtra("user_id");

        ///profiledatabase: ref vào thằng mình click
        //friendsRequestDatabase: ref vào node "Friends_req"
        //friendsDatabase: ref vào node "Friends"
        profileDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        friendsRequestDatabase=FirebaseDatabase.getInstance().getReference().child("Friends_req");
        friendsDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        //User hiện tại
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        //Profile realtime
        profileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                txtNameProfile.setText(name);
                txtStatusProfile.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.user).into(imgProfile);
                if(currentUser.getUid().equals(userId)){
                    btnDecline.setEnabled(false);
                    btnDecline.setVisibility(View.INVISIBLE);
                    btnAddFriend.setEnabled(false);
                    btnAddFriend.setVisibility(View.INVISIBLE);
                }
                //----------------Friends List / Request Feature --------------------
//                friendsRequestDatabase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.hasChild(userId)){
//                            String reqType = dataSnapshot.child(userId).child("request_type").getValue().toString();
//                            if(reqType.equals("received")){
//                                currentState = "req_received";
//                                btnAddFriend.setText("Accept Friend Request");
//                                btnDecline.setVisibility(View.VISIBLE);
//                                btnDecline.setEnabled(true);
//                            }else if(reqType.equals("sent")){
//                                currentState="req_sent";
//                                btnAddFriend.setText("Cancel Friend Request");
//                            };
//                            progressDialog.dismiss();
//                        }else{
//                            friendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if(dataSnapshot.hasChild(userId)){
//                                        currentState="friends";
//                                        btnAddFriend.setText("Unfriend");
//                                        btnDecline.setVisibility(View.INVISIBLE);
//                                        btnDecline.setEnabled(false);
//                                    }else{
//                                        currentState="notFriends";
//                                        btnAddFriend.setText("Send Friend Request");
//                                        btnDecline.setVisibility(View.INVISIBLE);
//                                        btnDecline.setEnabled(false);
//                                    }
//                                    progressDialog.dismiss();
//                                }
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    progressDialog.dismiss();
//                                }
//                            });
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //Friends request realtime
        friendsRequestDatabase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userId)){
                            String reqType = dataSnapshot.child(userId).child("request_type").getValue().toString();
                            if(reqType.equals("received")){
                                currentState = "req_received";
                                btnAddFriend.setText("Accept Friend Request");
                                btnDecline.setVisibility(View.VISIBLE);
                                btnDecline.setEnabled(true);
                            }else if(reqType.equals("sent")){
                                currentState="req_sent";
                                btnAddFriend.setText("Cancel Friend Request");
                            };
                            progressDialog.dismiss();
                        }else{
                            friendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userId)){
                                        currentState="friends";
                                        btnAddFriend.setText("Unfriend");
                                        btnDecline.setVisibility(View.INVISIBLE);
                                        btnDecline.setEnabled(false);
                                    }else{
                                        currentState="notFriends";
                                        btnAddFriend.setText("Send Friend Request");
                                        btnDecline.setVisibility(View.INVISIBLE);
                                        btnDecline.setEnabled(false);
                                    }
                                    progressDialog.dismiss();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddFriend.setEnabled(false);
                //=================NOT FRIEND=============
                if(currentState.equals("notFriends")){
                    friendsRequestDatabase.child(currentUser.getUid()).child(userId).child("request_type").setValue("sent")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendsRequestDatabase.child(userId).child(currentUser.getUid()).child("request_type").setValue("received")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    btnAddFriend.setEnabled(true);
                                                    btnAddFriend.setText("Cancel Friend Request");

                                                    btnDecline.setVisibility(View.INVISIBLE);
                                                    btnDecline.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }
               // =====================CANCLE REQUEST STATE====================
               if(currentState.equals("req_sent")){
                    friendsRequestDatabase.child(currentUser.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendsRequestDatabase.child(userId).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnDecline.setVisibility(View.INVISIBLE);
                                    btnDecline.setEnabled(false);

                                    btnAddFriend.setEnabled(true);
                                    currentState="notFriends";
                                    btnAddFriend.setText("Send Friend Request");
                                }
                            });
                        }
                    });
               }
                //=========================Accept Request===========
                if(currentState.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    friendsDatabase.child(currentUser.getUid()).child(userId).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendsDatabase.child(userId).child(currentUser.getUid()).child("date").setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendsRequestDatabase.child(currentUser.getUid()).child(userId).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            friendsRequestDatabase.child(userId).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    btnAddFriend.setEnabled(true);
                                                                    currentState="friends";
                                                                    btnAddFriend.setText("Unfriend");

                                                                    btnDecline.setVisibility(View.INVISIBLE);
                                                                    btnDecline.setEnabled(false);
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                }
                //========================Unfriend=====================
                if(currentState.equals("friends")){
                    friendsDatabase.child(currentUser.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendsDatabase.child(userId).child(currentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    btnAddFriend.setEnabled(true);
                                                    currentState="notFriends";
                                                    btnAddFriend.setText("Send Friend Request");
                                                }
                                            });
                                }
                            });
                }
            }
        });
//=================================DECLINE===============================
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentState.equals("req_received")){
                    friendsRequestDatabase.child(currentUser.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendsRequestDatabase.child(userId).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            btnDecline.setVisibility(View.INVISIBLE);
                                            btnDecline.setEnabled(false);

                                            btnAddFriend.setEnabled(true);
                                            currentState="notFriends";
                                            btnAddFriend.setText("Send Friend Request");
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }

    private void AnhXa() {
        imgProfile=findViewById(R.id.imgBackgroundProfile);
        txtNameProfile=findViewById(R.id.txtNameProfile);
        txtStatusProfile=findViewById(R.id.txtStatusProfile);
        txtTotalFriends=findViewById(R.id.txtTotalFriends);
        btnAddFriend=findViewById(R.id.btnAddFriend);
        btnDecline=findViewById(R.id.btnDeclineReq);
    }

}
