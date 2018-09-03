package com.application.recommend.recommendplaces;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PlaceDetails extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat=0;
    double longi=0;
    public static final String BASE_URL = "https://api.foursquare.com/v2/";
    String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    RequestQueue requestQueue;

    TextView tvAddress, tvRating, tvOpen, tvContact;
    RatingBar ratingBar;
    LinearLayout llRating;
    RecyclerView imagesRecyclerView;
    GridLayoutManager gridLayoutManager;
    ImagesAdapter imagesAdapter;
    ArrayList<ImagesModel> imagesModels = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        ArrayList<PlacesModel> placesModels = (ArrayList<PlacesModel>) getIntent().getSerializableExtra("BUNDLE");
        int position = getIntent().getExtras().getInt("position");

        lat = Double.parseDouble(placesModels.get(position).getLat());
        longi = Double.parseDouble(placesModels.get(position).getLongi());
        requestQueue = Volley.newRequestQueue(this);
        tvAddress = findViewById(R.id.tvAddress);
        tvRating = findViewById(R.id.tvRating);
        tvOpen = findViewById(R.id.tvOpen);
        ratingBar = findViewById(R.id.ratingBar);
        llRating = findViewById(R.id.llRating);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        tvContact = findViewById(R.id.tvContact);
        gridLayoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
        imagesAdapter = new ImagesAdapter(imagesModels, this);
        imagesRecyclerView.setLayoutManager(gridLayoutManager);
        imagesRecyclerView.setAdapter(imagesAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(placesModels.get(position).getName());
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Values values = new Values();
        String url = BASE_URL+"venues/"+placesModels.get(position).getId()+"?&client_id="+values.getCLIENT_ID()+"&client_secret="+values.getCLIENT_SECRET()+"&v="+date;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject("response");
                            JSONObject venueObject = responseObject.getJSONObject("venue");
                            if(venueObject.has("hours")) {
                                JSONObject hoursObject = venueObject.getJSONObject("hours");
                                if (hoursObject.getString("isOpen").equals("true")) {
                                    Toast.makeText(PlaceDetails.this, "Open", Toast.LENGTH_SHORT).show();
                                    tvOpen.setText(hoursObject.getString("status"));
                                    tvOpen.setBackgroundColor(Color.GREEN);
                                } else {
                                    JSONArray timeframesArray = hoursObject.getJSONArray("timeframes");
                                    for (int i = 0; i < timeframesArray.length(); ++i) {
                                        JSONObject timeObject = timeframesArray.getJSONObject(i);
                                        if (timeObject.get("includesToday").equals("true")) {
                                            String time = timeObject.getJSONArray("open").getJSONObject(0).getString("renderedTime");
                                            tvOpen.setText(time);
                                            tvOpen.setBackgroundColor(Color.GREEN);
                                        } else {
                                            tvOpen.setText("We're closed this time");
                                            tvOpen.setTextColor(Color.WHITE);
                                            tvOpen.setBackgroundColor(Color.RED);
                                        }
                                    }
                                }
                            }
                            else {
                                tvOpen.setVisibility(View.GONE);
                            }
                            JSONObject photosObject = venueObject.getJSONObject("photos");
                            if(photosObject.getInt("count")>0) {
                                JSONArray groupsArray = photosObject.getJSONArray("groups");
                                for (int i=0; i<groupsArray.length(); ++i) {
                                    JSONObject groupsObject = groupsArray.getJSONObject(i);
                                    JSONArray itemsArray = groupsObject.getJSONArray("items");
                                    for(int j=0; j<itemsArray.length(); ++j) {
                                        JSONObject itemsObject = itemsArray.getJSONObject(j);
                                        ImagesModel imagesModel = new ImagesModel();
                                        imagesModel.setPrefix(itemsObject.getString("prefix"));
                                        imagesModel.setSuffix(itemsObject.getString("suffix"));
                                        imagesModel.setWidth(itemsObject.getString("width"));
                                        imagesModel.setHeight(itemsObject.getString("height"));
                                        imagesModels.add(imagesModel);
                                        imagesAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            JSONObject contactObject = venueObject.getJSONObject("contact");
                            if(!TextUtils.isEmpty(contactObject.getString("formattedPhone")))
                                tvContact.setText(contactObject.getString("formattedPhone"));
                            else
                                if(!TextUtils.isEmpty(contactObject.getString("phone")))
                                    tvContact.setText(contactObject.getString("phone"));
                                else
                                    tvContact.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                });

        requestQueue.add(jsonObjectRequest);

        if(placesModels.get(position).getRating().equals("0"))
            llRating.setVisibility(View.GONE);
        else {
            float rating = Float.parseFloat(placesModels.get(position).getRating())/2;
            ratingBar.setRating(rating);
            tvRating.setText(String.valueOf(rating));
            tvRating.setBackgroundColor(Color.parseColor("#"+placesModels.get(position).getRatingColor()));
        }

        tvAddress.setText(placesModels.get(position).getAdd1());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, longi);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLoadedCallback(
                new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 15));
                    }
                }
        );

    }
}
