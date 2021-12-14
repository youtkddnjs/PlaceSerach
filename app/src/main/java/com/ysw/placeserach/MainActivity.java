package com.ysw.placeserach;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    int choiceID = R.id.choice_wc;

    //검색어
    String searchQuery = "화장실";
    //위치
    Location mylocation;
    //Fused Location 객체
    FusedLocationProviderClient locationProviderClient;

    //검색 관련 객체
    public SearchLocalApiResponse searchLocalApiResponse;

    TabLayout tabLayout;

    EditText etSearch;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar 제목줄 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //listFragment 붙이기
        getSupportFragmentManager().beginTransaction().add(R.id.container, new SearchListFragment()).commit();

        //탭 레이아웃 클릭
        tabLayout = findViewById(R.id.layout_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("LIST")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new SearchListFragment()).commit();
                } else if (tab.getText().equals("MAP")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new SearchMapFragment()).commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        etSearch = findViewById(R.id.et_search);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                searchQuery = etSearch.getText().toString();
                searchPlace();

                return false;
            }
        });

        //우선 내 위치 사용에 대한 허용 동적퍼미션(다이얼로그로 허락을 구하는 퍼미션)
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        int checkResult = checkSelfPermission(permissions[0]);
        if (checkResult == PackageManager.PERMISSION_DENIED) requestPermissions(permissions, 10);
        else requestMyLocation();


    }//onCreate

    //requestpermissions()의 다이얼로그를 선택하면 자동으로 실행되는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestMyLocation();
        } else {
            Toast.makeText(this, "GPS 사용 불가", Toast.LENGTH_SHORT).show();
        }
    }

    //내 위치 얻어내는 기능 코드 메소드
    void requestMyLocation() {

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //실시간 위치 검색 조건 설정
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //위치정보를 받았을때 반응하는 객체
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            //위치결과객체로 부터 내위치 정보 얻기
            mylocation = locationResult.getLastLocation();
//            Toast.makeText(MainActivity.this, ""+mylocation.getLatitude()+" "+mylocation.getLongitude(), Toast.LENGTH_SHORT).show();
            locationProviderClient.removeLocationUpdates(locationCallback);
            
            //카카오 키워드 로컬 검색 API 호출 메소드 시작
            searchPlace();

        }
    };

    //카카오 키워드 로컬 검색 API 호출 메소드
    void searchPlace(){

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("https://dapi.kakao.com");
        builder.addConverterFactory(ScalarsConverterFactory.create());
        builder.addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<SearchLocalApiResponse> call = retrofitService.searchPlace(searchQuery,mylocation.getLongitude()+"",mylocation.getLatitude()+"");
        call.enqueue(new Callback<SearchLocalApiResponse>() {
            @Override
            public void onResponse(Call<SearchLocalApiResponse> call, Response<SearchLocalApiResponse> response) {
                searchLocalApiResponse = response.body();

                getSupportFragmentManager().beginTransaction().replace(R.id.container,new SearchListFragment()).commit();
                tabLayout.getTabAt(0).select();

//                PlaceMeta meta = searchLocalApiResponse.meta;
//                List<Place> documents = searchLocalApiResponse.documents;
//
//                new AlertDialog.Builder(MainActivity.this).setMessage(meta.total_count+"\n"+documents.get(0).place_name+"\n"+documents.get(0).distance).show();
            }

            @Override
            public void onFailure(Call<SearchLocalApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
            }
        });

//        Call<String> call = retrofitService.searchPlaceByString(searchQuery,mylocation.getLongitude()+"",mylocation.getLatitude()+"");
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                String s = response.body();
//                new AlertDialog.Builder(MainActivity.this).setMessage(s).show();
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    //옵션메뉴를 만들어 주는 기능 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void clickChoice(View view){
        findViewById(choiceID).setBackgroundResource(R.drawable.bg_choice);
        view.setBackgroundResource(R.drawable.bg_choice_select);
        choiceID= view.getId();

        switch (choiceID){
            case R.id.choice_wc: searchQuery="화장실"; break;
            case R.id.choice_movie: searchQuery="영화관"; break;
            case R.id.choice_gas: searchQuery="주유소"; break;
            case R.id.choice_ev: searchQuery="전기충전소"; break;
            case R.id.choice_01: searchQuery="약국"; break;
            case R.id.choice_02: searchQuery="맛집"; break;
            case R.id.choice_03: searchQuery="모텔"; break;
            case R.id.choice_04: searchQuery="카페"; break;
            case R.id.choice_05: searchQuery="주차장"; break;
            case R.id.choice_06: searchQuery="편의점"; break;
            case R.id.choice_07: searchQuery="지하철"; break;
        }
        etSearch.setText("");
        //검색작업 요청
        searchPlace();
    }

}//main