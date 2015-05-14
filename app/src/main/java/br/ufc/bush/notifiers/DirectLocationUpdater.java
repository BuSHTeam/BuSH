package br.ufc.bush.notifiers;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by great on 05/05/2015.
 */
public class DirectLocationUpdater extends LocationUpdater implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static LocationUpdater instance;

    public static LocationUpdater getInstance() {
        if(instance == null){
            instance = new DirectLocationUpdater();
        }
        return instance;
    }

    @Override
    public void startLocationService() {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void stopLocationService() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public LatLng getLocation() {
        if(mGoogleApiClient != null){
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location == null){
                return null;
            }
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        else{
            Log.e("DirectLocationUpdater", "Google Play Services not initialized");
            return null;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        notifyLocationServiceConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        notifyLocationServiceDiscoonnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        notifyListeners(latLng);
    }
}