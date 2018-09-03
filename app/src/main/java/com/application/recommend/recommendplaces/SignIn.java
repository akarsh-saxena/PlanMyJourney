package com.application.recommend.recommendplaces;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    EditText etuser, etpass;
    Button bsignin;
    String user, pass;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressDialog dialog;
    ImageView imageButton;
    boolean hidden=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        etuser = findViewById(R.id.etuser);
        etpass = findViewById(R.id.etpass);
        bsignin = findViewById(R.id.blogin);
        imageButton=findViewById(R.id.imageButton4);
       // user = getIntent().getExtras().getString("username");
        //pass = getIntent().getExtras().getString("password");
       // etuser.setText(user);
        //etpass.setText(pass);
        dialog=new ProgressDialog(SignIn.this);
        auth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        bsignin.setOnClickListener(this);
        imageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view ==bsignin) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to become an authenticated user?");
            builder.setTitle("Message!!!");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkUser();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    startActivity(new Intent(SignIn.this, MainActivity.class));
                }
            });
            builder.show();

        }

        if (view == imageButton) {
            if (hidden) {
                etpass.setTransformationMethod(null);
                imageButton.setImageResource(R.mipmap.green);
                hidden=false;
            }
            else {
                etpass.setTransformationMethod(new PasswordTransformationMethod());
                imageButton.setImageResource(R.mipmap.red);
                hidden=true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

        //startActivity(new Intent(this,MainActivity.class));
        finish();
    }


    private void checkUser() {

            dialog.setMessage("Logging...PLease Wait");
            dialog.show();
            String email=etuser.getText().toString();
            String pas=etpass.getText().toString();
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pas) )
                Toast.makeText(this, "All fields are Mandatory!!!", Toast.LENGTH_SHORT).show();
            else {
                auth.signInWithEmailAndPassword(email, pas).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(SignIn.this, "Successfully Login!!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this, Info.class));
                            etuser.setText("");
                            etpass.setText("");
                        }

                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(SignIn.this, "OOps...FAiled!!!", Toast.LENGTH_SHORT).show();
                        etuser.setText("");
                        etpass.setText("");
                    }
                });
            }
    }
}
