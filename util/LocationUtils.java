package com.euet.guider.utils;

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
import com.euet.guider.R;
import com.euet.guider.view.CommonDialogUtils;
import com.euet.library.util.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiewensheng on 2017/3/15.
 */
public class LocationUtils {

    private LocationListener locationListener;
    private Activity activity;

    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;

    public static int DIALOG_GTYPE_SETTING = 1;//去设置权限
    public static int DIALOG_GTYPE_NOMAL = 2;//只是告诉用户要设置

    private int showDialogType;
    private OnPermissionCallBackListener permissionCallBackListener;
    private CommonDialogUtils commonDialogUtils;
    private final LocationClientOption locationClientOption;

    /**
     * 外部获取这个LocationClientOption 根据自己需求更改需要的参数
     * @return
     */
    public LocationClientOption getLocationClientOption() {
        return locationClientOption;
    }

    /**
     * @param activity
     */
    public LocationUtils(Activity activity, int type) {
        this.activity = activity;
        this.showDialogType = type;
        locationClientOption = new LocationClientOption();
    }

    private LocationClient mLocationClient;

    /**
     * 初始化定位
     */
    public void initeLocation() {

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(activity);
        }
        if (mLocationClient.isStarted()) {
            return;
        }
        MyLocationListener mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setOpenGps(true);// 打开gps
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setAddrType("all");
        mLocationClient.setLocOption(locationClientOption);
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
        public void onReceiveLocation(final BDLocation location) {
            // 更新位置
            if (locationListener != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationListener.locationDateBack(location);
                    }
                });
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }


    /**
     * 设置权限
     */
    public void setPermition() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (showDialogType == DIALOG_GTYPE_SETTING) {
                        if (permissionCallBackListener != null) {
                            permissionCallBackListener.OnPermissionCallBack(true);
                        }
                        settingDialog();
                    } else if (showDialogType == DIALOG_GTYPE_NOMAL) {
                        if (permissionCallBackListener != null) {
                            permissionCallBackListener.OnPermissionCallBack(false);
                        }
                        ToastUtils.toastLong(activity, activity.getResources().getString(R.string.permission_no_map));
                    }
                } else {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                }
                return;
            } else {
                initeLocation();
            }
        } else {
            initeLocation();
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
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (permissionCallBackListener != null) {
                        permissionCallBackListener.OnPermissionCallBack(true);
                    }
                    initeLocation();
                } else {
                    if (showDialogType == DIALOG_GTYPE_SETTING) {
                        settingDialog();
                    } else if (showDialogType == DIALOG_GTYPE_NOMAL) {
                        if (permissionCallBackListener != null) {
                            permissionCallBackListener.OnPermissionCallBack(false);
                        }
                        ToastUtils.toastLong(activity, activity.getResources().getString(R.string.permission_no_map));
                    }
                }
                break;
        }
    }


    /**
     *
     */
    public void settingDialog() {

        if (commonDialogUtils == null) {
            commonDialogUtils = new CommonDialogUtils();
        }

        commonDialogUtils.showNomalDialog(activity, "无法获取你的位置信息。请在手机系统设置，权限管理中打开位置权限，允许领路人游客版版使用定位服务。", "取消", "设置", new CommonDialogUtils.OnDialogItemClickListener() {
            @Override
            public void onItemClickPositon(int positon) {
                switch (positon) {
                    case 0:
                        break;
                    case 1:
                        getAppDetailSettingIntent(activity);
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
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }


    public interface OnPermissionCallBackListener {
        void OnPermissionCallBack(boolean isPermissionOk);
    }

    public void setOnPermissionCallBackListener(OnPermissionCallBackListener permissionCallBackListener) {
        this.permissionCallBackListener = permissionCallBackListener;
    }

}
