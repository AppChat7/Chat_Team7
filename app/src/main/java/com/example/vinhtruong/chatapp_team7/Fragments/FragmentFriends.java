package com.example.vinhtruong.chatapp_team7.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinhtruong.chatapp_team7.Activity.ChatActivity;
import com.example.vinhtruong.chatapp_team7.Activity.FriendAroundMapActivity;
import com.example.vinhtruong.chatapp_team7.Activity.MapDirectionActivity;
import com.example.vinhtruong.chatapp_team7.Activity.ProfileActivity;
import com.example.vinhtruong.chatapp_team7.Models.Friends;
import com.example.vinhtruong.chatapp_team7.Models.Users;
import com.example.vinhtruong.chatapp_team7.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentFriends extends Fragment {

    private FloatingActionButton btnMap;
    //Layout
    private RecyclerView recyclerFriends;
    //Firebase
    private DatabaseReference friendsDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference AllUserDatabase;
    //Fragment view
    private View mView;
    private String currentUserId;
    public static int check = 0;

    ArrayList<Users> arrUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Get view
        mView = inflater.inflate(R.layout.fragment_friends, container, false);
        Log.e("where", "onCreateView: ");
        addControls(mView);
        if (isOnline()) {
            initView();
        } else {
            Toast.makeText(getActivity(), "You are offline now", Toast.LENGTH_SHORT).show();
        }
        addEvents();
        return mView;
    }



    private void addEvents() {
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check=0;
                showFriendAround();
            }
        });
    }

    private void addControls(View mView) {
        arrUsers = new ArrayList<>();
        btnMap = mView.findViewById(R.id.btnMapAround);
        //Firebase
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        friendsDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        AllUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        //Recycler
        recyclerFriends = mView.findViewById(R.id.rvConversation);
        recyclerFriends.setHasFixedSize(true);
        recyclerFriends.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    //    @Override
//    public void onStart() {
//        super.onStart();
    public void initView() {
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class, R.layout.custom_user_line, FriendsViewHolder.class, friendsDatabase) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                viewHolder.setDate(model.getDate());

                final String listFriendsId = getRef(position).getKey();

                mUserDatabase.child(listFriendsId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String friendsName = dataSnapshot.child("name").getValue().toString();
                        String friendsThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        final Double friendLat = (Double) dataSnapshot.child("lat").getValue();
                        final Double friendLon = (Double) dataSnapshot.child("lon").getValue();

                        if (dataSnapshot.hasChild("online")) {
                            long onlineStatus = (long) dataSnapshot.child("online").getValue();
                            viewHolder.setFriendsOnline(onlineStatus);
                        }
                        viewHolder.setName(friendsName);
                        viewHolder.setThumbImage(friendsThumb);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message", "Get Direction"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", listFriendsId);
                                            startActivity(profileIntent);
                                        } else if (i == 1) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("userId", listFriendsId);
                                            chatIntent.putExtra("userName", friendsName);
                                            startActivity(chatIntent);
                                        } else if (i == 2) {
                                            showDirect(friendLat, friendLon);
                                            //showDirect(10.857351, 106.764235);
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerFriends.setAdapter(firebaseRecyclerAdapter);
        // }
    }

    private void showFriendAround() {

        AllUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        arrUsers.add(item.getValue(Users.class));
                        Log.e("arrUser ", arrUsers.size() + " ");

                    }
                    if (check == 0) {
                        check = 1;
                        Intent intent = new Intent(getActivity(), FriendAroundMapActivity.class);
                        intent.putExtra("listUser", arrUsers);
                        getActivity().startActivity(intent);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void showDirect(double lat, double lon) {
        Intent intent = new Intent(getActivity(), MapDirectionActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        getActivity().startActivity(intent);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView txtStatusUsers = mView.findViewById(R.id.txtStatusUsers);
            txtStatusUsers.setText(date);
        }

        public void setName(String name) {
            TextView txtNameUsers = mView.findViewById(R.id.txtNameUsers);
            txtNameUsers.setText(name);
        }

        public void setThumbImage(String thumbUrl) {
            CircleImageView imgAvatarUsers = mView.findViewById(R.id.imgGroup);
            Picasso.get().load(thumbUrl).placeholder(R.drawable.avatar).into(imgAvatarUsers);
        }

        public void setFriendsOnline(long onlineStatus) {
            ImageView imgOnline = mView.findViewById(R.id.imgOnlineStatus);
            if (onlineStatus == 0) {
                imgOnline.setVisibility(View.VISIBLE);
            } else {
                imgOnline.setVisibility(View.INVISIBLE);
            }
        }
    }
}
