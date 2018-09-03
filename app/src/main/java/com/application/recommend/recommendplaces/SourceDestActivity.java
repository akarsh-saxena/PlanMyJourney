package com.application.recommend.recommendplaces;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SourceDestActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvLocation;
    EditText etSearch;
    ImageButton ibSearch;
    ImageView ivFood, ivCafe, ivFun, ivShopping;
    LocationListener locationListener;
    LocationManager locationManager;
    double lat = 0;
    double longi = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_dest);

        etSearch = findViewById(R.id.etSearch);
        tvLocation = findViewById(R.id.tvLocation);
        ibSearch = findViewById(R.id.ibSearch);
        ivFood = findViewById(R.id.ivFood);
        ivCafe = findViewById(R.id.ivCafe);
        ivFun = findViewById(R.id.ivFun);
        ivShopping = findViewById(R.id.ivShopping);

        ibSearch.setOnClickListener(this);
        ivFood.setOnClickListener(this);
        ivCafe.setOnClickListener(this);
        ivFun.setOnClickListener(this);
        ivShopping.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Toast.makeText(SourceDestActivity.this, "You are in: " + addressList.get(0).getSubAdminArea() + ", " + addressList.get(0).getAdminArea(), Toast.LENGTH_LONG).show();
                    lat = location.getLatitude();
                    longi = location.getLongitude();
                    tvLocation.setText(addressList.get(0).getLocality());
                    progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1000, locationListener);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(SourceDestActivity.this, PlacesActivity.class);
        intent.putExtra("lat", String.valueOf(lat));
        intent.putExtra("longi", String.valueOf(longi));

        switch (view.getId()) {

            case R.id.ivFood:
                intent.putExtra("placeType", "food");
                startActivity(intent);
                break;

            case R.id.ivCafe:
                intent.putExtra("placeType", "coffee");
                startActivity(intent);
                break;

            case R.id.ivFun:
                intent.putExtra("placeType", "fun");
                startActivity(intent);
                break;

            case R.id.ivShopping:
                intent.putExtra("placeType", "shops");
                startActivity(intent);
                break;

        }
    }
}
