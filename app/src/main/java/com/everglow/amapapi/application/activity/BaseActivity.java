package com.everglow.amapapi.application.activity;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.everglow.amapapi.R;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EverGlow on 2017/8/16 17:13
 */

public    class BaseActivity extends RxAppCompatActivity {

    private static final int REQUEST_MULTIPLE_PERMISSIONS = 222;
    protected final String TAG = getClass().getSimpleName();
    private String mAppName;
    private ProgressDialog progDialog;
    private static final String[] PermissionsNeeded = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mAppName = getApplicationName();

    }
    public String getApplicationName() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Android Application";
        }
    }

    public boolean mayMultiplePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> permissionsList = new ArrayList<>();
        for (String permissionNeed : PermissionsNeeded) {
            if (checkSelfPermission(permissionNeed) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permissionNeed);
            }
        }
        System.out.println("permissionsList.size() ="+permissionsList.size());
        if (permissionsList.size() == 0) return true;

        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_MULTIPLE_PERMISSIONS);
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @Nullable final String[] permissions,
                                           @Nullable int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MULTIPLE_PERMISSIONS:
                if (permissions == null || grantResults == null) return;
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permissionGranted(permission, i == permissions.length - 1);
                    } else {
                        String permissionDescribe = describePermission(permission);
                        showAlertDialog((dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    startApplicationDetails();
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    permissionDenied(permissions[0]);
                                    break;
                            }
                        }, "权限申请", "在设置-应用-" + mAppName + "-权限中开启" + permissionDescribe, "去设置", "取消");
                        break;
                    }
                }
                break;
        }
    }

    private String describePermission(String permission) {
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "存储空间权限,以正常使用" + mAppName + "功能";
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return "位置信息权限,以正常使用" + mAppName + "功能";
            default:
                return "应用所需权限,以正常使用" + mAppName + "功能";
        }
    }

    protected void permissionGranted(String permission, boolean complete) {
        Log.d(TAG, "permission-" + permission + "-Granted");
    }

    protected void permissionDenied(String permission) {
        Log.d(TAG, "permission-" + permission + "-Denied");
        finish();
    }

    public void showAlertDialog(DialogInterface.OnClickListener clickListener,
                                String... text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle(text[0]).setMessage(text[1])
                .setPositiveButton(text[2], clickListener)
                .setNegativeButton(text[3], clickListener);
        AlertDialog dialog = builder.show();
        dialog.setCancelable(false);
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(15);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        lp.width = (int) (metric.widthPixels - 50 * metric.density);
        dialog.getWindow().setAttributes(lp);
    }
    private void startApplicationDetails() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    /**
     * 显示进度框
     */
    public void showProgressDialog(String keyWord) {
        if (keyWord == null) {
            keyWord = "";
        }
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    public void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


}
