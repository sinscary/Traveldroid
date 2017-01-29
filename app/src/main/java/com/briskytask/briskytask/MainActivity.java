package com.briskytask.briskytask;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomAdapter adapter;
    private List<UserData> u_data;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String origin, destination, mode = "driving";
    private String API = "AIzaSyD-tUsXFylNZrJwIHCf4fagJ1kjEUZjInM";
    TextView textView;
    RequestQueue requestQueue;
    RequestQueue requestQueue1;
    String url = "https://jsonplaceholder.typicode.com/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /*textView = (TextView) findViewById(R.id.timetravel);*/
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                origin = String.valueOf(lat)+","+String.valueOf(lng);
                /*textView.setText(String.valueOf(lat)+" "+String.valueOf(lng));*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            return;
        }
        else{
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }

        u_data = new ArrayList<>();

        get_data();

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CustomAdapter(this, u_data);
        recyclerView.setAdapter(adapter);

    }

    private void get_data(){
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject users = response.getJSONObject(i);

                                String id = ("id: "+users.getString("id"));
                                String name = ("Name: "+users.getString("name"));
                                String username = ("Username: "+users.getString("username"));
                                String email = ("Email: "+users.getString("email"));


                                JSONObject completeAdd = users.getJSONObject("address");
                                String street = completeAdd.getString("street");
                                String suite = completeAdd.getString("suite");
                                String city = completeAdd.getString("city");
                                String zipcode = completeAdd.getString("zipcode");
                                String address = ("Address :"+street+", "+suite+", "+city+", "+zipcode);

                                JSONObject coordinates = completeAdd.getJSONObject("geo");
                                String latitude = coordinates.getString("lat");
                                String longitude = coordinates.getString("lng");
                                destination = latitude+","+longitude;
                                get_time_to_travel(origin, destination, API, mode);
                                //String time_to_travel = ("ETA: " );
                                //System.out.println(time_arrival);


                                String phone = ("Phone: "+users.getString("phone"));
                                String website = ("Website: "+users.getString("website"));


                                JSONObject companyDetail = users.getJSONObject("company");
                                String company_name = companyDetail.getString("name");
                                String catchPhrase = companyDetail.getString("catchPhrase");
                                String bs = companyDetail.getString("bs");
                                String company = ("Company: "+company_name+", "+catchPhrase+", "+bs);

                                UserData udata = new UserData(id, name, username, email, address, phone, website, company);
                                u_data.add(udata);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void get_time_to_travel(String origin, String destination, String API, String mode){
        requestQueue1 = Volley.newRequestQueue(this);
        String google_api = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+origin+"&destinations="+destination+"s&mode="+mode+"&language=fr-FR&key="+API;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, google_api, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray rows = response.getJSONArray("rows");

                            JSONObject elements = rows.getJSONObject(0);

                            JSONArray elementsArr = elements.getJSONArray("elements");
                            JSONObject durationObj = elementsArr.getJSONObject(0);
                            JSONObject durationArray = durationObj.getJSONObject("duration");

                            //JSONObject duration = elements.getJSONObject(1);
                            String time_arrival = durationArray.getString("text");
                            TextView textView = (TextView) findViewById(R.id.timetravel);
                            textView.setText("ETA: "+time_arrival);
                            //System.out.println(time_arrival);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }

        );
        requestQueue1.add(jsonObjectRequest);


    }
}
