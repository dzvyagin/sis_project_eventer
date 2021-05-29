package com.zviagin_danila.eventer.ui.main_content.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.zviagin_danila.eventer.MainActivity;
import com.zviagin_danila.eventer.R;
import com.zviagin_danila.eventer.Utils;

public class EventActivity extends AppCompatActivity {

    MaterialButton responseButton;
    ImageButton imageButton;
    TextView title;
    TextView date;
    TextView description;
    TextView author;
    TextView number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        title = findViewById(R.id.event_title);
        date = findViewById(R.id.event_date);
        description = findViewById(R.id.event_description);
        number = findViewById(R.id.event_phone_number);


        responseButton = findViewById(R.id.send_message_button);
        responseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventActivity.this, MainActivity.class));
            }
        });

        Intent intent = getIntent();
        title.setText(intent.getStringExtra("title"));
        date.setText(Utils.getReadableDateTime(intent.getLongExtra("timeStart", 0),
                intent.getLongExtra("timeEnd", 0)));
        description.setText(intent.getStringExtra("description"));
        number.setText(intent.getStringExtra("number"));

        imageButton = findViewById(R.id.event_back_button);
        imageButton.setOnClickListener(v -> {
            Intent newIntent = new Intent(this, MainActivity.class);
            startActivity(newIntent);
        });


    }

}