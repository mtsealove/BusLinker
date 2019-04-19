package kr.ac.gachon.www.buslinker.Entity;

import java.util.Date;

public class DealLog {  //거래내역 클래스
    int LogID;  //기록 ID
    String MemberID;    //사용자 ID
    String DepTerminal; //출발 터미널
    String ArrTerminal; //도착 터미널
    String SendPersonName;  //보내는 사람 이름
    String SendPersonNumber;    //보내는 사람 번호
    String ReceivePersonName;   //받는 사람 이름
    String ReceivePersonNumber; //받는 사람 번호
    Date DeliveryTime;  //보내는 시간
    int Side;   //세 변의 합
    int Weight; //무게
    String PayMethod;   //결제 방식
    String Message; //메세지
    Date PayTime;   //결제 시간
    int Price;  //가격
    String State;   //현재 상태

    public DealLog(int LogID, String MemberID, String DepTerminal, String ArrTerminal, String SendPersonName, String SendPersonNumber, String ReceivePersonName, String ReceivePersonNumber, Date DeliveryDate, int Side, int Weight, String PayMethod, String Message, Date PayTime, int Price, String State) {
        this.LogID = LogID;
        this.MemberID = MemberID;
        this.DepTerminal = DepTerminal;
        this.ArrTerminal = ArrTerminal;
        this.SendPersonName = SendPersonName;
        this.SendPersonNumber = SendPersonNumber;
        this.ReceivePersonName = ReceivePersonName;
        this.ReceivePersonNumber = ReceivePersonNumber;
        this.DeliveryTime = DeliveryDate;
        this.Side = Side;
        this.Weight = Weight;
        this.PayMethod = PayMethod;
        this.Message = Message;
        this.PayTime = PayTime;
        this.Price = Price;
        this.State = State;
    }

    public Date getPayTime() {
        return PayTime;
    }

    public String getState() {
        return State;
    }

    public String getDepTerminal() {
        return DepTerminal;
    }

    public String getArrTerminal() {
        return ArrTerminal;
    }

    public Date getDeliveryTime() {
        return DeliveryTime;
    }
}
