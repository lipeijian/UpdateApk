package kntryer.downloadapk.server;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kntryer on 2017/10/17.
 */

public class DownloadApkService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_FINISHED = "ACTION_FINISHED";
    public static final String ACTION_CANCEL = "ACTION_CANCEL";
    public static final String ACTION_ERROR = "ACTION_ERROR";

    // 文件的保存路径
    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (ACTION_START.equals(intent.getAction())) {
                Log.e("---->", "onStartCommand");
                new DownLoadApkThread(intent.getIntExtra("id", 0)
                        , intent.getStringExtra("url"), intent.getStringExtra("name")).start();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class DownLoadApkThread extends Thread {

        private int id;
        private String downloadUrl;
        private String fileName;

        public DownLoadApkThread(int id, String downloadUrl, String fileName) {
            this.id = id;
            this.downloadUrl = downloadUrl;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            HttpURLConnection connLength = null;
            HttpURLConnection connFile = null;
            RandomAccessFile randomAccessFile = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(downloadUrl);

                //获取apk文件长度
                connLength = (HttpURLConnection) url.openConnection();
                connLength.setConnectTimeout(5000);
                connLength.setRequestMethod("GET");
                int code = connLength.getResponseCode();
                int length = -1;
                if (code == HttpURLConnection.HTTP_OK) {
                    length = connLength.getContentLength();
                    Log.i("---->", "file length is " + length);
                }

                //判断文件是否存在，不存在则创建
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, fileName);
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.setLength(length);

                //下载文件
                connFile = (HttpURLConnection) url.openConnection();
                connFile.setConnectTimeout(5000);
                connFile.setRequestMethod("GET");
                connFile.setRequestProperty("Range", "bytes=" + 0 + "-" + length);
                code = connFile.getResponseCode();

                //显示通知栏进度条
                Intent intent = new Intent();
                intent.setAction(ACTION_START);
                intent.putExtra("id", id);
                sendBroadcast(intent);

                if (code == HttpURLConnection.HTTP_PARTIAL) {
                    inputStream = connFile.getInputStream();
                    int finished = 0;
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(bytes)) != -1) {
                        //文件写入
                        randomAccessFile.write(bytes, 0, len);
                        //更新通知栏进度条
                        finished += len;
                        if (System.currentTimeMillis() - time > 1000) {
                            time = System.currentTimeMillis();
                            intent.setAction(ACTION_UPDATE);
                            intent.putExtra("finished", finished * 100 / length);
                            sendBroadcast(intent);
                        }
                    }
                }
                //关闭通知栏
                intent.setAction(ACTION_FINISHED);
                sendBroadcast(intent);

            } catch (MalformedURLException e) {
                sendBroadcast(new Intent().setAction(ACTION_ERROR));
                e.printStackTrace();
            } catch (IOException e) {
                sendBroadcast(new Intent().setAction(ACTION_ERROR));
                e.printStackTrace();
            } finally {
                if (connLength != null) {
                    connLength.disconnect();
                }
                if (connFile != null) {
                    connFile.disconnect();
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }
}
