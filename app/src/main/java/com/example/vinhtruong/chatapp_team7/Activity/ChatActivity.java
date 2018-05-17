package com.example.vinhtruong.chatapp_team7.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinhtruong.chatapp_team7.GetTimeAgo;
import com.example.vinhtruong.chatapp_team7.Models.Message;
import com.example.vinhtruong.chatapp_team7.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    //Layout
    private Toolbar toolbarChat;
    private ImageButton btnSend, btnSendImage;
    private EditText edtInputMessage;
    //Custom toolbar chat
    private TextView txtNameChatbar, txtLastSeenChatbar;
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private DatabaseReference messagesDatabase, userDatabase;
    private String mCurrentUserId;
    private StorageReference mImageStorage;
    //Recycler
    private RecyclerView recyclerMessages;
    private LinearLayoutManager mLayoutManager;
    //

    private String friendId,friendName;
    int GALLERY_PICK_CODE=123;

    ArrayList<Message> messageArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Anh Xa
        AnhXa();
        //From Friend Fragment
        friendId = getIntent().getStringExtra("userId");
        friendName = getIntent().getStringExtra("userName");
        //Toolbar
        setSupportActionBar(toolbarChat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(action_bar_view);
        // ---- Custom Action bar Items ----
        txtNameChatbar = (TextView) findViewById(R.id.txtNameChatBar);
        txtLastSeenChatbar = (TextView) findViewById(R.id.txtLastSeenChatBar);
        txtNameChatbar.setText(friendName);
        //Firebase
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        userDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        messagesDatabase=FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserId).child(friendId);
        //Check online
        mRootRef.child("Users").child(friendId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long online = (long) dataSnapshot.child("online").getValue();
                if(online==0) {
                    txtLastSeenChatbar.setText("Online");
                } else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = online;
                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    txtLastSeenChatbar.setText(lastSeenTime);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //Recycler
        mLayoutManager = new LinearLayoutManager(this);
        recyclerMessages = (RecyclerView) findViewById(R.id.recyclerMessages);
        recyclerMessages.setHasFixedSize(true);
        recyclerMessages.setLayoutManager(mLayoutManager);

        //==================Events===============
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK_CODE);
            }
        });
    }

    private void sendMessage() {
        String message = edtInputMessage.getText().toString();
        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + friendId;
            String chat_user_ref = "messages/" + friendId + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child(friendId).push();

            String push_id = user_message_push.getKey();

            String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("type", "text");
            messageMap.put("from", mCurrentUserId);
            messageMap.put("date", currentDate);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            edtInputMessage.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + mCurrentUserId + "/" + friendId;
            final String chat_user_ref = "messages/" + friendId + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child(friendId).push();
            final String push_id = user_message_push.getKey();

            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        String download_url = task.getResult().getDownloadUrl().toString();

                        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("type", "image");
                        messageMap.put("from", mCurrentUserId);
                        messageMap.put("date", currentDate);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        edtInputMessage.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError != null){
                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void AnhXa() {
        toolbarChat=findViewById(R.id.toolbarChat);
        recyclerMessages=findViewById(R.id.recyclerMessages);
        btnSend=findViewById(R.id.btnSendChat);
        btnSendImage=findViewById(R.id.btnPlusChat);
        edtInputMessage=findViewById(R.id.edtInputChat);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Message, MessagesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessagesViewHolder>(
                Message.class,
                R.layout.custom_message_line,
                MessagesViewHolder.class,
                messagesDatabase
        )
        {
            @Override
            protected void populateViewHolder(final MessagesViewHolder messagesViewHolder, Message messages, int position) {
                final String idSender = messages.getFrom().toString();
                String type =messages.getType();
                Boolean yourSelf=false;
                if(idSender.equals(mCurrentUserId)){
                    yourSelf=true;
                }
                messagesViewHolder.setMessage(messages.getMessage(),yourSelf,type);
                messagesViewHolder.setTime(messages.getDate());
                userDatabase.child(idSender).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("thumb_image").getValue().toString();
                        messagesViewHolder.setName(name);
                        messagesViewHolder.setThumbImage(image);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };
        recyclerMessages.setAdapter(firebaseRecyclerAdapter);
    }
    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public MessagesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        @SuppressLint("ResourceAsColor")
        public void setMessage(String message, boolean yourSelf, String type){
            TextView txtMessage = (TextView) mView.findViewById(R.id.txtMessage);
            ImageView imgMessage = mView.findViewById(R.id.imgMessage);

            if(yourSelf){
                txtMessage.setBackgroundResource(R.drawable.message_text_background_white);
                txtMessage.setTextColor(R.color.colorBlack);
            }
            if(type.equals("text")){
                txtMessage.setText(message);
                txtMessage.setVisibility(View.VISIBLE);
                imgMessage.setVisibility(View.GONE);
            }else{
                imgMessage.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                //txtMessage.setText(message);
                Picasso.get().load(message).placeholder(R.drawable.empty).into(imgMessage);
            }
        }
        public void setName(String name){
            TextView txtName=mView.findViewById(R.id.txtMessName);
            txtName.setText(name);
        }
        public void setThumbImage(String imgAvatarUsers){
            CircleImageView imgAvatarMess=mView.findViewById(R.id.imgMessAvatar);
            Picasso.get().load(imgAvatarUsers).placeholder(R.drawable.user).into(imgAvatarMess);
        }
        public void setTime(String time){
            TextView txtMessTime = mView.findViewById(R.id.txtMessTime);
            txtMessTime.setText(time);
        }
    }
}

