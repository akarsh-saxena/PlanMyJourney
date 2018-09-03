package com.application.recommend.recommendplaces;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class Time extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    TextToSpeech tts;
    FirebaseAuth auth;
   // TimePickerDialog dialog;
    TextView tv1,tv2;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tts=new TextToSpeech(this,this);
        auth=FirebaseAuth.getInstance();
        tv1=findViewById(R.id.tvFrom);
        tv2=findViewById(R.id.tvTo);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void onInit(int i) {
        if(i==TextToSpeech.SUCCESS)
        {
            tts.speak("Enter the time",TextToSpeech.QUEUE_FLUSH,null);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
           // startActivity(new Intent(this,Main3Activity.class));
            return true;
        }

        if (id == R.id.action_cab) {
           // startActivity(new Intent(this,Main3Activity.class));
            return true;
        }

        if (id == R.id.action_sign_out) {
            // startActivity(new Intent(this,Main3Activity.class));
            auth.signOut();
            Intent intent = new Intent(this,
                    SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,Info.class));
        finish();
    }

    @Override
    public void onClick(View view) {
          TimePickerDialog dialog;
        if(view==tv1)
        {
            Calendar c=Calendar.getInstance();
            int hour=c.get(Calendar.HOUR);
            int min=c.get(Calendar.MINUTE);
            dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    tv1.setText(i +": "+i1);
                }
            },hour,min,true);

            dialog.show();
        }
        else
        if(view==tv2)
        {
            Calendar c=Calendar.getInstance();
            int hour=c.get(Calendar.HOUR);
            int min=c.get(Calendar.MINUTE);
            dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {

                    String str=tv1.getText().toString();
                    String [] timepart=str.split(":");
                    String hour=timepart[0];
                    String min=timepart[1];
                    min=min.replace(" ","");
                     if(Integer.parseInt(hour)>i)
                     {
                         Toast.makeText(Time.this, "The destination time should be larger than the the source time", Toast.LENGTH_SHORT).show();
                     }
                     else if(Integer.parseInt(hour)==i)
                     {
                         if(Integer.parseInt(min)>i1)
                             Toast.makeText(Time.this, "The destination time should be larger than the the source time", Toast.LENGTH_SHORT).show();
                     }

                        else
                         tv2.setText(i+":"+i1);
                }
            },hour,min,true);

            dialog.show();
        }
        else
            if(view==fab) {
                Intent intent = new Intent(Time.this, SourceDestActivity.class);
                startActivity(intent);
        }

    }
}
