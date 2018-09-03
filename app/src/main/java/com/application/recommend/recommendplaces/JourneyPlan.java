package com.application.recommend.recommendplaces;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JourneyPlan extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Location> locations = new ArrayList<>();
    ArrayList<PlacesModel> placesModels;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_plan);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        placesModels = (ArrayList<PlacesModel>) getIntent().getSerializableExtra("BUNDLE");
        for(int i=0; i<placesModels.size(); ++i) {
            Location location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(Double.parseDouble(placesModels.get(i).getLat()));
            location.setLongitude(Double.parseDouble(placesModels.get(i).getLongi()));
            locations.add(location);
        }
        drawPrimaryLinePath(locations);
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

        mMap.setOnMapLoadedCallback(
                new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        drawPrimaryLinePath(locations);
                        progressDialog.dismiss();
                    }
                }
        );
        mMap.setOnPolylineClickListener(
                new GoogleMap.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        List<LatLng> latLngs = polyline.getPoints();
                        Log.d("muy", latLngs.size()+"");
                        for(LatLng latLng : latLngs)
                            Log.d("muy", latLng.toString());
                        getDistanceDetails(polyline.getPoints());
                    }
                }
        );
    }
    //final List<String> distanceDetails = new ArrayList<>();
    private void getDistanceDetails(final List<LatLng> latLngs) {
        String origin = latLngs.get(0).latitude+","+latLngs.get(0).longitude;
        String dest = latLngs.get(1).latitude+","+latLngs.get(1).longitude;
        String urlDriving = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + dest + "&mode=driving&key=AIzaSyDKPWqssYRY-vBHAvH3WIvDJSlUDIwScRc";
        Log.d("muy", urlDriving);
        final AlertDialog.Builder alert = new AlertDialog.Builder(JourneyPlan.this);
        JsonObjectRequest objectRequestDriving =
                new JsonObjectRequest(Request.Method.GET, urlDriving, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("muy", "onResponse() called");
                            JSONArray jsonArray = response.getJSONArray("rows");
                            String status = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).get("status").toString();
                            if (TextUtils.equals(status, "ZERO_RESULTS")) {
                                Log.d("muy", "no data");
                                alert.setTitle("Error");
                                alert.setMessage("No Data!!");
                                alert.show();
      //                          distanceDetails.add("No data");
                                return;
                            }
                            JSONObject object = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
                            String distance = object.getString("text");
                            JSONObject object1 = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration");
                            String duration = object1.getString("text");
                            /*distanceDetails.add(distance);
                            distanceDetails.add(duration);*/
                            Log.d("muy", distance);
                            Log.d("muy", duration);
                            /*double lat = (latLngs.get(0).latitude+latLngs.get(1).latitude)/2;
                            double lng = (latLngs.get(0).longitude+latLngs.get(1).longitude)/2;
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(distance).snippet(duration));*/
                            alert.setTitle("Distance: "+distance);
                            alert.setMessage("Duration: "+duration);
                            alert.show();
                        } catch (JSONException e) {
                            Log.d("muy", "catch");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("muy", "error");
                        error.printStackTrace();
                        Toast.makeText(JourneyPlan.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(objectRequestDriving);
        //Log.d("muy",distanceDetails.size()+" size");
        /*Log.d("muy",distanceDetails.get(0));
        Log.d("muy",distanceDetails.get(1));*/
    }


    private void drawPrimaryLinePath( ArrayList<Location> listLocsToDraw )
    {
        if ( mMap == null )
            return;

        if ( listLocsToDraw.size() < 2 )
            return;

        listLocsToDraw.add(listLocsToDraw.get(0));
        //options.clickable(true);
        LatLng latLng;
        PolylineOptions options = new PolylineOptions();
        List<LatLng> latLngs = new ArrayList<>();

        for (int i=0; i<listLocsToDraw.size(); ++i) {

            if(i%2 == 0 || i>1) {
                options = new PolylineOptions();
                //Log.d("muy", options.getPoints().size()+"");
                options.color(Color.RED);
                options.width( 20 );
                options.visible( true );
            }

            if(i>1)
                options.add(latLngs.get(latLngs.size()-1));

            //options.color( Color.parseColor( "#CC0000FF" ) );
            Location locRecorded = listLocsToDraw.get(i);
            latLng = new LatLng(locRecorded.getLatitude(),locRecorded.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(placesModels.get(i%placesModels.size()).getName()).snippet((i+1)+""));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            options.add(new LatLng(locRecorded.getLatitude(), locRecorded.getLongitude()));
            latLngs.add(new LatLng(locRecorded.getLatitude(), locRecorded.getLongitude()));
            Log.d("muy", options.getPoints().size()+""+", i: "+i);
            if(i%2 != 0 || i>1) {
                Polyline polyline = mMap.addPolyline(options);
                polyline.setClickable(true);
                Log.d("muy", "i: "+i);
                /*Log.d("muy", "Options Size: "+options.getPoints().size());
                Log.d("muy", "options points: "+options.getPoints().toString());*/
                Log.d("muy", "polyline Size: "+polyline.getPoints().size());
                Log.d("muy", "drawing line"+polyline.getPoints().toString());
            }
        }
    }
}
