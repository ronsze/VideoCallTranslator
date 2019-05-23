package com.uswit.videocalltranslate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CallRecordAdapter extends BaseAdapter {
    ArrayList<CallRecordContent> items =  new ArrayList<>();
    private int cnt;

    CallRecordAdapter(int _cnt){
        this.cnt = _cnt;
    }

    @Override
    public int getCount() {
        return cnt;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomHolder holder = null;
        TextView textView = null;

        if(convertView == null) {
            LayoutInflater inflater_L = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater_L.inflate(R.layout.faivorit_callrecord_layout, parent, false);

            textView = convertView.findViewById(R.id.itemTextView);

            holder = new CustomHolder();
            holder.m_TextView = textView;
        }else{
            holder = (CustomHolder)convertView.getTag();
            textView = holder.m_TextView;
        }

        textView.setText(items.get(position).roomNum);

        return convertView;
    }

    class CallRecordContent{
        private int roomNum;

        CallRecordContent(int _roomNum){
            roomNum = _roomNum;
        }
    }

    private class CustomHolder {
        TextView m_TextView;
    }

    public void changeCnt(int num){
        this.cnt = num;
    }
}
