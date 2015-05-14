package br.ufc.bush.listeners;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by great on 05/05/2015.
 */
public interface LocationListener {

    public void updateLocation(LatLng latLng);

    public void onLocationServiceConnected();

    public void onLocationServiceDisconnected();
}
