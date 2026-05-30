package com.example.trackpoint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private double currentLatitude;
    private double currentLongitude;
    private double currentAltitude;
    private float currentAccuracy;
    private RequestQueue requestQueue;
    private TextView tvLocationDisplay;
    private TextView tvStatus;

    private String serverEndpoint = "http://10.0.2.2/localisation/createPosition.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocationDisplay = findViewById(R.id.tv_location_display);
        tvStatus = findViewById(R.id.tv_status);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE
                    }, 1);
            return;
        }

        startLocationUpdates(locationManager);
    }

    private void startLocationUpdates(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60000,
                150,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        currentAltitude = location.getAltitude();
                        currentAccuracy = location.getAccuracy();

                        String locationText = getString(R.string.new_location,
                                String.valueOf(currentLatitude),
                                String.valueOf(currentLongitude),
                                String.valueOf(currentAltitude),
                                String.valueOf(currentAccuracy));

                        tvLocationDisplay.setText(locationText);
                        tvStatus.setText(getString(R.string.sending));
                        Toast.makeText(getApplicationContext(),
                                locationText, Toast.LENGTH_LONG).show();

                        sendCoordinates(currentLatitude, currentLongitude);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        String statusText = "";
                        switch (status) {
                            case LocationProvider.OUT_OF_SERVICE:
                                statusText = "Hors service";
                                break;
                            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                                statusText = "Temporairement indisponible";
                                break;
                            case LocationProvider.AVAILABLE:
                                statusText = "Disponible";
                                break;
                        }
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.provider_new_status, provider, statusText),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.provider_enabled, provider),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.provider_disabled, provider),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void sendCoordinates(final double lat, final double lon) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                serverEndpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tvStatus.setText(getString(R.string.send_success));
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.send_success),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvStatus.setText(getString(R.string.send_error));
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.send_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                TelephonyManager telephonyManager =
                        (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                HashMap<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date_position", sdf.format(new Date()));

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    params.put("imei", "unknown");
                } else {
                    params.put("imei", telephonyManager.getDeviceId());
                }

                return params;
            }
        };

        requestQueue.add(request);
    }
}