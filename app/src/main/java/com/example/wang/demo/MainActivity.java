package com.example.wang.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.location.AMapLocationListener;

public class MainActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener
{
    //定义成员变量
    MapView mMapView = null;
    UiSettings mUiSettings = null;
    private AMap aMap = null;
    private OnLocationChangedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        //初始化地图
        InitMap();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
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

    private void InitMap()
    {
        if (aMap == null)
        {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
        aMap.setLocationSource(this);//设置定位监听
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        mUiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null)
            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }
}
