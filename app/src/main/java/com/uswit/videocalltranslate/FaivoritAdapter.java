package com.uswit.videocalltranslate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FaivoritAdapter extends BaseAdapter {
    ArrayList<FaivoritContent> items = new ArrayList<>();
    private int itemCnt;
    private Context context;

    FaivoritAdapter(int _cnt, Context _context) {
        this.itemCnt = _cnt;
        this.context = _context;
    }

    public void add(String roomName) {
        items.add(new FaivoritContent(roomName));
    }

    public void remove(int position) {
        items.remove(position);
    }

    @Override
    public int getCount() {                     //List에 표시할 갯수 반환
        if(items.size() <= itemCnt){
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


    public int getItemCnt() {
        return items.size();
    }

    public String getName(int position) {
        return items.get(position).roomName;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        final int pos = position;

        LayoutInflater inflater_L = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater_L.inflate(R.layout.faivorit_callrecord_layout, parent, false);

        textView = convertView.findViewById(R.id.itemTextView);

        textView.setText(items.get(pos).reName);

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                choiceDialog(pos);
                return false;
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)MainActivity.context).adapterCall(items.get(pos).roomName);
                ((MainActivity)MainActivity.context).updateAdapter();
            }
        });

        return convertView;
    }

    public void changeCnt(int cnt) {     //리스트에 표시할 갯수 조정
        this.itemCnt = cnt;
    }

    public void addCallCnt(int position) {
        items.get(position).callCnt += 1;

        sortItems();
    }

    private void choiceDialog(int position) {
        final ArrayList<String> dialogList = new ArrayList<>();
        dialogList.add("이름 변경");
        dialogList.add("삭제");

        final CharSequence[] dialogitems = dialogList.toArray(new String[dialogList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                            items.get(position).reName = edittext.getText().toString();
                            ((MainActivity)MainActivity.context).updateAdapter();
                        } else {
                            Toast.makeText(context, "이름을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.show();
    }

    private void sortItems() {
        FaivoritContent tmp;

        for (int i = 0; i < items.size(); i++) {
            for (int j = i; j < items.size(); j++) {
                if (items.get(i).callCnt < items.get(j).callCnt) {
                    tmp = items.get(i);
                    items.set(i, items.get(j));
                    items.set(j, tmp);

                }
            }
        }
    }

    class FaivoritContent {              //items ArrayList
        private String roomName;
        private int callCnt;
        private String reName;

        FaivoritContent(String _roomName) {
            roomName = _roomName;
            callCnt = 0;
            reName = roomName;
        }
    }
}
