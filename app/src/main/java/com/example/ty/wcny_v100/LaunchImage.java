package com.example.ty.wcny_v100;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by TY on 2015/10/5.
 */
public class LaunchImage extends Activity{

    /** 开启画面 */
    private static final int FAILURE = 0;
    private static final int SUCCESS = 1;
    private static final int OFFLINE = 2;

    /** 网络状态 */
    private static final int TYPE_NET_WORK_DISABLED = 0;
    private static final int TYPE_WIFI = 1;
    private static final int TYPE_MOBILE = 2;
    private static final int TYPE_OTHER =3;

    private TextView mVersionNameText;

    private static final int LAUNCH_TIME_MIN = 2000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.open_img);
        mVersionNameText = (TextView) findViewById(R.id.version_name);
        mVersionNameText.setText(R.string.version_name);

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                int result;
                long startTime = System.currentTimeMillis();
                result = loadingCache();
                long loadingTime = System.currentTimeMillis() - startTime;
                if(loadingTime < LAUNCH_TIME_MIN){
                    try{
                        Thread.sleep(LAUNCH_TIME_MIN - loadingTime);
                    }catch (InterruptedException error){
                        error.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                if (result == SUCCESS) {
                    finish();
                }
                else if(result == OFFLINE) {
                    AlertDialog launchDialog = new AlertDialog.Builder(LaunchImage.this).create();
                    launchDialog.setMessage("无法连接网络");
                }
            }
        }.execute(new Void[]{});
    }

    private int loadingCache(){
        if(checkNetworkType(this) == TYPE_NET_WORK_DISABLED){
            return OFFLINE;
        }
        return SUCCESS;
    }

    private static int checkNetworkType(Context mContext){
        try {
            final ConnectivityManager connectManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectManager.getActiveNetworkInfo();
            if(mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
                return TYPE_NET_WORK_DISABLED;
            }else{
                int netType = mobNetInfoActivity.getType();
                if(netType == connectManager.TYPE_WIFI)
                    return TYPE_WIFI;
                else if (netType == connectManager.TYPE_MOBILE){
                    return TYPE_MOBILE;
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return TYPE_OTHER;
        }
        return TYPE_OTHER;
    }
}
