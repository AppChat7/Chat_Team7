package com.example.vinhtruong.chatapp_team7.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinhtruong.chatapp_team7.Models.Room;
import com.example.vinhtruong.chatapp_team7.Activity.RoomChatActivity;
import com.example.vinhtruong.chatapp_team7.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vinhtruong on 4/20/2018.
 */

public class FragmentRooms extends Fragment {
    //Firebase
    private DatabaseReference  rootRef;
    private static DatabaseReference roomsDatabase, roomMessagesDatabase;
    private DatabaseReference usersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    //Layout
    private RecyclerView recyclerRooms;
    private FloatingActionButton btnCreateRoom;
    private LinearLayoutManager mLayoutManager;
    //Fragment view


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_rooms,container,false);
        //Firebase
         rootRef= FirebaseDatabase.getInstance().getReference();
         mAuth=FirebaseAuth.getInstance();
         mCurrentUserId=mAuth.getCurrentUser().getUid();
         roomsDatabase=rootRef.child("rooms");
         roomMessagesDatabase=rootRef.child("room_messages");
         usersDatabase=rootRef.child("Users");
        //AnhXa
        AnhXa(view);
        //Recycler
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerRooms.setHasFixedSize(true);
        recyclerRooms.setLayoutManager(mLayoutManager);


        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog createDialog = new Dialog(getContext());
                createDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                createDialog.setContentView(R.layout.dialog_create_room);

                final EditText edtInputName = createDialog.findViewById(R.id.edtInputRoomName);
                Button btnCreate = createDialog.findViewById(R.id.btnCreateRoom);
                Button btnCancel = createDialog.findViewById(R.id.btnCancel);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createDialog.dismiss();
                    }
                });

                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         String name = edtInputName.getText().toString().trim();
                        if(name.isEmpty()){
                            Toast.makeText(getActivity(), "Vui lòng nhập tên phòng", Toast.LENGTH_SHORT).show();
                        }else{
                            DatabaseReference roomPush = rootRef.child("rooms").push();
                            String pushId = roomPush.getKey();
                            Room room=new Room(name,mCurrentUserId);
                            rootRef.child("rooms").child(pushId).setValue(room);
                            createDialog.dismiss();
                        }
                    }
                });
                createDialog.show();
            }
        });
        return view;
    }

    private void AnhXa(View view) {
        btnCreateRoom=view.findViewById(R.id.btnCreateRoom);
        recyclerRooms=view.findViewById(R.id.recyclerRooms);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Room,RoomViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Room, RoomViewHolder>(
                Room.class,
                R.layout.custom_room_line,
                RoomViewHolder.class,
                roomsDatabase
        ) {
            @Override
            protected void populateViewHolder(final RoomViewHolder viewHolder, final Room room, int position) {
                viewHolder.setRoomName(room.getRommName());
                viewHolder.hideBtnDelete();

                String idCreator = room.getIdCreator();
                if(idCreator.equals(mCurrentUserId)){
                    viewHolder.showBtnDelete();
                }

                usersDatabase.child(idCreator).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String creatorName= dataSnapshot.child("name").getValue().toString();
                        String imageUrl = dataSnapshot.child("thumb_image").getValue().toString();
                        viewHolder.setCreator("Created by "+creatorName);
                        viewHolder.setRoomImage(imageUrl);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final String roomId =getRef(position).getKey();
                final String roomName = room.getRommName();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent roomChatIntent = new Intent(getContext(), RoomChatActivity.class);
                        roomChatIntent.putExtra("roomId", roomId);
                        roomChatIntent.putExtra("roomName",roomName);
                        startActivity(roomChatIntent);
                    }
                });

                viewHolder.btnDeleteroom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        roomsDatabase.child(roomId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                        roomMessagesDatabase.child(roomId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });


            }
        };
        recyclerRooms.setAdapter(firebaseRecyclerAdapter);
    }
    public static class RoomViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView btnDeleteroom;
        @SuppressLint("WrongViewCast")
        public RoomViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
             btnDeleteroom = mView.findViewById(R.id.btnDeleteRoom);

        }
        public void setRoomName(String name){
            TextView txtRoomName = mView.findViewById(R.id.txtNameUsers);
            txtRoomName.setText(name);
        }
        public void setCreator(String name){
            TextView txtCreator =mView.findViewById(R.id.txtCreator);
            txtCreator.setText(name);
        }
        public void setRoomImage(String url){
            CircleImageView imgRoom = mView.findViewById(R.id.imgGroup);
            Picasso.get().load(url).placeholder(R.drawable.user).into(imgRoom);
        }
        public void hideBtnDelete(){
            ImageView btnDeleteroom = mView.findViewById(R.id.btnDeleteRoom);
            btnDeleteroom.setVisibility(View.INVISIBLE);
            btnDeleteroom.setEnabled(false);
        }
        public void showBtnDelete(){
            //ImageView btnDeleteroom = mView.findViewById(R.id.btnDeleteRoom);
            btnDeleteroom.setVisibility(View.VISIBLE);
            btnDeleteroom.setEnabled(true);
        }
    }
}
