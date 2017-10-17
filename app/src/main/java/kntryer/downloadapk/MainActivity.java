package kntryer.downloadapk;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import kntryer.downloadapk.model.UpdateApk;
import kntryer.downloadapk.server.DownloadApkReceiver;
import kntryer.downloadapk.server.DownloadApkService;
import kntryer.downloadapk.util.CustomTools;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by kntryer on 2017/10/13.
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private String TAG = "MainActivity";
    DownloadApkReceiver downloadApkReceiver;
    UpdateApk updateApk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateApk = new UpdateApk("1.9.11", "", "http://7xki5q.com1.z0.glb.clouddn.com/dibao.apk", 0);
        findViewById(R.id.btn_update_y).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateApk.setVersion("1.9.11");
                checkUpdateApk();
            }
        });
        findViewById(R.id.btn_update_n).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateApk.setVersion("1.4.6");
                checkUpdateApk();
            }
        });
    }

    /**
     * 检查更新
     */
    public void checkUpdateApk() {
        if (CustomTools.checkVersionUpdate(this, updateApk.getVersion())) {
            methodRequiresPermission();
        } else {
            CustomTools.showToast(this, "提示", "宝宝目前是最新的，不需要更新哟", "确定");
        }
    }

    public final int RC_STORAGE = 1001;

    @AfterPermissionGranted(RC_STORAGE)
    private void methodRequiresPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadApkService.ACTION_START);
            intentFilter.addAction(DownloadApkService.ACTION_UPDATE);
            intentFilter.addAction(DownloadApkService.ACTION_FINISHED);
            intentFilter.addAction(DownloadApkService.ACTION_CANCEL);
            intentFilter.addAction(DownloadApkService.ACTION_ERROR);
            downloadApkReceiver = new DownloadApkReceiver();
            registerReceiver(downloadApkReceiver, intentFilter);
            CustomTools.startUpdateApk(this, updateApk.getStatus(), updateApk.getUrl());
        } else {
            EasyPermissions.requestPermissions(this, "更新应用需要打开存储权限", RC_STORAGE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        if (downloadApkReceiver != null) {
            unregisterReceiver(downloadApkReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, "你已打开存储权限，可以更新应用了", Toast.LENGTH_SHORT).show();
        }
    }

}
