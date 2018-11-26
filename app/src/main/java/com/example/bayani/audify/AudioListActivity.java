package com.example.bayani.audify;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AudioListActivity extends AppCompatActivity implements View.OnClickListener{

    final String[] from = new String[] { DatabaseHelper._ID,
            DatabaseHelper.SUBJECT, DatabaseHelper.DESC };

    final int[] to = new int[] { R.id.id, R.id.title, R.id.desc };

    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE = null;
    Chronometer myChronometer;
    private Button startbtn,stopbtn,playbtn,stoprec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_emp_list);

        myChronometer = (Chronometer) findViewById(R.id.chronometer);
        startbtn = (Button) findViewById(R.id.startbtn);
        stopbtn = (Button) findViewById(R.id.stopbtn);
        playbtn = (Button) findViewById(R.id.playbtn);
        stoprec = (Button) findViewById(R.id.stoprec);

        startbtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        playbtn.setOnClickListener(this);
        stoprec.setOnClickListener(this);

        stopbtn.setEnabled(false);
        playbtn.setEnabled(false);
        stoprec.setEnabled(false);

        Date createdTime = new Date();
        OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/" + createdTime + "_rec.3gpp";

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

        // OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView idTextView = (TextView) view.findViewById(R.id.id);
                TextView titleTextView = (TextView) view.findViewById(R.id.title);
                TextView descTextView = (TextView) view.findViewById(R.id.desc);

                String id = idTextView.getText().toString();
                String title = titleTextView.getText().toString();
                String desc = descTextView.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), ModifyAudioActivity.class);
                modify_intent.putExtra("title", title);
                modify_intent.putExtra("desc", desc);
                modify_intent.putExtra("id", id);

                startActivity(modify_intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startbtn:
                try {
                    beginRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.stopbtn:
                try {
                    stopRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.playbtn:
                try {
                    playRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.stoprec:
                try {
                    stopPlayback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }


    private void beginRecording() throws IOException {
        startbtn.setEnabled(false);
        stopbtn.setEnabled(true);
        playbtn.setEnabled(false);
        stoprec.setEnabled(false);
        myChronometer.setBase(SystemClock.elapsedRealtime());
        myChronometer.start();
        ditchMediaRecorder();
        File outFile = new File(OUTPUT_FILE);

        if (outFile.exists()){
            outFile.delete();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setOutputFile(OUTPUT_FILE);
        recorder.prepare();
        recorder.start();
        Toast.makeText(AudioListActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();

    }

    private void ditchMediaRecorder() {
        if (recorder != null)
            recorder.release();
    }

    private void stopRecording() {
        if (recorder != null){
            startbtn.setEnabled(true);
            stopbtn.setEnabled(false);
            playbtn.setEnabled(true);
            myChronometer.stop();
            recorder.stop();
            Toast.makeText(AudioListActivity.this, "Recording Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void playRecording() throws IOException {
        stoprec.setEnabled(true);
        ditchMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(OUTPUT_FILE);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    private void ditchMediaPlayer() {
        if (mediaPlayer != null){
            try{
                mediaPlayer.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void stopPlayback() {
        if (mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_record) {

            Intent add_mem = new Intent(this, AddAudioActivity.class);
            startActivity(add_mem);

        }
        else if (id == R.id.help1){
            Intent add_mem = new Intent(this,Help.class);
            startActivity(add_mem);
        }
        return super.onOptionsItemSelected(item);
    }
}
