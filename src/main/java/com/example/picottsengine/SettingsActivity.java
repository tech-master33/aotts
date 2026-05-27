package com.example.picottsengine;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setText("Pico TTS Engine Settings\n\nSupported Languages:\n" +
                "• English (US)\n" +
                "• English (GB)\n" +
                "• German\n" +
                "• Spanish\n" +
                "• French\n" +
                "• Italian");
        tv.setPadding(16, 16, 16, 16);
        
        setContentView(tv);
    }
}