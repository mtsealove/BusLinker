package kr.ac.gachon.www.buslinker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    Button nextBtn, prevBtn;

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
        nextBtn=findViewById(R.id.nextBtn);
        prevBtn=findViewById(R.id.previousBtn);

        SystemUiTuner systemUiTuner=new SystemUiTuner(SelectRouteActivity.this);
        systemUiTuner.setStatusBarWhite();

        getParameter();
        setCat();
        if(cat.equals("express")) {
            getExpress();
        } else {

        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextDate();
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousDate();
            }
        });
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
        String Date4Show=registrationDate.substring(0, 4)+"년 "+registrationDate.substring(4, 6)+"월 "+registrationDate.substring(6, 8)+"일";
        dateTV.setText(Date4Show);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                int DongSeoul=1;
                if(depTmnCd.equals("NAEK030")||arrTmnCd.equals("NAEK030")) DongSeoul=2;   //동서울이 매개변수이면
                try {
                    for(int i=0; i<DongSeoul; i++) {    //
                        if(i==1) {
                            System.out.println("동서울 발견");
                            if(depTmnCd.equals("NAEK030")) depTmnCd="NAEK032";
                            else arrTmnCd="NAEK032";
                            System.out.println("arr: "+arrTmnCd+" dep: "+depTmnCd);
                        }
                        final String query="http://openapi.tago.go.kr/openapi/service/ExpBusInfoService/getStrtpntAlocFndExpbusInfo?" +
                                    "serviceKey="+key
                                    +"&numOfRows=100"
                                    +"&depTerminalId="+depTmnCd
                                    +"&arrTerminalId="+arrTmnCd
                                    +"&depPlandTime="+registrationDate;
                        URL url = new URL(query);
                        InputStream is = url.openStream();
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser xmlPullParser = factory.newPullParser();
                        xmlPullParser.setInput(new InputStreamReader(is, "UTF-8"));

                        String tag;
                        xmlPullParser.next();
                        int eventType = xmlPullParser.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    tag = xmlPullParser.getName();
                                    if (tag.equals("depPlandTime")) {
                                        xmlPullParser.next();
                                        String time=xmlPullParser.getText().substring(8, 12);
                                        System.out.println(time);
                                        time=time.substring(0, 2)+"시 "+time.substring(2, 4)+"분";
                                        depTm.add(time);
                                    } else if (tag.equals("arrPlandTime")) {
                                        xmlPullParser.next();
                                        String time = xmlPullParser.getText().substring(8, 12);
                                        time=time.substring(0, 2)+"시 "+time.substring(2, 4)+"분";
                                        arrPrdtTm.add(time);
                                    }
                                    break;
                            }
                            eventType = xmlPullParser.next();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DispatchAdapter dispatchAdapter=new DispatchAdapter();
                            for(int i=0; i<depTm.size(); i++) {
                                String dep=depTm.get(i);
                                String arr=arrPrdtTm.get(i);
                                System.out.println("출발시간: "+dep);
                                System.out.println("도착시간: "+arr);
                                dispatchAdapter.addItem( arr, dep);
                            }
                            dispatchLV.setAdapter(dispatchAdapter);
                            dispatchLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    ShowInputDialog(depTm.get(i), arrPrdtTm.get(i));
                                }
                            });
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

    private void nextDate() {
        int year=Integer.parseInt(registrationDate.substring(0, 4));
        int month=Integer.parseInt(registrationDate.substring(4, 6));
        int day=Integer.parseInt(registrationDate.substring(6, 8));

        if(month==2&&day==28) { //2월일 경우
            month=3;
            day=1;
        } else if(month==12&&day==31) { //1년의 마지막 날일 경우
            year++;
            month=1;
            day=1;
        }  else  {
            if(day==30&&(month==4||month==6||month==9||month==11))  {   //30일 짜리 달이면
                month++;
                day=1;
            } else if(day==31) {
                month++;
                day=1;
            } else {
                day++;
            }
        }
        registrationDate=year+"";
        if(month<10) registrationDate+="0";
        registrationDate+=month;
        if(day<10) registrationDate+="0";
        registrationDate+=day;

        String Date4Show=registrationDate.substring(0, 4)+"년 "+registrationDate.substring(4, 6)+"월 "+registrationDate.substring(6, 8)+"일";
        dateTV.setText(Date4Show);

        if(cat.equals("express")) getExpress();
    }

    private void previousDate() {
        int year=Integer.parseInt(registrationDate.substring(0, 4));
        int month=Integer.parseInt(registrationDate.substring(4, 6));
        int day=Integer.parseInt(registrationDate.substring(6, 8));

        if(day==1) {  //1달의 첫날일 경우
            switch (month) {
                case 1: year--;
                    month=12;
                    day=31;
                    break;
                case 2: month--;
                    day=31;
                    break;
                case 3: month--;
                    day=28;
                    break;
                case 4: month--;
                    day=31;
                    break;
                case 5: month--;
                day=30;
                break;
                case 6: month--;
                day=31;
                break;
                case 7: month--;
                day=30;
                break;
                case 8: month--;
                day=31;
                break;
                case 9: month--;
                day=31;
                break;
                case 10: month--;
                day=30;
                break;
                case 11: month--;
                day=31;
                break;
                case 12: month--;
                day=30; break;
            }
        }
        else {
            day--;
        }
        registrationDate=year+"";
        if(month<10) registrationDate+="0";
        registrationDate+=month;
        if(day<10) registrationDate+="0";
        registrationDate+=day;
        String Date4Show=registrationDate.substring(0, 4)+"년 "+registrationDate.substring(4, 6)+"월 "+registrationDate.substring(6, 8)+"일";
        dateTV.setText(Date4Show);

        if(cat.equals("express")) getExpress();
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

    private void ShowInputDialog(final String depTime, final String arrTime) {  //가로,세로,높이,무게 입력 다이얼로그 출력
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_input_xyz, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(SelectRouteActivity.this);
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        final EditText XET=view.findViewById(R.id.XET);
        final EditText YET=view.findViewById(R.id.YET);
        final EditText ZET=view.findViewById(R.id.ZET);
        final EditText weightET=view.findViewById(R.id.weightET);
        Button confirmBtn=view.findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //파라미터 미입력 체크
                if(XET.getText().toString().length()==0) Toast.makeText(SelectRouteActivity.this, "가로를 입력하세요",Toast.LENGTH_SHORT).show();
                else if(YET.getText().toString().length()==0) Toast.makeText(SelectRouteActivity.this, "세로를 입력하세요",Toast.LENGTH_SHORT).show();
                else if(ZET.getText().toString().length()==0) Toast.makeText(SelectRouteActivity.this, "높이를 입력하세요",Toast.LENGTH_SHORT).show();
                else if(weightET.getText().toString().length()==0) Toast.makeText(SelectRouteActivity.this, "무게를 입력하세요",Toast.LENGTH_SHORT).show();
                else {
                    //입력한 내용을 읽어오기
                    double X=Double.parseDouble(XET.getText().toString());
                    double Y=Double.parseDouble(YET.getText().toString());
                    double Z=Double.parseDouble(ZET.getText().toString());
                    double weight=Double.parseDouble(weightET.getText().toString());

                    //인텐트 형성 및 데이터 전송
                    Intent intent=new Intent(SelectRouteActivity.this, SetInformationActivity.class);
                    intent.putExtra("depTime", depTime);    //출발시간
                    intent.putExtra("arrTime", arrTime);    //도착시간
                    intent.putExtra("X", X);    //가로
                    intent.putExtra("Y", Y);    //세로
                    intent.putExtra("Z", Z);    //높이
                    intent.putExtra("registrationDate", registrationDate);  //신청일
                    intent.putExtra("weight", weight);  //무게
                    intent.putExtra("arrTmnNm", arrTmnNm);  //도착 터미널 이름
                    intent.putExtra("depTmnNm", depTmnNm);  //출발 터미널 이름
                    startActivity(intent);
                    dialog.cancel();
                }
            }
        });
        dialog.show();

    }
}
