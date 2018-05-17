package com.example.vinhtruong.chatapp_team7.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.vinhtruong.chatapp_team7.Adapter.ViewPagerAdapter;
import com.example.vinhtruong.chatapp_team7.Models.Message;
import com.example.vinhtruong.chatapp_team7.Models.Users;
import com.example.vinhtruong.chatapp_team7.R;
import com.example.vinhtruong.chatapp_team7.Sqlite.Database;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static int PERMISSION_ALL = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    LatLng mylocation;
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    //Layout
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    //Adapter
    private ViewPagerAdapter mViewPagerAdapter;
    //SQLite
    public static Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        //Ánh Xạ
        AnhXa();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        //Tabs
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //SQLite
        database=new Database(this,"history.sqlite",null,1);
        database.QueryData("CREATE TABLE IF NOT EXISTS  History(id INTEGER PRIMARY KEY AUTOINCREMENT,status VARCHAR(200), date VARCHAR(100))");

        Cursor dataHistory=database.GetData("SELECT * FROM History");

        if (dataHistory.getCount()==0){
            String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            String status ="Hi there I am using Team7ChatApp.";
            database.QueryData("INSERT INTO History VALUES(null,'"+status+"','"+currentDate+"')");
        }


        //Toolbar & Navigation view
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Team7ChatApp");
        mToolbar.setNavigationIcon(R.drawable.navi);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuAccountSettings:
                        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                    case R.id.menuLogout:
                        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                        FirebaseAuth.getInstance().signOut();
                        sendToStart();
                        break;
                    case R.id.menuHistory:
                        Intent historyIntent = new Intent(MainActivity.this,HistoryActivity.class);
                        startActivity(historyIntent);
                        break;
                }
                return false;
            }
        });
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void AnhXa() {
        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        mViewPager = (ViewPager) findViewById(R.id.mainViewpager);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        drawerLayout=findViewById(R.id.drawerlayout);
        navigationView=findViewById(R.id.navigationview);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e( "onStart: ", "on start");
        // Kiểm tra người dùng đã đăng nhập chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        } else {
            View header =navigationView.getHeaderView(0);
            final CircleImageView imgAvatarNavi = header.findViewById(R.id.imgAvatarNavi);
            final TextView txtNameNavi = header.findViewById(R.id.txtNameNavi);
            TextView txtEmailNavi = header.findViewById(R.id.txtEmailNavi);
            mUserRef.child("online").setValue(0);
            detechLocation();
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name =dataSnapshot.child("name").getValue().toString();
                    txtNameNavi.setText(name);

                    String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.get().load(thumbImage).placeholder(R.drawable.user).into(imgAvatarNavi);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            String email = mAuth.getCurrentUser().getEmail();
            txtEmailNavi.setText(email);
        }

    }

    private void detechLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                        } else {
                            mylocation = new LatLng(10.848501, 106.786544);
                        }
                        Log.e("Last Know location: ", mylocation.latitude + " " + mylocation.longitude);
                        mUserRef.child("lat").setValue(mylocation.latitude);
                        mUserRef.child("lon").setValue(mylocation.longitude);
                    }
                });
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

}