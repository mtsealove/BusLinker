package kr.ac.gachon.www.buslinker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class SelectRouteActivity extends AppCompatActivity {
    private String depTmnNm, depTmnCd, arrTmnNm, arrTmnCd, registrationDate, cat;
    TextView depTmnTV, arrTmnTV, dateTV, expressTV, intercityTV;
    ListView dispatchLV;
    ArrayList<String> arrPrdtTm, corpNm, depTm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        depTmnTV=findViewById(R.id.depTmnNmTV);
        arrTmnTV=findViewById(R.id.arrTmnNmTV);
        dateTV=findViewById(R.id.dateTV);
        expressTV=findViewById(R.id.expressTV);
        intercityTV=findViewById(R.id.intercityTV);
        dispatchLV=findViewById(R.id.dispatchList);

        SystemUiTuner systemUiTuner=new SystemUiTuner(SelectRouteActivity.this);
        systemUiTuner.setStatusBarWhite();

        getParameter();
        setCat();
        if(cat.equals("express")) {
            getExpress();
        } else {

        }
    }

    private void getParameter() {   //매개변수 읽어오기
        Intent intent=getIntent();
        depTmnCd=intent.getStringExtra("depTmnCd");
        depTmnNm=intent.getStringExtra("depTmnNm")+"터미널";
        arrTmnCd=intent.getStringExtra("arrTmnCd");
        arrTmnNm=intent.getStringExtra("arrTmnNm")+"터미널";
        cat=intent.getStringExtra("cat");
        registrationDate=intent.getStringExtra("RegistrationDate");

        arrTmnTV.setText(arrTmnNm);
        depTmnTV.setText(depTmnNm);
        dateTV.setText(registrationDate);
    }

    private void setCat() { //카테고리 설정
        if(cat.equals("express")) {
            expressTV.setBackground(new ColorDrawable(getColor(R.color.colorPrimary)));
            intercityTV.setBackground(new ColorDrawable(getColor(R.color.light_gray)));
        } else {
            expressTV.setBackground(new ColorDrawable(getColor(R.color.light_gray)));
            intercityTV.setBackground(new ColorDrawable(getColor(R.color.colorPrimary)));
        }
    }

    private void getExpress() { //고속버스 데이터를 가져옴
        arrPrdtTm=new ArrayList<>();
        corpNm=new ArrayList<>();
        depTm=new ArrayList<>();
        final String key="MhyH4ySbB%2FINGpXIXZh63jG%2B5glyr88oqe58zxAnlTey8jLySc4lZzPO3mPS7bb1uIl7z7M9qIUEb65v1hl7Ug%3D%3D";
        final String query="http://openapi.tago.go.kr/openapi/service/ExpBusArrInfoService/getExpBusArrPrdtInfo?" +
                "serviceKey="+key
                +"&depTmnCd="+depTmnCd
                +"&arrTmnCd="+arrTmnCd;
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
                                if(tag.equals("depTm")) {
                                    xmlPullParser.next();
                                    depTm.add(xmlPullParser.getText());
                                } else if(tag.equals("arrPrdtTm")) {
                                    xmlPullParser.next();
                                    String arr=xmlPullParser.getText();
                                    arr=arr.substring(11, 16);
                                    arrPrdtTm.add(arr);
                                } else if(tag.equals("corpNm")) {
                                    xmlPullParser.next();
                                    corpNm.add(xmlPullParser.getText());
                                }
                                break;
                        }
                        eventType= xmlPullParser.next();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DispatchAdapter dispatchAdapter=new DispatchAdapter();
                                for(int i=0; i<depTm.size(); i++) {
                                    String dep=depTm.get(i);
                                    String arr=arrPrdtTm.get(i);
                                    String corp=corpNm.get(i);
                                    System.out.println("출발시간: "+dep);
                                    System.out.println("도착시간: "+arr);
                                    System.out.println("회사: "+corp);
                                    Drawable corpDrawable=getCorpDrawable(corp);
                                    dispatchAdapter.addItem(corpDrawable, arr, dep);
                                }
                                dispatchLV.setAdapter(dispatchAdapter);
                            }
                        });
                    }
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

    private Drawable getCorpDrawable(String corp) {
        corp=corp.replace("고속", "");
        Drawable resultDrawable=null;
        switch (corp) {
            case "중앙":
                resultDrawable=getDrawable(R.drawable.bus_central);
                break;
            case "충남":
                resultDrawable=getDrawable(R.drawable.bus_chung_nam);
                break;
            case "천일":
                resultDrawable=getDrawable(R.drawable.bus_chunil);
                break;
            case "대원":
                resultDrawable=getDrawable(R.drawable.bus_daewon);
                break;
            case "동양":
                resultDrawable=getDrawable(R.drawable.bus_dongyang);
                break;
            case "동부":
                resultDrawable=getDrawable(R.drawable.bus_dougbu);
                break;
            case "한일":
                resultDrawable=getDrawable(R.drawable.bus_hanil);
                break;
            case "코리아와이드경북":
                resultDrawable=getDrawable(R.drawable.bus_koreawide);
                break;
            case "금호":
                resultDrawable=getDrawable(R.drawable.bus_kumho);
                break;
            case "금호속리산":
                resultDrawable=getDrawable(R.drawable.bus_kumhosok);
                break;
            case "삼화":
                resultDrawable=getDrawable(R.drawable.bus_samhwa);
                break;
        }
        return resultDrawable;
    }
}
