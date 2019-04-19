package kr.ac.gachon.www.buslinker.Account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kr.ac.gachon.www.buslinker.R;

public class DealLogDetailActivity extends AppCompatActivity {
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_log_detail);

        index = getIntent().getIntExtra("index", 0);
    }


    public void back(View v) {
        finish();
    }
}