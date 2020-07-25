package com.maps.why;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng sydney = new LatLng(12.54936, 77.5432);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Pothole"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        try {
            String json_file = json_from_url(mMap, new URL("https://api.thingspeak.com/channels/698434/feeds.json?api_key=HVRBGS3FPOV77FXR&results=100"));
            json_parser_gson(mMap, json_file);
        }catch (Exception e) {
            e.printStackTrace();
            LatLng pothole = new LatLng(13, 15);
//            if((severity==1) || (severity == 49)){
            mMap.addMarker(new MarkerOptions().position(pothole).title("x pothole " + e.toString()));
        }
    }

    public static String json_from_url(GoogleMap mMap, URL url) throws Exception {

        try{
            URLConnection conn = url.openConnection();
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine = br.readLine();
            br.close();
            return inputLine;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            LatLng pothole = new LatLng(12.5493, 77.5431);
//            if((severity==1) || (severity == 49)){
            mMap.addMarker(new MarkerOptions().position(pothole).title("a pothole " + e.toString()));
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            LatLng pothole = new LatLng(12.5495, 77.5445);
//            if((severity==1) || (severity == 49)){
            mMap.addMarker(new MarkerOptions().position(pothole).title("b pothole " + e.toString()));
            return "";
        }
    }

    public static void json_parser_gson(GoogleMap mMap, String json) {
        JsonParser parser = new JsonParser();
        try {
            JsonElement jsonTree = parser.parse(json);
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            JsonArray feeds = jsonObject.getAsJsonArray("feeds");

            for (JsonElement feed : feeds) {
                JsonObject feedsObj = feed.getAsJsonObject();
                JsonElement entry_id = feedsObj.get("entry_id");
                JsonElement field1_latitude = feedsObj.get("field1");
                JsonElement field2_longitude = feedsObj.get("field2");
                JsonElement field3_severity = feedsObj.get("field3");
                double latitude = field1_latitude.getAsDouble();
                double longitude = field2_longitude.getAsDouble();
                int severity = 0;
                if (!field3_severity.isJsonNull())
                    severity = field3_severity.getAsInt();

                LatLng pothole = new LatLng(latitude, longitude);
                //            if((severity==1) || (severity == 49)){
                mMap.addMarker(new MarkerOptions().position(pothole).title("Small pothole " + entry_id));
                //                mMap.moveCamera(CameraUpdateFactory.newLatLng(pothole));
                if ((severity == 2) || (severity == 50)) {
                    mMap.addMarker(new MarkerOptions().position(pothole).title("Medium pothole " + entry_id));
                    //                mMap.moveCamera(CameraUpdateFactory.newLatLng(pothole));
                } else if ((severity == 3) || (severity == 51)) {
                    mMap.addMarker(new MarkerOptions().position(pothole).title("Large pothole " + entry_id));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pothole));
                }
                pothole = new LatLng(12, 15);
                //            if((severity==1) || (severity == 49)){
                mMap.addMarker(new MarkerOptions().position(pothole).title("Small pothole " + entry_id));
            }
        }
        catch (Exception e){
            LatLng pothole = new LatLng(12.5, 15.7);
            mMap.addMarker(new MarkerOptions().position(pothole).title(e.toString()));

        }
    }
}
