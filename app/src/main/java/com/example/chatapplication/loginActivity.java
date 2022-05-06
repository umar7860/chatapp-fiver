package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class loginActivity extends AppCompatActivity {
    TextInputEditText username;
    TextInputEditText password;
    DataBaseHandler dataBaseHandler;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (TextInputEditText) findViewById(R.id.username1);
        password = (TextInputEditText) findViewById(R.id.password1);
        signup = (Button)findViewById(R.id.signup);
        dataBaseHandler = new DataBaseHandler(this);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),signUp.class);
                startActivity(i);
            }
        });
    }



    public void login(View view) {
        Boolean s = false;
        if (isValid()) {

            s = dataBaseHandler.isLogged(username.getText().toString(), password.getText().toString());
            if (s) {
                Log.e("ID", String.valueOf(DataBaseHandler.logged_username));
                Toast.makeText(this, "Logging Successfully", Toast.LENGTH_SHORT).show();
                //onDestroy();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
            if (!s) {
                password.setText("");
                password.setError("Wrong password");
                password.requestFocus();
            }
        }

    }


//        if (dataBaseHandler.isLogged(username.getText().toString(),password.getText().toString())){
//            Toast.makeText(this,"Logged ",Toast.LENGTH_LONG).show();
//
//        }
//        else
//        {
//            password.setText("");
//            password.setError("Wrong password");
//            password.requestFocus();
//        }


    private Boolean isValid() {
        Boolean ch = true;
        if (username.getText().toString().trim().length() <= 0) {
            username.requestFocus();
            username.setError("Field is empty");
            ch = false;
        } else if (password.getText().toString().trim().length() <= 0) {
            password.requestFocus();
            password.setError("Field is empty");
            ch = false;
        } else {
            ch = true;
        }
        return ch;
    }
}
