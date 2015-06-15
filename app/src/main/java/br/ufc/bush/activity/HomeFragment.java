package br.ufc.bush.activity;

/**
 * Created by great on 28/04/2015.
 */
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Random;

import br.ufc.bush.R;
import br.ufc.bush.listeners.LocationListener;
import br.ufc.bush.notifiers.LocationUpdater;


public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener{

    private MapFragment mMapFragment;
    private GoogleMap map;

    private Marker busMarker;

    private float mDeclination;
    private float[] mRotationMatrix = new float[16];

    private TextToSpeech tts;

    private double myLatitude = -3.748403;
    private double myLongitude = -38.576358;

    private boolean isBusNear = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instantiateMap();
    }

    private void instantiateMap() {
        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        SensorManager manager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        manager.registerListener(new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(
                            mRotationMatrix, event.values);
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(mRotationMatrix, orientation);
                    float bearing = (float) (Math.toDegrees(orientation[0]) + mDeclination);
                    updateCamera(bearing);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        tts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(new Locale("pt", "BR"));
                }
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        Thread.sleep(7000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    isBusNear = false;
//                }
//            }
//        }).start();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void updateCamera(float bearing) {
        if(busMarker != null) {
            busMarker.setRotation(bearing);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tts.shutdown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationUpdater.getInstance(getActivity()).unregisterUpdater(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setupMap();
    }

    private void setupMap() {
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.zoomTo(16));

        LatLng position = new LatLng(myLatitude, myLongitude);
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nav_dexter))
                .position(position).flat(true));

        LocationUpdater.getInstance(getActivity()).registerUpdater(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void speak(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hello speak");
    }

    @Override
    public void updateLocation(Location location) {
        GeomagneticField field = new GeomagneticField(
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                System.currentTimeMillis()
        );


        double distance = calculateDistance(myLatitude, myLongitude, location.getLatitude(), location.getLongitude());
        //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(distance), Toast.LENGTH_SHORT).show();
        if(distance <  50){
            if(!isBusNear){
                speak("O onibus se aproxima");
                isBusNear = true;
            }
        }
        else {
            isBusNear = false;
        }

        mDeclination = field.getDeclination();
        busMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

        new RegisterTask().execute("Device_test", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void onLocationServiceConnected() {
        Location location = LocationUpdater.getInstance(getActivity()).getLocation();
        if(location != null){
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            busMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nav_arrow)).flat(true).position(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    @Override
    public void onLocationServiceDisconnected() {

    }

    private double calculateDistance(double fromLong, double fromLat,
                                     double toLong, double toLat) {
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }

    // Classe para conexao Android_PHP
    private class RegisterTask extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            String url = "http://www.sefiro.com.br/bush/insere.php?" + "id_device=" + params[0]
                         + "&latitude=" + params[1]
                         + "&longitude=" + params[2]
                         + "&timestamp=" + params[3];

            HttpGet httpGet = new HttpGet();
            try {
                httpGet.setURI(new URI(url));
                HttpResponse response = client.execute(httpGet);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {

        }
    }
}
