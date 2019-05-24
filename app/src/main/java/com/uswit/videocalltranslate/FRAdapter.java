package com.uswit.videocalltranslate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FRAdapter extends BaseAdapter {
    private ArrayList<FRContent> items = new ArrayList<>();
    private ArrayList<FRContent> addItem = new ArrayList<>();

    private Context context;

    private int itemCnt;
    public int faivoriteCnt;

    FRAdapter(Context _context, int _cnt){
        this.context = _context;
        this.itemCnt = _cnt;
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    public void add(String roomId, int addNum){
        items.add(addNum, new FRContent(roomId));
    }

    public void remove(int position){
        if(items.get(position).isFaivorite){
            faivoriteCnt -= 1;
        }
        items.remove(position);
    }

    @Override
    public int getCount() {
        if(items.size() < itemCnt){
            return items.size();
        }else{
            return itemCnt;
        }
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemCnt(){
        return items.size();
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int POS = position;

        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.faivorite_callrecord_layout, parent, false);

        TextView fTextView  = (TextView)convertView.findViewById(R.id.itemTextView);
        Button addFaivoriteBtn = (Button)convertView.findViewById(R.id.addFaivorite);
        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.layout);

        items.get(position).layout = layout;
        items.get(position).addFaivoriteBtn = addFaivoriteBtn;

        if(items.get(position).isFaivorite){
            items.get(position).addFaivoriteBtn.setBackgroundResource(R.drawable.star_color);
        }else{
            items.get(position).addFaivoriteBtn.setBackgroundResource(R.drawable.star_non_color);
        }

        fTextView.setText(items.get(position).roomName);

        fTextView.setOnClickListener(new View.OnClickListener() {                //짧게클릭
            @Override
            public void onClick(View v) {
                ((MainActivity)MainActivity.context).adapterCall(items.get(position).roomId);
                ((MainActivity)MainActivity.context).updateAdapter();
            }
        });

        fTextView.setOnLongClickListener(new View.OnLongClickListener() {        //길게클릭
            @Override
            public boolean onLongClick(View v) {
                choiceDialog(position);
                return false;
            }
        });

        fTextView.setOnTouchListener(new View.OnTouchListener() {                //터치이벤트
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        items.get(position).layout.setBackgroundColor(0x60FFFFFF);
                        break;
                    case MotionEvent.ACTION_UP:
                        items.get(position).layout.setBackgroundColor(0x000000);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        items.get(position).layout.setBackgroundColor(0x000000);

                }
                return false;
            }
        });

        addFaivoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FRContent tmp = items.get(position);

                if(items.get(position).isFaivorite){
                    items.remove(position);
                    items.add(tmp);
                    ((MainActivity)MainActivity.context).updateAdapter();
                    items.get(position).addFaivoriteBtn.setBackgroundResource(R.drawable.star_non_color);
                    items.get(items.size()-1).changeIsFaivorite();
                    faivoriteCnt -= 1;
                }else{
                    items.remove(position);
                    items.add(0, tmp);
                    ((MainActivity)MainActivity.context).updateAdapter();
                    items.get(0).addFaivoriteBtn.setBackgroundResource(R.drawable.star_color);
                    items.get(0).changeIsFaivorite();
                    faivoriteCnt += 1;
                }


            }
        });

        return convertView;
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    class FRContent{
        private String roomId;
        private String roomName;
        public boolean isFaivorite;
        private LinearLayout layout;
        private Button addFaivoriteBtn;

        private FRContent(String _roomId){
            roomId = _roomId;
            roomName = roomId;
            isFaivorite= false;
        }

        void changeIsFaivorite(){
            if(isFaivorite) {
                isFaivorite = false;
            }else{
                isFaivorite = true;
            }
        }
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    private void choiceDialog(int position) {
        final ArrayList<String> dialogList = new ArrayList<>();
        dialogList.add("이름 변경");
        dialogList.add("삭제");

        final CharSequence[] dialogitems = dialogList.toArray(new String[dialogList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(items.get(position).roomName);
        builder.setItems(dialogitems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = dialogitems[pos].toString();
                if (selectedText.equals("이름 변경")) {
                    reNameDialog(position);
                } else if (selectedText.equals("삭제")) {
                    remove(position);
                    ((MainActivity)MainActivity.context).updateAdapter();
                }
            }
        });
        builder.show();
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    private void reNameDialog(int position) {
        final EditText edittext = new EditText(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("이름을 입력해주세요.");
        builder.setView(edittext);
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (edittext.getText().length() > 0 && edittext.getText() != null) {
                            items.get(position).roomName = edittext.getText().toString();
                            ((MainActivity)MainActivity.context).updateAdapter();
                        } else {
                            Toast.makeText(context, "이름을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.show();
    }

    void changeShowItems(){

    }

    public String getRoomId(int position){
        return items.get(position).roomId;
    }

    public void updateRecord(int position){
        FRContent tmp = items.get(position);
        if(items.get(position).isFaivorite){

        }else{
            items.remove(position);
            items.add(faivoriteCnt, tmp);
        }
    }

    public ArrayList getItemArray(){
        return items;
    }
}
