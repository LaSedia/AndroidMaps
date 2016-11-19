package samplemodules.locationupdates;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


public class LocationReporter extends AppCompatActivity {

    private TextView TVReportLocation = null;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3646;
    private Button BtnGetAddress;
    private TextView TvAddress;
    private Double Latitude;
    private Double Longitude;
    Location lastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastLocation=null;
        setContentView(R.layout.activity_location_reporter);
        TVReportLocation = (TextView) findViewById(R.id.TVReportLocation);
        BtnGetAddress = (Button) findViewById(R.id.BtnGetAddress);
        TvAddress= (TextView) findViewById(R.id.TvAddress);
        BtnGetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder;
                List<Address> addresses;
                if (lastLocation != null) {
                    geocoder = new Geocoder(LocationReporter.this, Locale.getDefault());
// extracted from http://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
                    try {
                        addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName(); //
                        TvAddress.setText(address + " - " + city + " - " + state + " - " + country + "-" + postalCode + "-" + knownName);
                    } catch (Exception exception)
                    {
                        Log.d("Address Exemption","Exemption on geocoder");
                    }
                }
            }
        });
        // Acquire a reference to the system Location Manager
        askPermission();
    }
    @Override
    protected  void onStart()
    {
        super.onStart();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        if (ContextCompat.checkSelfPermission(LocationReporter.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        else
        {        Toast.makeText(this,"No permission Granted",Toast.LENGTH_LONG).show();
        }
    }
    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            makeUseOfNewLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    private void makeUseOfNewLocation(Location location)
    {
        TVReportLocation.setText("latitude"+location.getLatitude()+"Longitude"+location.getLongitude());
        lastLocation = location;
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();
    }

    private void askPermission()
    {
        if (ContextCompat.checkSelfPermission(LocationReporter.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LocationReporter.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block this thread waiting for the user's response! After the user sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LocationReporter.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)      	{
                    // permission was granted, yay! Do the contacts-related task you need to do.
                } else {
                    finish();
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
// other 'case' lines to check for other permissions this app might request
        }
    }
}
