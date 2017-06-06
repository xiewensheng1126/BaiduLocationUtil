package com.euet.guider.ui.mine.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.euet.guider.R;
import com.euet.guider.utils.LocationUtils;

import java.lang.ref.WeakReference;


public class DemoActivity extends AppCompatActivity {

    private LocationUtils locationUtils;
    private TextView tv_city;
    private TextView tv_gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_location);

        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_gps = (TextView) findViewById(R.id.tv_gps);

        locationUtils = new LocationUtils(this, LocationUtils.DIALOG_GTYPE_SETTING);
        //设置自己需要定位的参数
        locationUtils.getLocationClientOption().setScanSpan(1000);
        //设置权限
        locationUtils.setPermition();
        //开始定位
        locationUtils.startLocation();

        //监听定位返回的数据
        locationUtils.getLocationListener(new LocationUtils.LocationListener() {
            @Override
            public void locationDateBack(final BDLocation location) {
                Log.e("百度定位返回数据", location.getCity());
                tv_city.setText(location.getCity());
                tv_gps.setText("所在经度：" + location.getLatitude() + " 纬度：" + location.getLongitude());
            }
        });

    }

    /**
     * 地图定位权限返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationUtils.onLocationRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
