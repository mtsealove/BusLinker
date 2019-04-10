package kr.ac.gachon.www.buslinker.Search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.ac.gachon.www.buslinker.Entity.Terminal;
import kr.ac.gachon.www.buslinker.R;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class SearchTerminalActivity extends AppCompatActivity {
    private String node, cat;
    TextView titleTV, noResultTV, searchResultTV;
    EditText searchET;
    Button searchBtn;
    ListView resultListView;
    ArrayList<String> tmnCd, tmnNm;
    private String terminalCode, terminalName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_terminal);
        searchBtn=findViewById(R.id.searchBtn);
        searchET=findViewById(R.id.searchET);
        resultListView=findViewById(R.id.resultLV);
        titleTV=findViewById(R.id.titleTV);
        noResultTV=findViewById(R.id.noResultTV);
        searchResultTV=findViewById(R.id.searchResultTV);

        //상단바 색상 변경
        SystemUiTuner systemUiTuner=new SystemUiTuner(SearchTerminalActivity.this);
        systemUiTuner.setStatusBarYellow();

        //고속/시외버스인지, 출발/도착인지 검색
        Intent getIT=getIntent();
        node=getIT.getStringExtra("node");
        cat=getIT.getStringExtra("cat");

        //검색 타이틀 설정
        if(node.equals("start")) titleTV.setText("출발지 검색");
        else titleTV.setText("도착지 검색");

        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_SEARCH) {
                    Search();
                }
                return false;
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });

        if(cat.equals("express")) {
            showCloseExpressTerminal();
        }
    }

    private void Search() {
        String para=searchET.getText().toString();
        if(para.length()==0) Toast.makeText(SearchTerminalActivity.this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
        else if(cat.equals("express")) getExpressTerminal(para);
    }

    private void getExpressTerminal(String terminalName) {  //고속버스 터미널 조회
        tmnNm=new ArrayList<>();
        tmnCd=new ArrayList<>();
        terminalName= URLEncoder.encode(terminalName);
        final String key="MhyH4ySbB%2FINGpXIXZh63jG%2B5glyr88oqe58zxAnlTey8jLySc4lZzPO3mPS7bb1uIl7z7M9qIUEb65v1hl7Ug%3D%3D";
        final String query="http://openapi.tago.go.kr/openapi/service/ExpBusInfoService/getExpBusTrminlList?" +
                "serviceKey="+key
                +"&numOfRows=200"
                +"&terminalNm="+terminalName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL(query);
                    InputStream is=url.openStream();
                    XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                    XmlPullParser xmlPullParser=factory.newPullParser();
                    xmlPullParser.setInput(new InputStreamReader(is, "UTF-8"));

                    String tag;
                    xmlPullParser.next();
                    int eventType=xmlPullParser.getEventType();
                    while(eventType!=XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                tag=xmlPullParser.getName();
                                if(tag.equals("terminalId")) {
                                    xmlPullParser.next();
                                    tmnCd.add(xmlPullParser.getText());
                                } else if(tag.equals("terminalNm")) {
                                    xmlPullParser.next();
                                    tmnNm.add(xmlPullParser.getText());;
                                } break;
                        }
                        eventType= xmlPullParser.next();
                    }
                    RemoveDongSeoul();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTerminalList();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void RemoveDongSeoul() {    //동서울 터미널 2중 출력 제거
        int index=0;
        boolean exist=false;
        for(int i=0; i<tmnCd.size(); i++) { //동서울 터미널이 존재하는지 확인
            if(tmnCd.get(i).equals("NAEK032")) {
                exist=true;
                index=i;
            }
        }
        if(exist) { //동서울이 리스트에 존재하면
            tmnCd.remove(index);
            tmnNm.remove(index);
        }
    }
    private void setTerminalList() {    //리스트뷰에 출력 및 클릭 이벤트 설정
        searchResultTV.setText("검색 결과");
        if(tmnCd.size()==0){    //검색 결과가 없으면
            resultListView.setVisibility(View.GONE);
            noResultTV.setVisibility(View.VISIBLE);
        } else{ //결과가 있으면
            resultListView.setVisibility(View.VISIBLE);
            noResultTV.setVisibility(View.GONE);
            ArrayAdapter adapter = new ArrayAdapter(SearchTerminalActivity.this, R.layout.support_simple_spinner_dropdown_item, tmnNm);
            resultListView.setAdapter(adapter);
            resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    terminalName = tmnNm.get(i);  //터미널 이름 설정
                    terminalCode = tmnCd.get(i);  //터미널 코드 설정
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("tmnCd", terminalCode);
                    resultIntent.putExtra("tmnNm", terminalName);
                    setResult(RESULT_OK, resultIntent); //이전 액티비티에 데이터 넘겨줌
                    finish();
                }
            });
        }
    }

    ArrayList<Terminal> terminalArrayList;
    private void ReadExpressFile() {    //터미널 위경도 파일 읽기
        terminalArrayList=new ArrayList<>();
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("terminal_latlang.dat")));
            String tmp;
            while((tmp=br.readLine())!=null) {
                String tmnNm=tmp.split(",")[0];
                String tmnCd=tmp.split(",")[1];
                double latitude=Double.parseDouble(tmp.split(",")[2]);
                double longitude=Double.parseDouble(tmp.split(",")[3]);
                terminalArrayList.add(new Terminal(tmnNm, tmnCd, latitude, longitude));
            }
            br.close();
            System.out.println("터미널 파일 읽음");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void showCloseExpressTerminal() {
        ReadExpressFile();

        LocationManager locationManager;
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null) {
            for(Terminal terminal: terminalArrayList) { //모든 객체에 대해 거리 설정
                terminal.getDistance(location.getLatitude(), location.getLongitude());
            }
            terminalArrayList=Terminal.sortTerminalListByDistance(terminalArrayList); //거리 기준으로 정렬
            final ArrayList<String> terminalNms=new ArrayList<>();
            final ArrayList<String> terminalCds=new ArrayList<>();
            for(int i=0; i<3; i++) {
                terminalNms.add(terminalArrayList.get(i).getTerminalName());
                terminalCds.add(terminalArrayList.get(i).getTerminalCode());
            }

            searchResultTV.setText("추천");
            ArrayAdapter adapter = new ArrayAdapter(SearchTerminalActivity.this, R.layout.support_simple_spinner_dropdown_item, terminalNms);
            resultListView.setAdapter(adapter);
            resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    terminalName = terminalNms.get(i);  //터미널 이름 설정
                    terminalCode = terminalCds.get(i);  //터미널 코드 설정
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("tmnCd", terminalCode);
                    resultIntent.putExtra("tmnNm", terminalName);
                    setResult(RESULT_OK, resultIntent); //이전 액티비티에 데이터 넘겨줌
                    finish();
                }});
        }

    }

    public void back(View view) {
        finish();
    }
}
