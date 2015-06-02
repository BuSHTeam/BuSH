package br.ufc.bush.notifiers;

import android.content.Context;
import android.location.Location;

import java.util.ArrayList;

import br.ufc.bush.listeners.LocationListener;

/**
 * Created by great on 05/05/2015.
 */
public abstract class LocationUpdater {

    public static Context context;
    private ArrayList<LocationListener> listeners;

    private static LocationUpdater instance;

    private static final boolean USE_LOCCAM = false;

    public LocationUpdater(){
        listeners = new ArrayList<>();
    }

    public static LocationUpdater getInstance(Context context){
        LocationUpdater.context = context;

        if(USE_LOCCAM){
            return LoccamLocationUpdater.getInstance();
        }
        else{
            return DirectLocationUpdater.getInstance();
        }
    }

    public void registerUpdater(LocationListener listener){
        listeners.add(listener);

        if(listeners.size() == 1){
            startLocationService();
        }
    }

    public void unregisterUpdater(LocationListener listener){
        if(listeners.contains(listener)){
            listeners.remove(listener);

            if(listeners.size() == 0){
                stopLocationService();
            }
        }
    }

    public void notifyListeners(Location location){
        for(LocationListener listener : listeners){
            listener.updateLocation(location);
        }
    }

    public void notifyLocationServiceConnected(){
        for(LocationListener listener : listeners){
            listener.onLocationServiceConnected();
        }
    }

    public void notifyLocationServiceDiscoonnected(){
        for(LocationListener listener : listeners){
            listener.onLocationServiceDisconnected();
        }
    }

    public abstract void startLocationService();

    public abstract void stopLocationService();

    public abstract Location getLocation();
}
