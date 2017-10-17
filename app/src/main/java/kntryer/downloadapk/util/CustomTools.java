package kntryer.downloadapk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import java.io.File;

import kntryer.downloadapk.BuildConfig;
import kntryer.downloadapk.server.DownloadApkService;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by kntryer on 2017/10/13.
 */
public class CustomTools {

    /**
     * @param context
     * @param title
     * @param msg
     * @param positive
     */
    public static void showToast(Context context, String title, String msg, String positive) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setCanceledOnTouchOutside(true);
        if (!positive.equals("")) {
            dialog.setPositiveButton(positive, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }


    /**
     * 检查版本号是否一致
     *
     * @param context
     * @param serverVersion
     * @return
     */
    public static boolean checkVersionUpdate(Context context, String serverVersion) {
        String[] serverVersions = serverVersion.split("\\.");
        String[] localVersions = getVersionName(context).split("\\.");
        int num = serverVersions.length > localVersions.length ? localVersions.length : serverVersions.length;

        for (int i = 0; i < num; i++) {
            if (serverVersions[i].compareTo(localVersions[i]) > 0) {
                return true;
            } else if (serverVersions[i].compareTo(localVersions[i]) < 0) {
                return false;
            }
        }
        return serverVersions.length > localVersions.length;
    }

    /**
     * 版本更新
     *
     * @param context
     * @param status
     * @param url
     */
    public static void startUpdateApk(final Context context, int status, final String url) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle("更新啦");
        dialog.setMessage("快来体验崭新的宝宝！");
        dialog.setCanceledOnTouchOutside(status == 0);
        dialog.setPositiveButton("立即更新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动下载服务，显示通知栏
                Log.e("---->", "下载更新");

                Intent intent = new Intent(context, DownloadApkService.class);
                intent.setAction(DownloadApkService.ACTION_START);
                intent.putExtra("id", 0);
                intent.putExtra("url", url);
                intent.putExtra("name", "宝宝.apk");
                context.startService(intent);
                dialog.dismiss();
            }
        });
        if (status == 0) {
            dialog.setNegativeButton("稍候更新", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
