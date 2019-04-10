package kr.ac.gachon.www.buslinker;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class DispatchItem {
    private String depTime;
    private String arrTime;

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public String getArrTime() {
        return arrTime;
    }

    public void setArrTime(String arrTime) {
        this.arrTime = arrTime;
    }
}
