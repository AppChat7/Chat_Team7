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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomChatActivity extends AppCompatActivity {
    //Layout
    private Toolbar toolbar;
    private ImageButton btnSend, btnSendImage;
    private EditText edtInputMessage;
    //Custom toolbar chat
    private TextView txtNameChatbar, txtCreatorName;
    //Recycler
    private RecyclerView recyclerMessagesRoom;
    private LinearLayoutManager mLayoutManager;
    //Firebase
    private DatabaseReference rootRef, roomMessagesDatabase,userDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private StorageReference mImageStorage;

    private String roomId;
    int GALLERY_PICK_CODE=123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);
        //AnhXa
        AnhXa();
        //From Fragment Rooms
         roomId = getIntent().getStringExtra("roomId");
        String roomName = getIntent().getStringExtra("roomName");
        //Toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(action_bar_view);
        // ---- Custom Action bar Items ----
        txtNameChatbar = (TextView) findViewById(R.id.txtNameChatBar);
        txtCreatorName=findViewById(R.id.txtLastSeenChatBar);
        txtNameChatbar.setText(roomName);
        //Firebase
        mAuth=FirebaseAuth.getInstance();
        mCurrentUserId=mAuth.getCurrentUser().getUid();
        rootRef=FirebaseDatabase.getInstance().getReference();
        roomMessagesDatabase=rootRef.child("room_messages").child(roomId);
        userDatabase=rootRef.child("Users");
        mImageStorage = FirebaseStorage.getInstance().getReference();
        //Recycler
        mLayoutManager = new LinearLayoutManager(this);
        recyclerMessagesRoom.setHasFixedSize(true);
        recyclerMessagesRoom.setLayoutManager(mLayoutManager);

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

   @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Message,RoomMessagesViewholder> recyclerAdapter=new FirebaseRecyclerAdapter<Message, RoomMessagesViewholder>(
                Message.class,
                R.layout.custom_message_line,
                RoomMessagesViewholder.class,
                roomMessagesDatabase
        ) {
            @Override
            protected void populateViewHolder(final RoomMessagesViewholder viewHolder, Message message, int position) {


                final String idSender = message.getFrom().toString();
                String type =message.getType();
                Boolean yourSelf=false;
                if(idSender.equals(mCurrentUserId)){
                    yourSelf=true;
                }
                viewHolder.setMessage(message.getMessage(),yourSelf,type);
                viewHolder.setTime(message.getDate());
                userDatabase.child(idSender).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("thumb_image").getValue().toString();
                        viewHolder.setName(name);
                        viewHolder.setThumbImage(image);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };
        recyclerMessagesRoom.setAdapter(recyclerAdapter);
    }



    public static class RoomMessagesViewholder extends RecyclerView.ViewHolder{
        View mView;
        public RoomMessagesViewholder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name){
            TextView txtName=mView.findViewById(R.id.txtMessName);
            txtName.setText(name);
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
        public void setThumbImage(String imgAvatarUsers){
            CircleImageView imgAvatarMess=mView.findViewById(R.id.imgMessAvatar);
            Picasso.get().load(imgAvatarUsers).placeholder(R.drawable.user).into(imgAvatarMess);
        }
        public void setTime(String time){
            TextView txtMessTime = mView.findViewById(R.id.txtMessTime);
            txtMessTime.setText(time);
        }
    }

    private void sendMessage() {
        String messageInput = edtInputMessage.getText().toString();
        if(!TextUtils.isEmpty(messageInput)){
            DatabaseReference roomMessagesPush = roomMessagesDatabase.push();
            String pushId = roomMessagesPush.getKey();
            String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            Message message=new Message(messageInput,"text",mCurrentUserId,currentDate);
            roomMessagesDatabase.child(pushId).setValue(message);

            edtInputMessage.setText("");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            DatabaseReference roomMessagesPush = roomMessagesDatabase.push();
            final String pushId = roomMessagesPush.getKey();

            StorageReference filepath = mImageStorage.child("message_images").child( pushId + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        String download_url = task.getResult().getDownloadUrl().toString();
                        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                        Message message=new Message(download_url,"image",mCurrentUserId,currentDate);
                        roomMessagesDatabase.child(pushId).setValue(message);

                    }
                }
            });
        }
    }

    private void AnhXa() {
        toolbar=findViewById(R.id.toolbarRoomChat);
        btnSend=findViewById(R.id.btnSendChatRoom);
        btnSendImage=findViewById(R.id.btnSendImgRoom);
        edtInputMessage=findViewById(R.id.edtInputChatRoom);
        recyclerMessagesRoom=findViewById(R.id.recyclerMessagesRoom);
    }
}
