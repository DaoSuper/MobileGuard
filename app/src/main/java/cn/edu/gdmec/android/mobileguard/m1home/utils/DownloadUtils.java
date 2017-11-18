package cn.edu.gdmec.android.mobileguard.m1home.utils;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.MimeTypeMap;

/**
 * Created by X on 2017/9/17.
 */

public class DownloadUtils {
    public void downloadApk(String url, String targetFile, Context context){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap  = MimeTypeMap.getSingleton();
        String mimeSring = mimeTypeMap.getMimeTypeFromExtension(mimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeSring);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("/download",targetFile);

        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        long mTaskid = downloadManager.enqueue(request);

    }
}
