package kr.ac.gachon.www.buslinker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DispatchAdapter extends BaseAdapter {
    private ArrayList<DispatchItem> dispatchItemArrayList=new ArrayList<>();

    public DispatchAdapter() {

    }

    @Override
    public int getCount() { //크기 반환
        return dispatchItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {   //객체 반환
        return dispatchItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos=position;
        final Context context=parent.getContext();

        if(convertView==null) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_dispatch, parent, false);
        }

        TextView depTimeTV=convertView.findViewById(R.id.depTimeTV);
        TextView arrTimeTV=convertView.findViewById(R.id.arrTimeTV);
        ImageView corpImage=convertView.findViewById(R.id.corpImage);

        DispatchItem dispatchItem=dispatchItemArrayList.get(position);

        depTimeTV.setText(dispatchItem.getDepTime());
        arrTimeTV.setText(dispatchItem.getArrTime());
        corpImage.setImageDrawable(dispatchItem.getCorpIcon());

        return convertView;
    }

    public void addItem(Drawable corpImage, String arrTime, String depTime) {
        DispatchItem dispatchItem=new DispatchItem();
        dispatchItem.setCorpIcon(corpImage);
        dispatchItem.setArrTime(arrTime);
        dispatchItem.setDepTime(depTime);
        dispatchItemArrayList.add(dispatchItem);
    }
}
