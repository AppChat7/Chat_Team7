package com.example.vinhtruong.chatapp_team7.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vinhtruong.chatapp_team7.Models.History;
import com.example.vinhtruong.chatapp_team7.R;

import java.util.ArrayList;

/**
 * Created by vinhtruong on 5/14/2018.
 */

public class HistoryAdapter extends BaseAdapter {

    Context context;
    ArrayList<History> historyArrayList;
    int layout;

    public HistoryAdapter(Context context, ArrayList<History> historyArrayList, int layout) {
        this.context = context;
        this.historyArrayList = historyArrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return historyArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder{
        TextView txtContent, txtDate;


    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewholder;
        if(view==null){
            viewholder=new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            viewholder.txtContent=view.findViewById(R.id.txtContentHistory);
            viewholder.txtDate=view.findViewById(R.id.txtDate);
            view.setTag(viewholder);
        }else{
            viewholder= (ViewHolder) view.getTag();
        }

        History history=historyArrayList.get(i);
        viewholder.txtContent.setText(history.getStatus());
        viewholder.txtDate.setText(history.getDate());

        return view;
    }
}
