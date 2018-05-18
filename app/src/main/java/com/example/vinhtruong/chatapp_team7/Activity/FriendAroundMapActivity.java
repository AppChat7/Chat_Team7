package com.example.vinhtruong.chatapp_team7.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.vinhtruong.chatapp_team7.Fragments.FragmentFriends;
import com.example.vinhtruong.chatapp_team7.Models.Users;
import com.example.vinhtruong.chatapp_team7.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collection;

public class FriendAroundMapActivity extends AppCompatActivity {
    //Khai báo đối tượng Google Map
    GoogleMap mMap;
    public static int PERMISSION_ALL = 1;
    ArrayList<LatLng> arrPosition = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    LatLng mylocation;
    ArrayList<Users> users = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_around);
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        FragmentFriends.check=1;
        if (!hasPermissions(FriendAroundMapActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
//        users.add(new Users("super woman", " ", "","https://i.pinimg.com/originals/e0/39/51/e03951dd11f06f3405e4e9905293bcc0.jpg", 10.849622, 106.787925));
//        users.add(new Users("super man", " ", "","http://i.f1g.fr/media/ext/1200x1200/madame.lefigaro.fr/sites/default/files/img/2011/03/en-prive-avec-eva-green0jpg_0.jpg", 10.931946, 106.828128));
//        users.add(new Users("bat man", " ", "","http://www.elle.vn/wp-content/uploads/2018/03/27/eva-green-phong-cach-trang-diem-quyen-ru-7.jpg", 10.847956, 106.776341));
        Intent intent = getIntent();
        users.addAll((Collection<? extends Users>) intent.getSerializableExtra("listUser"));
        initViews();
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
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
    public static double distanceBetween2Points(double la1, double lo1,
                                                double la2, double lo2) {
        double dLat = (la2 - la1) * (Math.PI / 180);
        double dLon = (lo2 - lo1) * (Math.PI / 180);
        double la1ToRad = la1 * (Math.PI / 180);
        double la2ToRad = la2 * (Math.PI / 180);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(la1ToRad)
                * Math.cos(la2ToRad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6372.797 * c;
        return d;
    }
    private void initViews() {
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id
                        .fragment_map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        if (ActivityCompat.checkSelfPermission(FriendAroundMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FriendAroundMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(FriendAroundMapActivity.this);
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(FriendAroundMapActivity.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            // Logic to handle location object
                                            mylocation = new LatLng(location.getLatitude(), location.getLongitude());

                                        } else {
                                            mylocation = new LatLng(10.848501, 106.786544);
                                        }
                                        mMap.addMarker(new MarkerOptions()
                                                .position(mylocation)
                                                .title("Marker in MyLocation")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.myloca))
                                        );
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 12));
                                        Log.e("Last Know location: ", location.getLatitude() + " " + location.getLongitude());
                                        mMap.addCircle(new CircleOptions()
                                                .center(new LatLng(mylocation.latitude, mylocation.longitude))
                                                .radius(5000) // 5km
                                                .strokeColor(Color.BLUE).strokeWidth(2)
                                                .fillColor(Color.TRANSPARENT));
                                        mMap.addCircle(new CircleOptions()
                                                .center(new LatLng(mylocation.latitude, mylocation.longitude))
                                                .radius(10000) // 10km
                                                .strokeColor(Color.RED).strokeWidth(2)
                                                .fillColor(Color.TRANSPARENT));

                                        for(Users user : users){
                                            double kc =distanceBetween2Points(mylocation.latitude, mylocation.longitude,user.getLat(),user.getLon());
                                            Log.e("kc "+user.getName(), ": "+kc  );

                                            if(kc<10){
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(user.getLat(),user.getLon()))
                                                        .title(user.getName())
                                                );
                                            }


                                        }

                                    }
                                });

                    }
                });



                if (ActivityCompat.checkSelfPermission(FriendAroundMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FriendAroundMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Toast.makeText(FriendAroundMapActivity.this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
                        // Return false so that we don't consume the event and the default behavior still occurs
                        // (the camera animates to the user's current position).
                        return true;

                    }
                });



            }
        });
    }

    private void addEvent() {

    }


}
