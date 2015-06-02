package br.ufc.bush.listeners;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by great on 05/05/2015.
 */
public interface LocationListener {

    public void updateLocation(Location location);

    public void onLocationServiceConnected();

    public void onLocationServiceDisconnected();
}
