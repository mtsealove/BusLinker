package kr.ac.gachon.www.buslinker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import kr.ac.gachon.www.buslinker.Search.SearchRouteActivity;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        SystemUiTuner systemUiTuner=new SystemUiTuner(LoadActivity.this);
        systemUiTuner.setStatusBarWhite();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED) {
            TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .setRationaleMessage("위치정보에 접근하기 위해 권한이 필요합니다")
                    .check();
        }
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                StartSearchActivity();
            }
        }, 1000);
    }

    private void StartSearchActivity() {    //메인 화면으로 이동
        Intent intent=new Intent(LoadActivity.this, SearchRouteActivity.class);
        startActivity(intent);
        finish();
    }

    PermissionListener permissionListener=new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(LoadActivity.this, "권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(LoadActivity.this, "권한이 거부되었기 때문에 위치 정보를 이용할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    };

}
