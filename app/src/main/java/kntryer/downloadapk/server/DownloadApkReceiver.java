package kntryer.downloadapk.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

import kntryer.downloadapk.util.CustomTools;
import kntryer.downloadapk.util.NotificationUtil;

/**
 * Created by kntryer on 2017/10/17.
 */

public class DownloadApkReceiver extends BroadcastReceiver {

    private NotificationUtil mNotificationUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationUtil = new NotificationUtil(context);

        if (DownloadApkService.ACTION_START.equals(intent.getAction())) {
            // 下载开始的时候启动通知栏
            mNotificationUtil.showNotification(intent.getIntExtra("id", 0));
        } else if (DownloadApkService.ACTION_UPDATE.equals(intent.getAction())) {
            // 更新进度条
            mNotificationUtil.updateNotification(intent.getIntExtra("id", 0), intent.getIntExtra("finished", 0));
        } else if (DownloadApkService.ACTION_FINISHED.equals(intent.getAction())) {
            // 下载结束后取消通知
            mNotificationUtil.cancelNotification(intent.getIntExtra("id", 0));
            CustomTools.installApk(context, new File(DownloadApkService.path + "宝宝.apk"));
        } else if (DownloadApkService.ACTION_CANCEL.equals(intent.getAction())) {
            // 下载结束后取消通知
            mNotificationUtil.cancelNotification(intent.getIntExtra("id", 0));
        } else if (DownloadApkService.ACTION_ERROR.equals(intent.getAction())) {
            CustomTools.showToast(context, "提示", "下载链接有误", "确定");
        }
    }
}
