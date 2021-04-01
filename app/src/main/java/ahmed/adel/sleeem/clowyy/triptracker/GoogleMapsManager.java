package ahmed.adel.sleeem.clowyy.triptracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripCell;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripExtraInfo;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripPoints;

public class GoogleMapsManager implements OnMapReadyCallback, RoutingListener {

    private static GoogleMapsManager googleMapsManager = null;
    private static Context context;
    private static String API_KEY;
    public final static int LOCATION_REQUEST_CODE = 23;
    public boolean locationPermission = false;

    private List<Polyline> polyLines = null;

    public List<TripCell> tripPointsList;
    private GoogleMap googleMap;

    private int cameraAnimationAtIndex = 0;


    private GoogleMapsManager() {

    }

    public static GoogleMapsManager getInstance(Context mContext) {
        context = mContext;

        if (googleMapsManager == null) {
            API_KEY = context.getString(R.string.google_maps_key);
            googleMapsManager = new GoogleMapsManager();
        }
        return googleMapsManager;
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    public void findRoutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(context, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(API_KEY)
                    .build();
            routing.execute();
        }
    }

    public TripPoints getTripPoints(String source, String destination){
        return new TripPoints(getLocationFromAddress(source), getLocationFromAddress(destination));
    }

    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(context);
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

    public TripExtraInfo getTripExtraInfo(String sourceStr, String destinationStr) {

        LatLng source = getLocationFromAddress(sourceStr);
        LatLng destination = getLocationFromAddress(destinationStr);

        String response = "";
        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;
        String str_source = "origin=" + source.latitude + "," + source.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String parameters = str_source + "&" + str_dest + "&key=" + API_KEY;
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
            } else {

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

    public String getLocationImageURL(String location) {
        // https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Kafr%20El-Shaikh,%20Qism%20Kafr%20El-Shaikh,%20Kafr%20Al%20Sheikh&inputtype=textquery&fields=photos&key=AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA
        String response = "";
        //Bitmap imageResult = null;

        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;

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

                /*
                URL myImageURL = new URL(strUrl);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) myImageURL.openConnection();
                httpsURLConnection.connect();

                if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpsURLConnection.getInputStream();
                    imageResult = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } else {
                    return null;
                }
                */

                return strUrl;
            } else {
                Toast.makeText(context, "Unable to get photo", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            strUrl = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            strUrl = null;
        } catch (IOException e) {
            e.printStackTrace();
            strUrl = null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }

        return strUrl;
    }

    public Bitmap getLocationPhoto(String location) {
        // https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Kafr%20El-Shaikh,%20Qism%20Kafr%20El-Shaikh,%20Kafr%20Al%20Sheikh&inputtype=textquery&fields=photos&key=AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA
        String response = "";
        Bitmap imageResult = null;

        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;

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

                if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpsURLConnection.getInputStream();
                    imageResult = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } else {
                    return null;
                }
            } else {
                Toast.makeText(context, "Unable to get photo", Toast.LENGTH_LONG).show();
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

    public void launchGoogleMaps(String destination){
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public void startNavigate() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.clear();

        for (TripCell trip : tripPointsList) {
            if (trip.isSelected) {
                findRoutes(trip.tripPoints.start, trip.tripPoints.end);
            }
        }

        animateCamera(0);
    }

    void animateCameraToCenter() {
        LatLngBounds.Builder latLngBounds = LatLngBounds.builder();
        boolean includePoints = false;
        for (TripCell trip : tripPointsList) {
            if (trip.isSelected) {
                latLngBounds.include(trip.tripPoints.start);
                latLngBounds.include(trip.tripPoints.end);
                includePoints = true;
            }
        }

        if (includePoints)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.build().getCenter(), 6), 2000, null);
        else
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.0595581, 31.2234449), 8), 2000, null);
    }

    private void animateCamera(int index) {
        cameraAnimationAtIndex = index;
        if (index < tripPointsList.size() && !tripPointsList.get(index).isSelected) {
            animateCamera(index + 1);
        } else if (index < tripPointsList.size()) {
            LatLng source = tripPointsList.get(index).tripPoints.start;
            LatLng destination = tripPointsList.get(index).tripPoints.end;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 10), 2000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            googleMap.animateCamera(CameraUpdateFactory.zoomIn(), 2000, new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    if (index < tripPointsList.size() - 1) {
                                        animateCamera(index + 1);
                                    } else {
                                        animateCameraToCenter();
                                    }
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                }

                @Override
                public void onCancel() {
                }
            });
        } else {
            cameraAnimationAtIndex = 0;
            animateCameraToCenter();
        }
    }

    public void pauseCameraAnimation() {
        googleMap.stopAnimation();
    }

    public void resumeCameraAnimation() {
        animateCamera(cameraAnimationAtIndex);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (locationPermission) {
            startNavigate();
        }
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
        if (polyLines != null) {
            polyLines.clear();
        }

        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;

        polyLines = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {

            if (i == shortestRouteIndex) {
                Random random = new Random();
                int color = Color.rgb(random.nextInt(256), random.nextInt(128), random.nextInt(256));
                polyOptions.color(color);
                polyOptions.width(10);
                polyOptions.addAll(routes.get(shortestRouteIndex).getPoints());
                Polyline polyline = googleMap.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                polylineEndLatLng = polyline.getPoints().get(polyline.getPoints().size() - 1);
                polyLines.add(polyline);
            } else {
            }
        }

        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        startMarker.title("Source");
        googleMap.addMarker(startMarker);

        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        googleMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingFailure(RouteException e) {
    }

    @Override
    public void onRoutingCancelled() {

    }
}
