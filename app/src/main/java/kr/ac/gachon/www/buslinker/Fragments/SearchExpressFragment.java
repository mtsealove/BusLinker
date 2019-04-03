package kr.ac.gachon.www.buslinker.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.gachon.www.buslinker.R;
import kr.ac.gachon.www.buslinker.Search.SearchTerminalActivity;
import static android.app.Activity.RESULT_OK;

public class SearchExpressFragment extends Fragment {
    LinearLayout startLayout, destinationLayout, registrationLayout;
    TextView startTV, destinationTV, registrationTV;
    Button swapBtn;
    final String cat="express";
    final int ExpressStart=1, ExpressDestination=2;

    private String startTmnCd, destinationTmnCd, startTmnNm, destinationTmnNm, registrationDate;
    public SearchExpressFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search_express, container, false);

        startLayout=view.findViewById(R.id.startLayout);
        destinationLayout=view.findViewById(R.id.destinationLayout);
        registrationLayout=view.findViewById(R.id.registrationDateLayout);
        startTV=view.findViewById(R.id.startTV);
        destinationTV=view.findViewById(R.id.destinationTV);
        registrationTV=view.findViewById(R.id.registrationDateTV);
        swapBtn=view.findViewById(R.id.swapBtn);
        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwapTerminalCd();
            }
        });

        startLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchStart();
            }
        });

        destinationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchDestination();
            }
        });
        registrationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRegistrationDate();
            }
        });
        return view;
    }

    private void SwapTerminalCd() { //출발지와 도착지를 변경
        //각 터미널 코드를 변경
        String tmp=startTmnCd;
        startTmnCd=destinationTmnCd;
        destinationTmnCd=tmp;
        //터미널 이름도 변경
        tmp=startTmnNm;
        startTmnNm=destinationTmnNm;
        destinationTmnNm=tmp;

        //화면에 표시
        destinationTV.setText(destinationTmnNm);
        startTV.setText(startTmnNm);
    }

    private void SearchStart() {    //출발지 검색
        Intent intent=new Intent(getContext(), SearchTerminalActivity.class);
        intent.putExtra("cat", cat);
        intent.putExtra("node", "start");
        startActivityForResult(intent, ExpressStart);
    }

    private void SearchDestination() {  //도착지 검색
        Intent intent=new Intent(getContext(), SearchTerminalActivity.class);
        intent.putExtra("cat", cat);
        intent.putExtra("node", "destination");
        startActivityForResult(intent, ExpressDestination);
    }


    private void setRegistrationDate() {   //접수일 선택
        Date date=new Date();
        SimpleDateFormat yearFormat=new SimpleDateFormat("yy");
        SimpleDateFormat monthFormat=new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat=new SimpleDateFormat("dd");
        int year=Integer.parseInt(yearFormat.format(date));
        int month=Integer.parseInt(monthFormat.format(date));
        int day=Integer.parseInt(dayFormat.format(date));

        DatePickerDialog dialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                registrationDate=""+i+(i1+1)+i2;
                registrationTV.setText(i+"년 "+(i1+1)+"월 "+i2+"일");
            }
        },year, month, day);
        dialog.getDatePicker().setMinDate(date.getTime());
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case ExpressStart:
                    startTmnNm=data.getStringExtra("tmnNm");
                    startTmnCd=data.getStringExtra("tmnCd");
                    startTV.setText(startTmnNm);
                    break;
                case ExpressDestination:
                    destinationTmnNm=data.getStringExtra("tmnNm");
                    destinationTmnCd=data.getStringExtra("tmnCd");
                    destinationTV.setText(destinationTmnNm);
                    break;
            }
        }
    }

}
