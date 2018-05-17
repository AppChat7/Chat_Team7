//package com.example.vinhtruong.chatapp_team7.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//import android.widget.Toast;
//
//
//import com.example.vinhtruong.chatapp_team7.Models.Conversation;
//
//import java.util.ArrayList;
//
///**
// * Created by CR7 on 4/15/2018.
// */
//
//public class DBManager extends SQLiteOpenHelper {
//    public static final String DATABASE_NAME ="chatapp";
//    private static final String TABLE_CONVERSATION ="conversation";
//
//    private static final String ID_CONVERSATION ="id_conversation";
//    private static final String AVA_SEND ="avatar";
//    private static final String FNAME_SEND ="fname";
//    private static final String LNAME_SEND ="lname";
//    private static final String TIME_SEND ="time_send";
//    private static final String LAST_MESSAGE ="last_message";
//    private static final String TABLE_USER ="user";
//
//    private static final String ID_USER ="id_user";
//    private static final String EMAIL ="email";
//    private static final String PASSWORD ="password";
//    private static final String USER_AVA ="avatar";
//    private static final String LNAME ="lname";
//    private static final String FNAME ="fname";
//    private static final String BIRTHDAY ="birthday";
//    private static final String GENDER ="gender";
//    private static final String SDT ="sdt";
//
//
//
//    private Context context;
//
//
//
//    public DBManager(Context context) {
//        super(context, DATABASE_NAME,null, 1);
//        Log.d("DBManager", "DBManager: ");
//        this.context = context;
//    }
//
//
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        String sqlQueryUSER = "CREATE TABLE "+TABLE_USER +" (" +
//                ID_USER +" TEXT PRIMARY KEY , "+
//                EMAIL +" TEXT , "+
//                PASSWORD + " TEXT, "+
//                USER_AVA+" TEXT," +
//                LNAME +" TEXT, "+
//                FNAME+" TEXT," +
//                BIRTHDAY+" TEXT," +
//                GENDER+" INT," +
//                SDT +" TEXT)";
//        String sqlQueryMESSAGE = "CREATE TABLE "+TABLE_CONVERSATION +" (" +
//                ID_CONVERSATION +" TEXT PRIMARY KEY, "+
//                AVA_SEND + " TEXT, "+
//                FNAME_SEND +" TEXT, "+
//                LNAME_SEND+" TEXT," +
//                TIME_SEND+" TEXT," +
//                LAST_MESSAGE+" TEXT)";
//
//        sqLiteDatabase.execSQL(sqlQueryUSER);
//        sqLiteDatabase.execSQL(sqlQueryMESSAGE);
//        Toast.makeText(context, "Create successfylly", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_CONVERSATION);
//        onCreate(sqLiteDatabase);
//        Toast.makeText(context, "Drop successfylly", Toast.LENGTH_SHORT).show();
//    }
//
//    //Add new a User
//    public void addUser(User user){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(ID_USER, user.getIdUser());
//        values.put(EMAIL, user.getEmail());
//        values.put(PASSWORD, user.getPass());
//        values.put(USER_AVA, user.getAvatar());
//        values.put(LNAME, user.getlName());
//        values.put(FNAME, user.getfName());
//        values.put(BIRTHDAY, user.getBirthday());
//        values.put(GENDER, user.getGender());
//        values.put(SDT, user.getSdt());
//
//        long row = db.insert(TABLE_USER, null, values);
//        if(row<0){
//            Toast.makeText(context, "Can't insert " + user.getfName(), Toast.LENGTH_SHORT).show();
//        }else
//        {
//            Toast.makeText(context, "Insert succesfull!!! " + user.getfName(), Toast.LENGTH_SHORT).show();
//        }
//        db.close();
//    }
//    public void updateUser(User user, String id){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(ID_USER, user.getIdUser());
//        values.put(EMAIL, user.getEmail());
//        values.put(PASSWORD, user.getPass());
//        values.put(USER_AVA, user.getAvatar());
//        values.put(LNAME, user.getlName());
//        values.put(FNAME, user.getfName());
//        values.put(BIRTHDAY, user.getBirthday());
//        values.put(GENDER, user.getGender());
//        values.put(SDT, user.getSdt());
//        try{
//            long row= db.update(TABLE_USER,values,ID_USER +"=?",new String[]{id});
//            Toast.makeText(context, "Update Thành Công !!!!" + ID_USER, Toast.LENGTH_SHORT).show();
//        }catch (Exception ex){
//            Toast.makeText(context, "Không thể update!!! Because: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        db.close();
//
//    }
//
//    //Add new Conversation
//    public void addConversation(Conversation conver){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(ID_CONVERSATION, conver.getIDMessage());
//        values.put(AVA_SEND, conver.getImage());
//        values.put(FNAME_SEND, conver.getFname());
//        values.put(LNAME_SEND, conver.getLname());
//        values.put(TIME_SEND, conver.getTime());
//        values.put(LAST_MESSAGE, conver.getLastMessage());
//
//        long row = db.insert(TABLE_CONVERSATION, null, values);
//        if(row<0){
//            Toast.makeText(context, "Can't insert " + conver.getLastMessage(), Toast.LENGTH_SHORT).show();
//        }else
//        {
//            Toast.makeText(context, "Insert succesfull!!! " + conver.getLastMessage(), Toast.LENGTH_SHORT).show();
//        }
//        db.close();
//    }
//    public ArrayList<Conversation> getAllConversation(String idReceive){
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT * FROM "+ TABLE_CONVERSATION+"";
//        Log.e( "getAllConversation: ",""+ selectQuery );
//        Cursor cursor =db.rawQuery( selectQuery,null);
//        ArrayList<Conversation> arrConver= new ArrayList<>();
//        while(cursor.moveToNext()){
//            String IDMessage = cursor.getString(0);
//             String image= cursor.getString(1);
//             String fname= cursor.getString(2);
//             String lname= cursor.getString(3);
//             String time= cursor.getString(4);
//             String lastMessage= cursor.getString(5);
//            Conversation conver= new Conversation( IDMessage,  image,  fname,  lname, time,lastMessage,  false);
//            arrConver.add(conver);
//        }
//        cursor.close();
//        return arrConver;
//    }
//   public void updateConver(Conversation conver, String id){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//           values.put(ID_CONVERSATION, conver.getIDMessage());
//           values.put(AVA_SEND, conver.getImage());
//           values.put(FNAME_SEND, conver.getFname());
//           values.put(LNAME_SEND, conver.getLname());
//           values.put(TIME_SEND, conver.getTime());
//           values.put(LAST_MESSAGE, conver.getLastMessage());
//        try{
//            long row= db.update(TABLE_CONVERSATION,values,ID_CONVERSATION +"=?",new String[]{id});
//            Toast.makeText(context, "Update Thành Công !!!!" + conver.getFname() +": "+ conver.getLastMessage(), Toast.LENGTH_SHORT).show();
//        }catch (Exception ex){
//            Toast.makeText(context, "Không thể update! because: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        db.close();
//
//    }
//    public void deleteConver(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        try{
//            long row= db.delete(TABLE_CONVERSATION, ID_CONVERSATION + " = ?", new String[] { id });
//            Toast.makeText(context, "Xóa Thành Công !!!!" , Toast.LENGTH_SHORT).show();
//        }catch (Exception ex){
//            Toast.makeText(context, "Không thể xóa, vì : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        db.close();
//    }
//
//
//}
