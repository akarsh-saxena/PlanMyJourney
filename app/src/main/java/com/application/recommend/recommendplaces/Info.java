package com.application.recommend.recommendplaces;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Info extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    EditText t1,t2;
    ImageButton img1,img2;
    TextToSpeech tts;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        t1=findViewById(R.id.etsource);
        t2=findViewById(R.id.etdest);
        img1=findViewById(R.id.imageButton);
        img2=findViewById(R.id.imageButton2);
        tv=findViewById(R.id.tvTime);
        tv.setOnClickListener(this);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        tts=new TextToSpeech(this,this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);


        finish();
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
    public void onClick(View view) {
        if(view==img1)
        {
            Intent in= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            in.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say");
            startActivityForResult(in,1234);

        }

        if(view==img2)
        {
            Intent in= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            in.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say");
            startActivityForResult(in,123);

        }

        if(view==tv)
        {
            startActivity(new Intent(this,Time.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234 && resultCode==RESULT_OK)
        {
            ArrayList<String> array =data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            t1.setText(array.get(0));
            t2.requestFocus();
            tts.speak("Fill the destination",TextToSpeech.QUEUE_FLUSH,null);

        }
        if(requestCode == 123 && resultCode==RESULT_OK)
        {
            ArrayList<String> array =data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            t2.setText(array.get(0));

        }

    }

    @Override
    public void onInit(int i) {

        /*if(i==TextToSpeech.SUCCESS)
            Toast.makeText(this, "Initialised....", Toast.LENGTH_SHORT).show();*/
        //tts.speak("Intialised",TextToSpeech.QUEUE_FLUSH,null);
        String a=t1.getText().toString();
        String b=t2.getText().toString();
        if(a.length()==0 || b.length()==0)
            tts.speak("Fill the source and destination",TextToSpeech.QUEUE_FLUSH,null);
        if(i==TextToSpeech.ERROR)
            Toast.makeText(this, "Error Intialising..", Toast.LENGTH_SHORT).show();
        // tts.speak("Error in Initialising",TextToSpeech.QUEUE_FLUSH,null);
        if(i==TextToSpeech.STOPPED)
            Toast.makeText(this, "Stopped...", Toast.LENGTH_SHORT).show();
    }
}
