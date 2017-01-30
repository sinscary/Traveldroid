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
    private List<UserData> userData;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String origin;

    TextView textView;
    RequestQueue requestQueue;
    String url = "https://jsonplaceholder.typicode.com/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        recyclerView.setHasFixedSize(true);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                origin = String.valueOf(lat)+","+String.valueOf(lng);
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


        getData();

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    private void getData(){
        userData = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        parseJson(response);
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

    private void parseJson(JSONArray response){
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject users = response.getJSONObject(i);

                String id = ("id: "+users.getString("id"));
                String name = ("Name: "+users.getString("name"));
                String username = ("Username: "+users.getString("username"));
                String email = ("Email: "+users.getString("email"));
                String address = parseAddress(users);
                String destination = parseCoordinates(users);
                String company = parseCompany(users);
                String phone = ("Phone: "+users.getString("phone"));
                String website = ("Website: "+users.getString("website"));
                getEtarrive(origin, destination, i);

                UserData udata = new UserData(id, name, username, email, address, phone, website, company, "loading....");
                userData.add(udata);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new CustomAdapter(this, userData);
        recyclerView.setAdapter(adapter);

    }


    private String parseAddress(JSONObject users) {
        JSONObject completeAdd = null;
        String address = null;
        try {
            completeAdd = users.getJSONObject("address");
            String street = completeAdd.getString("street");
            String suite = completeAdd.getString("suite");
            String city = completeAdd.getString("city");
            String zipcode = completeAdd.getString("zipcode");
            address = ("Address: " + street + ", " + suite + ", " + city + ", " + zipcode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return address;
    }

    private String parseCoordinates(JSONObject users) {
        JSONObject completeAdd = null;
        String destination = null;
        try {
            completeAdd = users.getJSONObject("address");
            JSONObject coordinates = completeAdd.getJSONObject("geo");
            String latitude = coordinates.getString("lat");
            String longitude = coordinates.getString("lng");
            destination = latitude + "," + longitude;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return destination;
    }

    private String parseCompany(JSONObject users) {
        JSONObject companyDetail = null;
        String company = null;
        try {
            companyDetail = users.getJSONObject("company");
            String company_name = companyDetail.getString("name");
            String catchPhrase = companyDetail.getString("catchPhrase");
            String bs = companyDetail.getString("bs");
            company = ("Company: " + company_name + ", " + catchPhrase + ", " + bs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return company;
    }


    private void getEtarrive(String origin, String destination, final int index){
        String API = "AIzaSyD-tUsXFylNZrJwIHCf4fagJ1kjEUZjInM", mode="driving";
        String google_api = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+origin+"&destinations="
                +destination+"s&mode="+mode+"&language=fr-FR&key="+API;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, google_api, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String eta = parseGoogleData(response);
                        userData.get(index).setEta("ETA: "+eta);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }

        );
        requestQueue.add(jsonObjectRequest);
    }

    private String parseGoogleData(JSONObject response) {
        String estimated_time_arrival = null;
        try {
            JSONArray rows = response.getJSONArray("rows");

            JSONObject elements = rows.getJSONObject(0);
            JSONArray elementsArr = elements.getJSONArray("elements");

            JSONObject durationObj = elementsArr.getJSONObject(0);
            JSONObject durationData = durationObj.getJSONObject("duration");
            estimated_time_arrival = durationData.getString("text");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return estimated_time_arrival;
    }

}
