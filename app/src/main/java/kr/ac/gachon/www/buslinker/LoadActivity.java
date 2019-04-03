package kr.ac.gachon.www.buslinker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kr.ac.gachon.www.buslinker.Search.SearchRouteActivity;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        SystemUiTuner systemUiTuner=new SystemUiTuner(LoadActivity.this);
        systemUiTuner.setStatusBarWhite();
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
}
