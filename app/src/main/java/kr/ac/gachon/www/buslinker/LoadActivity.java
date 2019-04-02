package kr.ac.gachon.www.buslinker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

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
