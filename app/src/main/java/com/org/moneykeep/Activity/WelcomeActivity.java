package com.org.moneykeep.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.org.moneykeep.Activity.SignInView.SignInActivity;
import com.org.moneykeep.R;
import com.org.moneykeep.Recevier.Service.KeepAliveJobService;
import com.org.moneykeep.Recevier.Service.LocalForegroundService;
import com.org.moneykeep.Recevier.Service.RemoteForegroundService;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {
    //private static HashMap<String, Integer> IntegerColor;
    public SharedPreferences userdata;
    private static boolean isPermissionRequested = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        /*Bmob.initialize(this, "3b1d2e279e692c9f417fd752066fb91b");*/

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        requestPermission();

        userdata = getSharedPreferences("user", MODE_PRIVATE);
        boolean user_islogin = userdata.getBoolean("user_isused", false);
        String user_account = userdata.getString("user_email", "");
        String user_name = userdata.getString("user_name", "");
        int user_id = userdata.getInt("user_objectId", 0);


        //JobSchedulerUntil.scheduleJob(getApplicationContext(),1000);
        /*Intent service = new Intent(getApplicationContext(), MessageService.class);
        getApplicationContext().startForegroundService(service);*/

        Intent intent;
        if (user_islogin) {
            startService(new Intent(this, LocalForegroundService.class));
            startService(new Intent(this, RemoteForegroundService.class));
            // JobScheduler 拉活
            KeepAliveJobService.startJob(this);
            Bundle bundle = new Bundle();
            bundle.putString("user_email", user_account);
            bundle.putString("user_name", user_name);
            bundle.putInt("user_objectId", user_id);
            intent = new Intent(WelcomeActivity.this, UserMainActivity.class);
            intent.putExtras(bundle);
        } else {
            intent = new Intent(WelcomeActivity.this, SignInActivity.class);
        }
        startActivity(intent);
        finish();
    }

    /**
     * Android6.0之后需要动态申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (!isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissionsList = new ArrayList<>();

            @SuppressLint("InlinedApi") String[] permissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION


            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (permissionsList.isEmpty()) {
                return;
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
    }
}