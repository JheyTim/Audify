package com.example.bayani.audify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Help extends Activity implements View.OnClickListener{

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Help");
        setContentView(R.layout.help);
        button = (Button)findViewById(R.id.done);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done:

                Intent main = new Intent(Help.this, AudioListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);;
                startActivity(main);
                break;
        }
    }
}
