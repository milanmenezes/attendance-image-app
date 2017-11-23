package com.androidsrc.server;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Courses extends Activity {
    JSONObject cou= new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        RequestQueue queue = Volley.newRequestQueue(this);
        final RequestQueue queue1 = Volley.newRequestQueue(this);
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final LinearLayout llayout = (LinearLayout) findViewById(R.id.lview);
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String tid=sharedpreferences.getString("id",null);
        String url="http://app.automated-attendance.tk/teacher-courses/"+tid+"/";
        final String url1="http://app.automated-attendance.tk/total-count/";

        Button webviewbutton = (Button) findViewById(R.id.webviewbutton);

        webviewbutton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View arg0) {
                                                 Intent inent = new Intent(getApplicationContext(), TeacherWebView.class);
                                                 startActivity(inent);
                                             }
                                         });


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//										mTextView.setText("Response is: "+ response.substring(0,500));
                        Log.d("Log: ",response.toString());
//                        Toast.makeText(getApplicationContext() , response.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json= new JSONObject(response);
                            int len=json.getInt("len");
                            JSONObject data=json.getJSONObject("data");
                            cou=data;
                            for(int i=1;i<=len;i++){
                                JSONObject row= data.getJSONObject("id"+i);
                                final String cid=row.getString("cid");
                                Button button=new Button(Courses.this);
                                button.setLayoutParams(lparams);
                                button.setText(row.getString("cname"));
                                button.setId(i);

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        Intent inent = new Intent(getApplicationContext(), MainActivity.class);
                                        inent.putExtra("courses",cid);

//
//
                                        // Request a string response from the provided URL.
                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1+cid,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // Display the first 500 characters of the response string.
//                                                        mTextView.setText("Response is: "+ response.substring(0,500));
                                                        Toast.makeText(getApplicationContext() , "Started attendance", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
//                                                mTextView.setText("That didn't work!");
                                                Toast.makeText(getApplicationContext() , "Ierror", Toast.LENGTH_SHORT).show();
                                            }
                                        });
// Add the request to the RequestQueue.
                                        queue1.add(stringRequest);


//
//
                                        startActivity(inent);

                                                              }
                                                          });
                                llayout.addView(button);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext() , "JError", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//								mTextView.setText("That didn't work!");
//                vstatus="Error";
//                r="Database Connection Error";
//                stoast(url);
                Toast.makeText(getApplicationContext() , "Error", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


//        Intent inent = new Intent(getApplicationContext(), MainActivity.class);
//        inent.putExtra("courses","cs0401");
//        startActivity(inent);
    }
}
