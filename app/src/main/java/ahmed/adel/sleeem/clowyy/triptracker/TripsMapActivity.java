package ahmed.adel.sleeem.clowyy.triptracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripExtraInfo;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripPoints;

public class TripsMapActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private static final String TAG = "TripsMapActivity";
    private GoogleMap googleMap;

    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    private List<Polyline> polylines = null;

    private List<TripPoints> tripPointsList;

    private ImageView myLocationImageView;

    private TripExtraInfo getTripExtraInfo(LatLng source, LatLng destination) {
        String response = "";
        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;
        String str_source = "origin=" + source.latitude + "," + source.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String parameters = str_source + "&" + str_dest + "&key=" + getString(R.string.google_maps_key);
        String strUrl = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                response = stringBuffer.toString();

                JSONObject jsonObject = new JSONObject(response);
                String distance = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                String duration = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

                bufferedReader.close();

                return new TripExtraInfo(distance, duration);
            }else{

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }

        return null;
    }


    private Bitmap getLocationPhoto(String location){
        // https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Kafr%20El-Shaikh,%20Qism%20Kafr%20El-Shaikh,%20Kafr%20Al%20Sheikh&inputtype=textquery&fields=photos&key=AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA
        String response = "";
        Bitmap imageResult = null;

        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;
        String API_KEY = getString(R.string.google_maps_key);

        // String location = "Kafr%20El-Shaikh,%20Qism%20Kafr%20El-Shaikh,%20Kafr%20Al%20Sheikh";
        String parameters = "input=" + location + "&inputtype=textquery&fields=photos&key=" + API_KEY;
        String strUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" + parameters;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                response = stringBuffer.toString();


                JSONObject jsonObject = new JSONObject(response);
                String photoReference = jsonObject.getJSONArray("candidates").getJSONObject(0).getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                bufferedReader.close();
                inputStream.close();
                urlConnection.disconnect();

                strUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + API_KEY;

                URL myImageURL = new URL(strUrl);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) myImageURL.openConnection();
                httpsURLConnection.connect();

                if(httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                    imageResult = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }else{
                    return null;
                }


//                return new TripExtraInfo(distance, duration);
            }else{

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }

        return imageResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_map);

        requestPermision();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View mapView = mapFragment.getView();

        myLocationImageView = findViewById(R.id.myLocationImage);

        tripPointsList = new ArrayList<>();
        TripPoints trip1 = new TripPoints(getLocationFromAddress("Kafr El-Shaikh, Qism Kafr El-Shaikh, Kafr Al Sheikh"), getLocationFromAddress("Alexandria"));
        TripPoints trip2 = new TripPoints(getLocationFromAddress("Alexandria"), getLocationFromAddress("Cairo"));
        TripPoints trip3 = new TripPoints(getLocationFromAddress("Cairo"), getLocationFromAddress("Marsa Matruh, Mersa Matruh"));
        TripPoints trip4 = new TripPoints(getLocationFromAddress("Kafr El-Shaikh, Qism Kafr El-Shaikh, Kafr Al Sheikh"), getLocationFromAddress("Military Area, Sidi Barrani"));

        tripPointsList.add(trip1);
        tripPointsList.add(trip2);
        tripPointsList.add(trip3);
        tripPointsList.add(trip4);

        findViewById(R.id.btnGoToMaps).setOnClickListener(v->{
            // https://maps.googleapis.com/maps/api/directions/json?origin=31.110659299999995,30.938779900000004&destination=31.2000924,29.9187387&key=AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA
            /*
            Uri gmmIntentUri = Uri.parse("google.navigation:q=Military Area, Sidi Barrani");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
            */

            // 7.5 - 542

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TripExtraInfo tripExtraInfo = getTripExtraInfo(tripPointsList.get(3).start, tripPointsList.get(3).end);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Distance: "+ tripExtraInfo.getDistance() + ", Duration: " + tripExtraInfo.getDuration(), Toast.LENGTH_LONG).show();
                            // 542    7,12
                        }
                    });
                }
            }).start();
            */



            new Thread((new Runnable() {
                @Override
                public void run() {
                    Bitmap locationImage = getLocationPhoto("Marsa Matruh, Mersa Matruh");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myLocationImageView.setImageBitmap(locationImage);
                        }
                    });
                }
            })).start();


            // distance = response.getJSONArray("routes").getJSONObject(routeIndex).getJSONArray("legs").getJSONObject(index).getJSONObject("distance").getString("text");
           // duration = response.getJSONArray("routes").getJSONObject(routeIndex).getJSONArray("legs").getJSONObject(index).getJSONObject("duration").getString("text");
        });


    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng point = null;

        try {

            address = coder.getFromLocationName(strAddress, 2);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            point = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return point;
    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationPermission = true;
                    startNavigate();
                }
            }break;
        }
    }

    private void startNavigate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
      //  LatLngBounds bounds = new LatLngBounds(tripPointsList.get(0).start, tripPointsList.get(0).end);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 10, 10, 10); //CameraUpdateFactory.newLatLngZoom(trip.start, 16f);
        //googleMap.animateCamera(cameraUpdate);

        for (TripPoints trip: tripPointsList) {

           // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(trip.start, 16f);
           // googleMap.animateCamera(cameraUpdate);

            //googleMap.clear();

            Findroutes(trip.start, trip.end);
        }

        LatLngBounds bounds = new LatLngBounds(
                tripPointsList.get(2).start,
                tripPointsList.get(2).end
                // new LatLng(tripPointsList.get(0).start.latitude,tripPointsList.get(0).start.longitude),
                // new LatLng(tripPointsList.get(0).end.latitude,tripPointsList.get(0).end.longitude)
                //tripPointsList.get(0).end
        );

        double distance = CalculationByDistance(tripPointsList.get(0).start,  tripPointsList.get(0).end);

      //  int zoom = getBoundsZoomLevel(tripPointsList.get(2).start,  tripPointsList.get(2).end, 1080, 1321);
       //  googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bounds.getCenter().latitude, 180 + bounds.getCenter().longitude), 7));


        LatLng source = tripPointsList.get(1).start;
        LatLng destination = tripPointsList.get(1).end;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 15));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                // Toast.makeText(TripsMapActivity.this, "Finish", Toast.LENGTH_SHORT).show();
               // CameraUpdateFactory.newLatLngZoom(mountainView, 15);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 10), 2000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(destination)      // Sets the center of the map to Mountain View
                                .zoom(17)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        googleMap.animateCamera(CameraUpdateFactory.zoomIn(), 2000, null);
                    }

                    @Override
                    public void onCancel() { }
                });
            }

            @Override
            public void onCancel() { }
        });





        // Move the camera instantly to Sydney with a zoom of 15.
       // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mountainView, 15));

