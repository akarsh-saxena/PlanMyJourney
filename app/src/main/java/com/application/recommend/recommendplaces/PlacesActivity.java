package com.application.recommend.recommendplaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PlacesActivity extends AppCompatActivity {

    Values values = new Values();
    public static final String BASE_URL = "https://api.foursquare.com/v2/";
    public final String EXPLORE_REQUEST = "venues/explore?client_id="+values.getCLIENT_ID()+"&client_secret="+values.getCLIENT_SECRET();
    public final String LAT_LANG_REQUEST = BASE_URL+EXPLORE_REQUEST+"&ll=";
    String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

    RequestQueue requestQueue;
    RecyclerView placesRecyclerView;
    LinearLayoutManager linearLayoutManager;
    PlacesAdapter placesAdapter;
    ArrayList<PlacesModel>  placesModels = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        String lat = getIntent().getExtras().getString("lat");
        String longi = getIntent().getExtras().getString("longi");
        /*String lat = "19.0817112";
        String longi = "72.83105";*/

        String categoryId="";

        switch (getIntent().getExtras().getString("placeType")) {

            case "food":
                categoryId="4bf58dd8d48988d1f9941735";
                break;

            case "coffee":
                categoryId="4bf58dd8d48988d1e0931735";
                break;

            case "shops":
                categoryId = "4d4b7105d754a06378d81259";
                break;

            case "fun":
                categoryId = "4d4b7104d754a06370d81259";
        }

        requestQueue = Volley.newRequestQueue(this);
        placesRecyclerView = findViewById(R.id.placesRecyclerView);
        placesAdapter = new PlacesAdapter(this, placesModels);
        linearLayoutManager = new LinearLayoutManager(this);

        placesRecyclerView.setLayoutManager(linearLayoutManager);
        placesRecyclerView.setAdapter(placesAdapter);

        String url = LAT_LANG_REQUEST+lat+","+longi+"&categoryId="+categoryId+"&v="+date+"&sortByDistance=1";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getJSONObject("meta").getInt("code") != 400) {
                                JSONObject responseObject = response.getJSONObject("response");
                                    JSONArray itemsArray = responseObject.getJSONArray("groups").getJSONObject(0).getJSONArray("items");
                                    for(int i=0; i<itemsArray.length(); ++i) {
                                        PlacesModel placesModel = new PlacesModel();
                                        JSONObject itemsObject = itemsArray.getJSONObject(i);
                                        placesModel.setId(itemsObject.getJSONObject("venue").getString("id"));
                                        placesModel.setName(itemsObject.getJSONObject("venue").getString("name"));
                                        placesModel.setLat(itemsObject.getJSONObject("venue").getJSONObject("location").getString("lat"));
                                        placesModel.setLongi(itemsObject.getJSONObject("venue").getJSONObject("location").getString("lng"));
                                        JSONArray locationArray = itemsObject.getJSONObject("venue").getJSONObject("location").getJSONArray("formattedAddress");
                                        for(int j=0; j<locationArray.length(); ++j)
                                            placesModel.setAdd1(placesModel.getAdd1()+locationArray.getString(j)+"\n");
                                        if(!itemsObject.getJSONObject("venue").isNull("price")) {
                                            String price = itemsObject.getJSONObject("venue").getJSONObject("price").getString("message");
                                            placesModel.setPrice(price);
                                        }
                                        else
                                            placesModel.setPrice("null");

                                        if(!itemsObject.getJSONObject("venue").isNull("rating")) {
                                            placesModel.setRating(itemsObject.getJSONObject("venue").getString("rating"));
                                            placesModel.setRatingColor(itemsObject.getJSONObject("venue").getString("ratingColor"));
                                        }
                                        else
                                            placesModel.setRating("0");
                                        placesModels.add(placesModel);
                                        placesAdapter.notifyDataSetChanged();
                                    }
                                    Toast.makeText(PlacesActivity.this, "Total Items: "+placesModels.size(), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_plan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(PlacesActivity.this, JourneyPlan.class);
        intent.putExtra("BUNDLE", placesModels);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
