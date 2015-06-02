package br.ufc.bush.notifiers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by great on 05/05/2015.
 */
public class LoccamLocationUpdater extends LocationUpdater {

    private static LocationUpdater instance;

    public static LocationUpdater getInstance() {
        if(instance == null){
            instance = new DirectLocationUpdater();
        }
        return instance;
    }

    @Override
    public void startLocationService() {

    }

    @Override
    public void stopLocationService() {

    }

    @Override
    public Location getLocation() {
        return null;
    }
}
