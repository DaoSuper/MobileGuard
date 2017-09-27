package com.example.x.mobileguard.m1home.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.print.PageRange;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.os.Handler;
import com.example.x.mobileguard.R;
import com.example.x.mobileguard.m1home.HomeActivity;
import com.example.x.mobileguard.m1home.entity.VersionEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by X on 2017/9/17.
 */

public class VersionUpdateUtils {
    private String mVersion;
    private Activity context;
    private VersionEntity versionEntity;

    private static final int MESSAGE_IO_ERROR = 102;
    private static final int MESSAGE_JSON_ERROR = 103;
    private static final int MESSAGE_SHOW_DIALOG = 104;
    private static final int MESSAGE_ENTERHOME = 105;

   private Handler handler = new Handler(){
       public void handlerMessage(Message msg){
           switch (msg.what){
               case MESSAGE_IO_ERROR:
                   Toast.makeText(context,"IO错误",Toast.LENGTH_LONG).show();
                   break;
               case MESSAGE_JSON_ERROR:
                   Toast.makeText(context,"JSON解析错误",Toast.LENGTH_LONG).show();
                   break;
               case MESSAGE_SHOW_DIALOG:
                   showUpdateDialog(versionEntity);
                   break;
               case MESSAGE_ENTERHOME:
                   Intent intent = new Intent(context, HomeActivity.class);
                   context.startActivity(intent);
                   context.finish();
                   break;
           }
       }
   };

    public VersionUpdateUtils(String mVersion,Activity context){
        this.mVersion = mVersion;
        this.context = context;
    }

    public void getCloudVersion(){
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),5000);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(),5000);
            HttpGet httpGet = new HttpGet("http://androif2017.duapp.com/updateinfo.html");
            HttpResponse execute  = httpClient.execute(httpGet);
            if (execute.getStatusLine().getStatusCode()==200){
                HttpEntity httpEntity = execute.getEntity();
                String result = EntityUtils.toString(httpEntity,"utf-8");
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                versionEntity.versionCode = jsonObject.getString("code");
                versionEntity.description = jsonObject.getString("des");
                versionEntity.apkurl = jsonObject.getString("apkurl");
                if (!mVersion.equals(versionEntity.versionCode)){
                    //版本不同，需要升级
                    handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
                }
            }
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_ERROR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_ERROR);
            e.printStackTrace();
        }
    }

    private void showUpdateDialog(final VersionEntity versionEntity){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到有新版本："+versionEntity.versionCode);
        builder.setMessage(versionEntity.description);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setPositiveButton("立刻升级",new DialogInterface.OnClickListener(){
                 @Override
                 public void onClick(DialogInterface dialogInterface,int i){
                    downloadNewApk(versionEntity.apkurl);
                 }
                });
        builder.setPositiveButton("暂不升级",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int i){
                 dialogInterface.dismiss();
                 enterHome();
            }
        });
        builder.show();
    }
    private void enterHome(){
        handler.sendEmptyMessage(MESSAGE_ENTERHOME);
    }
    private void downloadNewApk(String apkurl){
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downloadApk(apkurl,"moblieguard.apk",context);
    }
}
