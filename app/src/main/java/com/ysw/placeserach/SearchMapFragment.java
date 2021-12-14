package com.ysw.placeserach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class SearchMapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_map, container, false);
    }//onCreateView

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                MainActivity ma = (MainActivity) getActivity();

                LatLng me = new LatLng(ma.mylocation.getLatitude(), ma.mylocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 16));

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                for(Place place : ma.searchLocalApiResponse.documents){
                    double latitude= Double.parseDouble(place.y);
                    double longitude= Double.parseDouble(place.x);
                    LatLng position= new LatLng(latitude, longitude);

                    //마커옵션객체를 통해 마커의 설정들
                    MarkerOptions options= new MarkerOptions().position(position).title(place.place_name).snippet(place.distance+"m");
                    googleMap.addMarker(options).setTag(place.place_url);

                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(@NonNull Marker marker) {

                            Intent intent = new Intent(getActivity(), PlaceUrlActivity.class);
                            intent.putExtra("place_url",marker.getTag().toString());
                            startActivity(intent);

                        }
                    });


                }//setOnInfoWindowClickListener



            }//onMapReady
        });//getMapAsync

    }//onviewCreated
}//SearchMapFragment
