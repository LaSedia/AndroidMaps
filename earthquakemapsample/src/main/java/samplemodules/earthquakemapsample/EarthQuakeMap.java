package samplemodules.earthquakemapsample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthQuakeMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final double CAMERA_LNG = 87.0;
    private static final double CAMERA_LAT = 17.0;

    // The Map Object
    // URL for getting the earthquake
    // replace with your own user name

    private final static String UNAME = "yourname";
    private final static String URL = "http://api.geonames.org/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
            + UNAME;

    public static final String TAG = "EarthquakeMapActivity";

    // Set up UI and get earthquake data



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_quake_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
       new HttpGetTask().execute(URL);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
  //      mapFragment.getMapAsync(this);
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private class HttpGetTask extends
            AsyncTask<String, Void, List<EarthQuakeRec>> {


        @Override
        protected List<EarthQuakeRec> doInBackground(String... params) {

            String request = new String(params[0]);

            HttpURLConnection httpUrlConnection = null;
            String UrlContents="";
            try {

                // Get Earthquake data in JSON format
                // Parse data into a list of EarthQuakeRecs
                httpUrlConnection = (HttpURLConnection) new URL(request).openConnection();

                    InputStream in = new BufferedInputStream(
                         httpUrlConnection.getInputStream());
                    UrlContents= readStream(in);



            } catch (MalformedURLException exception) {
                Log.i(TAG, "ClientProtocolException");
            }
            catch ( IOException exeption)
            {
                Log.i(TAG, "ClientProtocolException");
            }

            finally {
                if (null != httpUrlConnection)
                httpUrlConnection.disconnect();
            }
            return ProcessData(UrlContents);
        }
        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException");
                return "";
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }
        public  List<EarthQuakeRec> ProcessData(String StrData)
        {
            List<EarthQuakeRec> result = new ArrayList<EarthQuakeRec>();
            //      String JSONResponse = new BasicResponseHandler()
            //            .handleResponse(response);
            try {
                JSONObject object = (JSONObject) new JSONTokener(StrData)
                        .nextValue();
                JSONArray earthquakes = object.getJSONArray("earthquakes");
                for (int i = 0; i < earthquakes.length(); i++) {
                    JSONObject tmp = (JSONObject) earthquakes.get(i);
                    result.add(new EarthQuakeRec(
                            tmp.getDouble("lat"),
                            tmp.getDouble("lng"),
                            tmp.getDouble("magnitude")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<EarthQuakeRec> result) {

            // Get Map Object
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(EarthQuakeMap.this);

            if (null != mMap) {

                // Add a marker for every earthquake

                for (EarthQuakeRec rec : result) {

                    // Add a new marker for this earthquake
                    mMap.addMarker(new MarkerOptions()

                            // Set the Marker's position
                            .position(new LatLng(rec.getLat(), rec.getLng()))

                            // Set the title of the Marker's information window
                            .title(String.valueOf(rec.getMagnitude()))

                            // Set the color for the Marker
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(getMarkerColor(rec
                                            .getMagnitude()))));

                }

                // Center the map
                // Should compute map center from the actual data

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
                        CAMERA_LAT, CAMERA_LNG)));

            }

//            if (null != mClient)
   //             mClient.close();

        }

        // Assign marker color
        private float getMarkerColor(double magnitude) {

            if (magnitude < 6.0) {
                magnitude = 6.0;
            } else if (magnitude > 9.0) {
                magnitude = 9.0;
            }

            return (float) (120 * (magnitude - 6));
        }

    }

}
