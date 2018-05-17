package com.example.vinhtruong.chatapp_team7.Activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.example.vinhtruong.chatapp_team7.Adapter.HistoryAdapter;
import com.example.vinhtruong.chatapp_team7.Models.History;
import com.example.vinhtruong.chatapp_team7.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    private ListView lvHistory;
    HistoryAdapter adapter;
    ArrayList<History> historyArrayList;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolbar=findViewById(R.id.historyToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvHistory=findViewById(R.id.lvHistory);
        historyArrayList=new ArrayList<>();
        adapter=new HistoryAdapter(HistoryActivity.this,historyArrayList,R.layout.custom_history_line);
        lvHistory.setAdapter(adapter);

        Cursor dataHistory=MainActivity.database.GetData("SELECT * FROM History");
        while (dataHistory.moveToNext()){

            String status = dataHistory.getString(1);
            String date = dataHistory.getString(2);

            History history=new History(status,date);

            historyArrayList.add(history);
            adapter.notifyDataSetChanged();
        }

    }
}
