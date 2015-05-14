package br.ufc.bush.activity;

/**
 * Created by great on 28/04/2015.
 */
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import br.ufc.bush.R;
import br.ufc.bush.listeners.LocationListener;
import br.ufc.bush.notifiers.LocationUpdater;


public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener{

    private MapFragment mMapFragment;
    private GoogleMap map;

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
        fragmentTransaction.add(R.id.map_container,  mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Inflate the layout for this fragment
        return rootView;
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
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.zoomTo(16));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("Marker", "clicked");
                return true;
            }
        });

        LocationUpdater.getInstance(getActivity()).registerUpdater(this);
    }

    @Override
    public void updateLocation(LatLng latLng) {
        //map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onLocationServiceConnected() {
        LatLng latLng = LocationUpdater.getInstance(getActivity()).getLocation();
        if(latLng != null){
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onLocationServiceDisconnected() {

    }
}
