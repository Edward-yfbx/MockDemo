package com.yfbx.mockdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:Edward
 * Date:2017/9/19
 * Description:
 */

public class MockLoc {

    private static final String TAG = MockLoc.class.getSimpleName();
    private static final String MOCK_LOCATION = "android.permission.ACCESS_MOCK_LOCATION";
    private Context context;
    private PackageManager pkgManager;

    public interface OnMockListener {
        void onMockRunning(List<String> mockAppNames);

        void onMockClear();
    }


    public static void checkMock(Context context, OnMockListener listener) {
        MockLoc mockLoc = new MockLoc(context);
        List<String> mockAppList = mockLoc.getMockAppList();
        if (mockAppList.size() == 0) {
            listener.onMockClear();
        } else {
            listener.onMockRunning(mockAppList);
        }
    }


    public MockLoc(Context context) {
        this.context = context;
        pkgManager = context.getPackageManager();
    }

    /**
     * 查询虚拟定位程序列表
     */
    public List<String> getMockAppList() {
        List<String> list = new ArrayList<>();
        List<ApplicationInfo> packages = pkgManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : packages) {
            if (hasMockPermission(appInfo) && isAlive(appInfo)) {
                list.add((String) pkgManager.getApplicationLabel(appInfo));
            }

        }
        return list;
    }


    /**
     * 是否具有虚拟定位权限
     */
    private boolean hasMockPermission(ApplicationInfo appInfo) {
        //获取该APP权限
        String[] permissions;
        try {
            PackageInfo packageInfo = pkgManager.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
            permissions = packageInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (permissions == null || permissions.length == 0) {
            return false;
        }

        //查询权限中是否有MOCK_LOCATION权限
        for (String permission : permissions) {
            if (permission.equals(MOCK_LOCATION)) {
                Log.i(TAG, pkgManager.getApplicationLabel(appInfo) + "has Mock Permission");
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某程序是否正在运行 或有进程存活
     */
    private boolean isAlive(ApplicationInfo appInfo) {
        boolean rstA = isAppRunning(appInfo.packageName);
        boolean rstB = isProcessRunning(appInfo.uid);
        return rstA || rstB;
    }

    /**
     * 判断某一应用是否正在运行
     */
    private boolean isAppRunning(String pkgName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = manager.getRunningTasks(200);
        Log.i(TAG, "isAppRunning: list size = " + list.size());
        for (ActivityManager.RunningTaskInfo info : list) {
            Log.i(TAG, "isAppRunning: " + info.baseActivity.getPackageName());
            if (info.baseActivity.getPackageName().equals(pkgName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 某一 uid 的程序是否有正在运行的进程，即是否存活
     */
    private boolean isProcessRunning(int uid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(200);
        Log.i(TAG, "isProcessRunning: list size = " + list.size());
        for (ActivityManager.RunningServiceInfo process : list) {
            if (uid == process.uid) {
                return true;
            }
        }
        return false;
    }

    /**
     * Android 6.0 以前，可以通过此方法判断虚拟定位是否开启
     */
    public static boolean isMockSettingsON(Context context) {
        return !Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }

}
