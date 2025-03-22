package com.example.menaccessoriesshop.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.Store;
import com.example.menaccessoriesshop.data.repository.StoreService;
import com.google.android.material.navigation.NavigationView;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.engine.AuthenticationMode;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageStoreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private double latitude,longitude;
    private EditText edtStoreName, edtStoreAddress, edtStorePhone,edtLatitude,edtLongitude;
    private ImageView imgStore;
    private Button btnUpdateStore;
    private StoreService storeService;
    private String storeId = "1"; // ID cửa hàng
    private MapView mapView;
    private GeoCoordinates storeLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeHERESDK();
        setContentView(R.layout.activity_manage_store);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://67dd31cce00db03c406a9077.mockapi.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        storeService = retrofit.create(StoreService.class);
        getStore();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý cửa hàng");

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        edtStoreName = findViewById(R.id.edtStoreName);
        edtStoreAddress = findViewById(R.id.edtStoreAddress);
        edtStorePhone = findViewById(R.id.edtStorePhone);
        imgStore = findViewById(R.id.imgStore);
        edtLatitude = findViewById(R.id.edtLatitude);
        edtLongitude = findViewById(R.id.edtLongitude);
        btnUpdateStore = findViewById(R.id.btnUpdateStore);

        // Lấy thông tin cửa hàng
        loadStoreInfo();

        // Cập nhật cửa hàng
        btnUpdateStore.setOnClickListener(v -> updateStoreInfo());
    }

    private void initializeHERESDK() {
        String accessKeyID = "UV1meQcKkcbClcXyMS6TFQ";
        String accessKeySecret = "-LMWw1usmP2eeEwIonAtqpKo1Pl5doEe9e_iuxmcxRJ9FAJSQoO8y-4qZmIyYWMRZO_NyZaIoAbSqkzlDQAb5A";
        AuthenticationMode authenticationMode = AuthenticationMode.withKeySecret(accessKeyID, accessKeySecret);
        SDKOptions options = new SDKOptions(authenticationMode);
        try {
            SDKNativeEngine.makeSharedInstance(getApplicationContext(), options);
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_manage_product) {
            startActivity(new Intent(this, ManageProductActivity.class));
        } else if (id == R.id.nav_manage_store) {
            startActivity(new Intent(this, ManageStoreActivity.class));
        } else if (id == R.id.nav_manage_order) {
            startActivity(new Intent(this, ManageOrderActivity.class));
        } else if (id == R.id.nav_manage_payment) {
            startActivity(new Intent(this, ManagePaymentActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open when back is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void getStore(){

        storeService.getStoreById("1").enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if(response.isSuccessful() && response.body() != null){
                    Store store = response.body();
                    storeLocation = new GeoCoordinates(store.getLatitude(), store.getLongitude());
                    MapImage mapImage = MapImageFactory.fromResource(getResources(), R.drawable.location);
                    MapMarker mapMarker = new MapMarker(storeLocation, mapImage);
                    mapView.getMapScene().addMapMarker(mapMarker);
                    loadMapScene(store.getLatitude(), store.getLongitude());
                } else
                    Log.e("STORE", "Lỗi lấy thông tin cửa hàng");
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Log.e("STORE", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }
    private void loadStoreInfo() {
        storeService.getStoreById(storeId).enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Store store = response.body();
                    edtStoreName.setText(store.getName());
                    edtStoreAddress.setText(store.getAddress());
                    edtStorePhone.setText(store.getPhone());

                    // Lấy latitude & longitude từ API
                    double latitude = store.getLatitude();
                    double longitude = store.getLongitude();

                    // Hiển thị lên EditText
                    edtLatitude.setText(String.valueOf(latitude));
                    edtLongitude.setText(String.valueOf(longitude));

                    // Hiển thị vị trí trên bản đồ
                    storeLocation = new GeoCoordinates(latitude, longitude);
                    loadMapScene(latitude, longitude);
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Toast.makeText(ManageStoreActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadMapScene(double latitude, double longitude) {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                double distanceInMeters = 1000;
                MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, distanceInMeters);
                mapView.getCamera().lookAt(new GeoCoordinates(latitude, longitude), mapMeasureZoom);

                // Thêm marker vào vị trí cửa hàng
                MapImage mapImage = MapImageFactory.fromResource(getResources(), R.drawable.location);
                MapMarker mapMarker = new MapMarker(new GeoCoordinates(latitude, longitude), mapImage);
                mapView.getMapScene().addMapMarker(mapMarker);
            } else {
                Toast.makeText(this, "Lỗi tải bản đồ: " + mapError.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStoreInfo() {
        String name = edtStoreName.getText().toString().trim();
        String address = edtStoreAddress.getText().toString().trim();
        String phone = edtStorePhone.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        double latitude = Double.parseDouble(edtLatitude.getText().toString());
        double longitude = Double.parseDouble(edtLongitude.getText().toString());

        Store updatedStore = new Store(storeId, name, address, phone, latitude, longitude);

        storeService.updateStore(storeId, updatedStore).enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageStoreActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    loadStoreInfo(); // Refresh UI
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Toast.makeText(ManageStoreActivity.this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }
}