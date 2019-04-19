package kr.ac.gachon.www.buslinker.Account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.ac.gachon.www.buslinker.Entity.DealLog;
import kr.ac.gachon.www.buslinker.Entity.Member;
import kr.ac.gachon.www.buslinker.R;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class DealLogListActivity extends AppCompatActivity {
    ListView DealLogListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_log_list);
        DealLogListView = findViewById(R.id.DealLogList);

        SystemUiTuner systemUiTuner = new SystemUiTuner(this);
        systemUiTuner.setStatusBarWhite();

        setDealLogListView();
    }

    private void setDealLogListView() { //거래내역 조회
        ArrayList<DealLog> dealLogs = Member.user.getDealLogs();
        ArrayList<String> titleList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/hh");
        for (DealLog dealLog : dealLogs) { //출/도착지 및 시간을 타이틀로
            String title = dealLog.getDepTerminal() + " -> " + dealLog.getArrTerminal() + " " + dateFormat.format(dealLog.getDeliveryTime());
            titleList.add(title);
        }
        ArrayAdapter adapter = new ArrayAdapter(DealLogListActivity.this, R.layout.support_simple_spinner_dropdown_item, titleList);
        DealLogListView.setAdapter(adapter);
    }
}