// Zoom in, animating the camera.
        // googleMap.animateCamera(CameraUpdateFactory.zoomIn(), 2000, null);

// Zoom out to zoom level 10, animating with a duration of 2 seconds.
      //  googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


        //  googleMap.setLatLngBoundsForCameraTarget(bounds);

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    final static int GLOBE_WIDTH = 256; // a constant in Google's map projection
    final static int ZOOM_MAX = 21;

    public static int getBoundsZoomLevel(LatLng northeast,LatLng southwest,
                                         int width, int height) {
        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX);
        return (int)(zoom);
    }
    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }
    private static double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(mapPx / worldPx / fraction) / LN2);
    }

    LatLngBounds boundsFromLatLngList(List<LatLng> list) {
        //assert(list.isNotEmpty);
        double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        for (LatLng latLng : list) {
            if (x0 == 0) {
                x0 = x1 = latLng.latitude;
                y0 = y1 = latLng.longitude;
            } else {
                if (latLng.latitude > x1) x1 = latLng.latitude;
                if (latLng.latitude < x0) x0 = latLng.latitude;
                if (latLng.longitude > y1) y1 = latLng.longitude;
                if (latLng.longitude < y0) y0 = latLng.longitude;
            }
        }
        return new LatLngBounds(new LatLng(x1, y1), new LatLng(x0, y0));
    }

    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(TripsMapActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getString(R.string.google_maps_key))
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if(locationPermission) {
            startNavigate();
        }
    }

    @Override
    public void onRoutingStart() {
        //Toast.makeText(TripsMapActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
        if(polylines!=null) {
            polylines.clear();
        }

        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        for (int i = 0; i <routes.size(); i++) {

            if(i==shortestRouteIndex)
            {
                Random random = new Random();
                int color = Color.rgb(random.nextInt(256), random.nextInt(128), random.nextInt(256));
                polyOptions.color(color);
                polyOptions.width(7);
                polyOptions.addAll(routes.get(shortestRouteIndex).getPoints());
                Polyline polyline = googleMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else { }
        }

        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        startMarker.title("My Location");
        googleMap.addMarker(startMarker);

        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        googleMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingCancelled() {

    }

}