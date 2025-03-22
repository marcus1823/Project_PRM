package com.example.menaccessoriesshop.ui.view;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.BannerAdapter;
import com.example.menaccessoriesshop.adapter.ProductAdapter;
import com.example.menaccessoriesshop.data.model.Product;
import com.example.menaccessoriesshop.data.model.Store;
import com.example.menaccessoriesshop.data.repository.ProductRepository;
import com.example.menaccessoriesshop.data.repository.ProductService;
import com.example.menaccessoriesshop.data.repository.StoreRepository;
import com.example.menaccessoriesshop.data.repository.StoreService;
import com.example.menaccessoriesshop.ui.viewmodel.HomeViewModel;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.engine.AuthenticationMode;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.routing.RoutingEngine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private ViewPager2 bannerViewPager;
    private BannerAdapter bannerAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private ProductService productService;
    private StoreService storeService;
    private RoutingEngine routingEngine;
    private MapView mapView;
    private GeoCoordinates storeLocation;
    private HomeViewModel mViewModel;
    private TextView infoAddress;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initializeHERESDK();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        AnhXa(view);
        mapView.onCreate(savedInstanceState);
        getStore();

        setupBanner();


        fetchTop6Products();

//        productAdapter = new ProductAdapter(productList);
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerProducts.setAdapter(productAdapter);
        return view;
    }

    private void AnhXa(View view) {
        recyclerProducts = view.findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
       // productAdapter = new ProductAdapter(productList);
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerProducts.setAdapter(productAdapter);

        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        productService = ProductRepository.getProductService();
        storeService = StoreRepository.getStoreService();
        mapView = view.findViewById(R.id.map_view);
        infoAddress = view.findViewById(R.id.infoAddress);
    }

    private void initializeHERESDK() {
        String accessKeyID = "UV1meQcKkcbClcXyMS6TFQ";
        String accessKeySecret = "-LMWw1usmP2eeEwIonAtqpKo1Pl5doEe9e_iuxmcxRJ9FAJSQoO8y-4qZmIyYWMRZO_NyZaIoAbSqkzlDQAb5A";
        AuthenticationMode authenticationMode = AuthenticationMode.withKeySecret(accessKeyID, accessKeySecret);
        SDKOptions options = new SDKOptions(authenticationMode);
        try {
            Context context = requireContext();
            SDKNativeEngine.makeSharedInstance(context, options);
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }


    private void fetchTop6Products() {
        productService.getTopSellingProducts("salesQuantity", "desc").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    List<Product> allProducts = response.body();
                    productList.addAll(allProducts.subList(0, Math.min(allProducts.size(), 6)));
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e("PRODUCT", "Lỗi lấy danh sách sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("PRODUCT", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    private void setupBanner() {
        List<Integer> imageList = Arrays.asList(
                R.drawable.ic_banner,
                R.drawable.banner2,
                R.drawable.banner3
        );
        bannerAdapter = new BannerAdapter(imageList);
        bannerViewPager.setAdapter(bannerAdapter);

        startBannerAutoScroll();
    }

    private void startBannerAutoScroll() {
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = bannerViewPager.getCurrentItem() + 1;
                if (nextItem >= bannerAdapter.getItemCount()) {
                    nextItem = 0;
                }
                bannerViewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
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
                    infoAddress.setText(store.getAddress() + "  |  " + store.getPhone());
                } else
                    Log.e("STORE", "Lỗi lấy thông tin cửa hàng");
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Log.e("STORE", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }


    private void loadMapScene(double latitude, double longitude ) {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    double distanceInMeters = 1000;
                    MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, distanceInMeters);
                    mapView.getCamera().lookAt(
                            new GeoCoordinates(latitude, longitude), mapMeasureZoom);
                } else {
                    Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
                }
            }
        });
    }
}

