package com.euet.tourist.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.euet.library.util.ToastUtils;
import com.euet.tourist.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiewensheng on 2017/3/15.
 * 百度定位的工具类
 */
public class BaiduLocationUtils {

    private LocationListener locationListener;
    private WeakReference<Activity> activityWeak;
    private int onceTime;

    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;

    public static int DIALOG_GTYPE_SETTING = 1;//没有权限时去设置权限界面
    public static int DIALOG_GTYPE_NOMAL = 2;//没有权限时只是弹框告诉用户要设置

    private int showDialogType;
    private CommonDialogUtils commonDialogUtils;

    /**
     * @param onceTime 多长时间定位一次
     */
    public BaiduLocationUtils(WeakReference<Activity> activityWeak, int onceTime, int type) {
        this.activityWeak = activityWeak;
        this.onceTime = onceTime;
        this.showDialogType = type;
    }

    private LocationClient mLocationClient;

    /**
     * 初始化定位
     *
     * @param onceTime
     */
    public void initeLocation(int onceTime) {

        if (activityWeak == null || activityWeak.get() == null) {
            return;
        }
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(activityWeak.get());
        }
        if (mLocationClient.isStarted()) {
            return;
        }
        MyLocationListener mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(onceTime);// 每个多少秒进行一次请求
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");
        option.setAddrType("all");
        mLocationClient.setLocOption(option);
        startLocation();
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        if (mLocationClient != null) {
            if (!mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        }
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    /**
     * 经纬度返回监听
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // 更新位置
            double mLatitude = location.getLatitude();
            double mLongtitude = location.getLongitude();
            if (locationListener != null) {
                locationListener.locationDateBack(location);
            }
        }
    }


    /**
     * 设置权限
     */
    public void setPermition() {
        if (activityWeak == null || activityWeak.get() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activityWeak.get(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activityWeak.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activityWeak.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(activityWeak.get(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (showDialogType == DIALOG_GTYPE_SETTING) {
                        settingDialog();
                    } else if (showDialogType == DIALOG_GTYPE_NOMAL) {
                        ToastUtils.toastLong(activityWeak.get(), activityWeak.get().getResources().getString(R.string.permission_no_map));
                    }
                } else {
                    activityWeak.get().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                }
                return;
            } else {
                initeLocation(onceTime);
            }
        } else {
            initeLocation(onceTime);
        }
    }


    public interface LocationListener {
        void locationDateBack(BDLocation location);
    }

    public void getLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    /**
     * 权限请求返回
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onLocationRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (activityWeak == null || activityWeak.get() == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    initeLocation(onceTime);
                } else {
                    if (showDialogType == DIALOG_GTYPE_SETTING) {
                        settingDialog();
                    } else if (showDialogType == DIALOG_GTYPE_NOMAL) {
                        ToastUtils.toastLong(activityWeak.get(), activityWeak.get().getResources().getString(R.string.permission_no_map));
                    }
                }
                break;
        }
    }


    /**
     *
     */
    public void settingDialog() {
        if (activityWeak == null || activityWeak.get() == null) {
            return;
        }

        if (commonDialogUtils == null) {
            commonDialogUtils = new CommonDialogUtils();
        }

        commonDialogUtils.showNomalDialog(activityWeak.get(), "无法获取你的位置信息。请在手机系统设置，权限管理中打开位置权限，允许领路人游客版版使用定位服务。", "取消", "设置", new CommonDialogUtils.OnDialogItemClickListener() {
            @Override
            public void onItemClickPositon(int positon) {
                switch (positon) {
                    case 0:
                        break;
                    case 1:
                        getAppDetailSettingIntent(activityWeak.get());
                        break;
                }
            }
        });

    }


    /**
     * 跳入设置权限的界面
     *
     * @param context
     */
    public void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activityWeak.get().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }


}
