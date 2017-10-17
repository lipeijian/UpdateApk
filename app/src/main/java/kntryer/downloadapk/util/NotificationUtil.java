package kntryer.downloadapk.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import kntryer.downloadapk.MainActivity;
import kntryer.downloadapk.R;

/**
 * Created by kntryer on 2017/10/13.
 */
public class NotificationUtil {

    private Context mContext;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;

    public NotificationUtil(Context context) {
        this.mContext = context;
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
    }

    /**
     * 显示通知栏
     *
     * @param id
     */
    public void showNotification(int id) {

        mBuilder.setTicker("宝宝.apk 下载");//Ticker是状态栏显示的提示
        mBuilder.setContentTitle("宝宝");
        mBuilder.setProgress(100, 0, false);
        mBuilder.setContentText(0 + "%");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));//通知栏的大图标

        Intent msgIntent = new Intent();
        msgIntent.setClass(mContext, MainActivity.class);
        msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);//点击跳转

        mManager.notify(id, mBuilder.build());
    }

    /**
     * 取消通知栏通知
     */
    public void cancelNotification(int id) {
        mManager.cancel(id);
    }

    /**
     * 更新通知栏进度条
     *
     * @param id       获取Notification的id
     * @param progress 获取的进度
     */
    public void updateNotification(int id, int progress) {
        if (mBuilder != null) {
            mBuilder.setTicker("宝宝.apk 开始下载");//Ticker是状态栏显示的提示
            mBuilder.setContentTitle("宝宝");
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));//通知栏的大图标
            mBuilder.setProgress(100, progress, false);
            mBuilder.setContentText(progress + "%");
            mManager.notify(id, mBuilder.build());
        }
    }
}
