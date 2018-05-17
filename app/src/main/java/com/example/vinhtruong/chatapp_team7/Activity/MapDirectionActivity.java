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
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinhtruong.chatapp_team7.Models.use_map.Directions;
import com.example.vinhtruong.chatapp_team7.R;
import com.example.vinhtruong.chatapp_team7.Rest.APIService;
import com.example.vinhtruong.chatapp_team7.Rest.RetrofitClientMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapDirectionActivity extends AppCompatActivity {
    double Friend_lat, Friend_lon;
    GoogleMap mMap;
    public static int PERMISSION_ALL = 1;
    ArrayList<LatLng> arrPosition = new ArrayList<>();
    Polyline line;
    private FusedLocationProviderClient mFusedLocationClient;
    LatLng mylocation;
    TextView txtDistance, txtDuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_direction);

        addControls();
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (!hasPermissions(MapDirectionActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        initViews();
    }
    private void addControls() {
        txtDistance = findViewById(R.id.txtDistance);
        txtDuration = findViewById(R.id.txtDuration);
        Intent intent= getIntent();
         Friend_lat = intent.getDoubleExtra("lat",0);
        Log.e( "Friend_lat", ""+ Friend_lat);
        Friend_lon = intent.getDoubleExtra("lon",0);
        Log.e( "Friend_lon", ""+ Friend_lon);
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
                        if (ActivityCompat.checkSelfPermission(MapDirectionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapDirectionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapDirectionActivity.this);
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(MapDirectionActivity.this, new OnSuccessListener<Location>() {
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
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15));
                                        Log.e("Last Know location: ", location.getLatitude() + " " + location.getLongitude());

                                        loadData(Friend_lat, Friend_lon);
                                    }
                                });

                    }
                });

                if (ActivityCompat.checkSelfPermission(MapDirectionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapDirectionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        Toast.makeText(MapDirectionActivity.this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
                        // Return false so that we don't consume the event and the default behavior still occurs
                        // (the camera animates to the user's current position).
                        return true;

                    }
                });


            }
        });
    }
    private void loadData(double friend_lat,double friend_lon) {
         APIService apiService2 = RetrofitClientMap.getClient(RetrofitClientMap.BASE_MAP).create(APIService.class);
        Call<Directions> callDirections = apiService2.getDistanceDuration("metric", mylocation.latitude + "," + mylocation.longitude, friend_lat + "," + friend_lon, "driving");

        Log.e("URL", callDirections.request().url() + " ");
        callDirections.enqueue(new Callback<Directions>() {
            @Override
            public void onResponse(Call<Directions> call, Response<Directions> response) {

                try {
                    //Remove previous line from map
                    if (line != null) {
                        line.remove();
                    }
                    Directions directions = response.body();
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < directions.getRoutes().size(); i++) {
                        String distance = directions.getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        Log.e("Distance: ", "Distance:" + distance + ", Duration:" + time);
                        txtDuration.setText("Duration: " + time);
                        txtDistance.setText("Distance: " + distance);

                        String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = decodePoly(encodedString);
                        line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(5)
                                .color(Color.BLACK)
                                .geodesic(true)
                        );
                        LatLng endPoint = new LatLng(list.get(list.size() - 1).latitude, list.get(list.size() - 1).longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(endPoint)
                                .title("Marker in end point")
                        );
//                        LatLng start = new LatLng(list.get(0).latitude, list.get(0).longitude);
//                        mMap.addMarker(new MarkerOptions()
//                                .position(start)
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                                .title("Marker in start")
//                        );

                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
            }
        }

            @Override
            public void onFailure(Call<Directions> call, Throwable t) {
                Log.e("onFailure: ", "something fail ");
                Toast.makeText(MapDirectionActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
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
}
