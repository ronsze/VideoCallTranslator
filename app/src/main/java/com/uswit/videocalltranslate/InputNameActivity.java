package com.uswit.videocalltranslate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class InputNameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputname);

        final EditText editText = findViewById(R.id.input_name);
        editText.setFilters(new InputFilter[] {filter});

        findViewById(R.id.btn_complete).setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.name_empty, Toast.LENGTH_SHORT).show();
            }
            else {
                SharedPreferences prefs = getSharedPreferences("PrefSets", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", editText.getText().toString());
                editor.putBoolean("setbool", true);
                editor.apply();

                Intent intent = new Intent(InputNameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }

    protected InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
        if(!ps.matcher(source).matches()) {
            return "";
        }

        return null;
    };
}
