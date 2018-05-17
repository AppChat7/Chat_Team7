package com.example.vinhtruong.chatapp_team7.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vinhtruong on 5/14/2018.
 */

public class Database extends SQLiteOpenHelper {
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    //truy vấn không trả KQ: CREATE INSERT UPDATE DELETE ...
    public void QueryData(String truyvan){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL(truyvan);
    }
    //truy vấn trả KQ: SELECT
    public Cursor GetData(String truyvan){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(truyvan,null);
    }

}