package com.androidsrc.server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Tlogin extends Activity {
    String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlogin);
        final EditText id=(EditText) findViewById(R.id.idvalue);
        final EditText password=(EditText) findViewById(R.id.passwordvalue);
        Button button=(Button) findViewById(R.id.submit);
        final RequestQueue queue = Volley.newRequestQueue(this);


//        final String url="http://192.168.0.105:5000/teacher-login/"+id.toString()+"/"+password.getText();
        Log.d("log: ",id.toString());

        // Request a string response from the provided URL.
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                final String url="http://app.automated-attendance.tk/teacher-login/"+id.getText()+"/"+password.getText();
                final String url="http://192.168.0.2:5000/teacher-login/"+id.getText()+"/"+password.getText();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
//										mTextView.setText("Response is: "+ response.substring(0,500));
                                status = response;
                                if (status.equals("OK")) {
                                    Toast.makeText(Tlogin.this, "Logged in", Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedpreferences;
                                    sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("id", id.getText().toString());
                                    editor.commit();
                                    Intent inent = new Intent(getApplicationContext(), Courses.class);
                                    startActivity(inent);
                                } else {
                                    Toast.makeText(Tlogin.this, status.toString(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//								mTextView.setText("That didn't work!");
                        Toast.makeText(Tlogin.this, "No Internet", Toast.LENGTH_SHORT).show();

                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
    }
}
