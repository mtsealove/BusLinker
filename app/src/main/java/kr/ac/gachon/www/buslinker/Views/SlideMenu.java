package kr.ac.gachon.www.buslinker.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.ac.gachon.www.buslinker.LoginActivity;
import kr.ac.gachon.www.buslinker.R;

public class SlideMenu extends RelativeLayout {
    LinearLayout myAccountLayout;
    TextView recentDealTV, nameTV, prepareShipTV, shippingTV, shippedTV;    //사용자 정보로 표시될 텍스트
    RelativeLayout loginLayout, inquireDealLayout, publicNoticeLayout, checkChargeLayout, faqLayout, memberLayout;  //하단에 표시될 레이아웃
    Button closeBtn;
    private Context context;
    public SlideMenu(Context context) {
        super(context);
        this.context=context;
        init();
    }
    public SlideMenu(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context=context;
        init();
    }

    public SlideMenu(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context=context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideMenu(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        String inflaterService=Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(inflaterService);
        View view=inflater.inflate(R.layout.slide_layout, SlideMenu.this, false);
        addView(view);

        //뷰 매칭
        closeBtn=view.findViewById(R.id.closeBtn);
        //로그인 했을 떄 보이는 뷰들
        myAccountLayout=view.findViewById(R.id.myAccountLayout);
        recentDealTV=view.findViewById(R.id.recentDealTV);
        nameTV=view.findViewById(R.id.nameTV);
        prepareShipTV=view.findViewById(R.id.prepareShipTV);
        shippingTV=view.findViewById(R.id.shippingTV);
        shippedTV=view.findViewById(R.id.shippedTV);
        //하단 뷰
        loginLayout=view.findViewById(R.id.loginLayout);
        memberLayout=view.findViewById(R.id.memberLayout);
        inquireDealLayout=view.findViewById(R.id.inquireLayout);
        publicNoticeLayout=view.findViewById(R.id.noticeLayout);
        checkChargeLayout=view.findViewById(R.id.check_chargeLayout);
        faqLayout=view.findViewById(R.id.faqLayout);

        loginLayout.setVisibility(View.VISIBLE);
        memberLayout.setVisibility(View.GONE);
        myAccountLayout.setVisibility(GONE);

        loginLayout.setOnClickListener(new OnClickListener() {  //로그인 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    private void Login() {  //로그인 액티비티로 이동
        Intent intent=new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
