package com.example.wang.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.CameraUpdateFactory;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchFunctionType;
import com.amap.api.services.nearby.NearbySearchResult;
import com.amap.api.services.nearby.UploadInfo;
import com.amap.api.services.nearby.NearbySearch.NearbyQuery;
import com.amap.api.services.nearby.NearbySearch.NearbyListener;
import com.amap.api.services.nearby.NearbyInfo;



public class MainActivity extends AppCompatActivity implements AMapLocationListener,
        LocationSource
{
    //定义成员变量
    MapView mMapView = null;
    UiSettings mUiSettings = null;
    private AMap aMap = null;
    public Marker mMarker = null;
    public LatLonPoint mCenterPoint = new LatLonPoint(1,1);  //自己的位置
    public LatLng mMarkerPoint = new LatLng(1,1);  //覆盖物位置
    public int nCount = 0;   //覆盖物计数
    private OnLocationChangedListener mListener;
    public AMapLocationClient mAMapLocationClient = null;   //声明定位客户端类对象
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明附近派单对象
    public NearbySearch mNearbySearch = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);

        //初始化附近派单功能
        mNearbySearch = NearbySearch.getInstance(getApplicationContext());
        mNearbySearch.addNearbyListener(new NearbyListener() {
            @Override
            public void onUserInfoCleared(int i) {
                if(i == 1000){
                    mMarker.remove();
                    nCount = 0;
                }
            }

            @Override
            public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int i) {
                if(i == 1000){
                    if (nearbySearchResult != null
                            && nearbySearchResult.getNearbyInfoList() != null
                            && nearbySearchResult.getNearbyInfoList().size() > 0) {
                        NearbyInfo nearbyInfo = nearbySearchResult.getNearbyInfoList().get(0);
                        mNearbySearch.setUserID(nearbyInfo.getUserID());
                        mNearbySearch.clearUserInfoAsyn();
                    }
                }
            }

            @Override
            public void onNearbyInfoUploaded(int i) {
                if (i == 1000){
                    //添加标志
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.position(mMarkerPoint);
                    mMarker = aMap.addMarker(markerOption);
                    mMarker.setDraggable(true);
                    mMarker.setTitle("User1");
                    nCount = 1;
                }
            }
        });

        Button chidouButton = (Button)findViewById(R.id.chidoubutton);
        chidouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置搜索条件
                NearbyQuery query = new NearbyQuery();
                //设置搜索的中心点
                query.setCenterPoint(mCenterPoint);
                //设置搜索的坐标体系
                query.setCoordType(NearbySearch.AMAP);
                //设置搜索半径
                query.setRadius(50);
                //设置查询的时间
                query.setTimeRange(1800);
                //设置查询的方式驾车还是距离
                query.setType(NearbySearchFunctionType.DRIVING_DISTANCE_SEARCH);
                //调用异步查询接口
                mNearbySearch.searchNearbyInfoAsyn(query);
            }
        });


        //初始化地图
        InitMap();
    }

    private void InitMap()
    {
        if (aMap == null)
        {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
        aMap.setLocationSource(this);//设置定位按钮监听源
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);// 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //设置地图单击事件
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (nCount == 0){
                    UploadInfo loadInfo = new UploadInfo();
                    //设置上传位置的坐标系支持AMap坐标数据与GPS数据
                    loadInfo.setCoordType(NearbySearch.AMAP);
                    //设置上传数据位置,位置的获取推荐使用高德定位sdk进行获取
                    loadInfo.setPoint(new LatLonPoint(latLng.latitude, latLng.longitude));
                    //设置上传用户id
                    loadInfo.setUserID("User1");
                    //调用异步上传接口
                    mNearbySearch.uploadNearbyInfoAsyn(loadInfo);
                    mMarkerPoint = latLng;
                }
            }
        });
        CameraUpdate cu = CameraUpdateFactory.zoomTo(20);
        aMap.moveCamera(cu);

        mUiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮

        // 初始化定位客户端
        mAMapLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mAMapLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mAMapLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mAMapLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                mCenterPoint.setLatitude(aMapLocation.getLatitude());
                mCenterPoint.setLongitude(aMapLocation.getLongitude());
            }
        }

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        NearbySearch.destroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }
}


