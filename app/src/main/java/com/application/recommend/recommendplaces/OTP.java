package com.application.recommend.recommendplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OTP extends AppCompatActivity implements View.OnClickListener {

    EditText t1;
    Button b1;
    String otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        otp=getIntent().getExtras().getString("otp");
        t1=findViewById(R.id.eotp);
        t1.setText(otp);
        b1=findViewById(R.id.bok);
        b1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(otp.contentEquals(t1.getText().toString())){
            {
                Toast.makeText(this, "You are a valid user!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,MainActivity.class));
            }
        }
        else
        {  Toast.makeText(this, "Wrong OTP!!!", Toast.LENGTH_SHORT).show();
           t1.setText("");

    }}
}
