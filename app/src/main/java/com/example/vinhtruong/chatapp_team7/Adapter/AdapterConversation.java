package com.example.vinhtruong.chatapp_team7.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vinhtruong.chatapp_team7.Models.Conversation;
import com.example.vinhtruong.chatapp_team7.Models.Users;
import com.example.vinhtruong.chatapp_team7.R;

import java.util.List;

/**
 * Created by CR7 on 3/9/2018.
 */

public class AdapterConversation extends RecyclerView.Adapter<AdapterConversation.ConversationViewHolder>{
    private List<Users> listConversation;
    private Context context;

    public List<Users> getListConversation() {
        return listConversation;
    }


    public AdapterConversation(List<Users> listConversation, Context context) {
        this.listConversation = listConversation;
        this.context = context;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_user_line,parent,false);
        return new ConversationViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        Users conversation = listConversation.get(position);
        holder.txtName.setText(conversation.getName());
        //holder.txtDate.setText(conversation.getTime());
        if(!conversation.getImage().equals("default")){
            Glide.with(context)
                    .load(conversation.getImage())
                    .into(holder.imgAvatar);
        }
//        if(!conversation.isOnline()){
//            holder.imgOnOff.setVisibility(View.INVISIBLE);
//        }

    }

    @Override
    public int getItemCount() {
        return listConversation.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener {
        private TextView txtName,txtDate ;
        private ImageView imgAvatar;
        private ImageView imgOnOff;
        public ConversationViewHolder(View itemView) {
            super(itemView);
            txtName= itemView.findViewById(R.id.txtNameUsers);
            txtDate= itemView.findViewById(R.id.txtStatusUsers);
            imgAvatar = itemView.findViewById(R.id.imgGroup);
            imgOnOff = itemView.findViewById(R.id.imgOnlineStatus);
            itemView.setOnLongClickListener( this);
            itemView.setOnClickListener( this);
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(context, "LongClick"+ getListConversation().get(this.getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            Users conversation = getListConversation().get(this.getAdapterPosition());
            myLongClickItem(conversation);
            return true;
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();

        }
    }

    private void myLongClickItem(Users conversation) {
        
    }
}
