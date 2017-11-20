package com.androidsrc.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class MainActivity extends Activity {

	Server server;
	TextView infoip, msg,head;
	List<String> maclist;
	Intent intent = getIntent();
	String course = intent.getStringExtra("courses");


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoip = (TextView) findViewById(R.id.infoip);
		head = (TextView) findViewById(R.id.head);
		msg = (TextView) findViewById(R.id.msg);
		RequestQueue queue = Volley.newRequestQueue(this);
		server = new Server(this,queue);
		infoip.setText(server.getIpAddress()+":"+server.getPort());

		
	}

	public void stoast(String x){
		Toast.makeText(getApplicationContext() , x, Toast.LENGTH_SHORT).show();
	}

	public String getMacByIp(String x) {
		String flushCmd = "sh ip -s -s neigh flush all";
		Runtime runtime = Runtime.getRuntime();
		try
		{
			runtime.exec(flushCmd,null,new File("/proc/net"));
		}
		catch (Exception e)
		{}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" +");
				if (splitted != null ) {
					// Basic sanity check
					String mac = splitted[3];
					if (mac.matches("..:..:..:..:..:..")) {

                   /* ClientList.add("Client(" + macCount + ")");
                    IpAddr.add(splitted[0]);
                    HWAddr.add(splitted[3]);
                    Device.add(splitted[5]);*/
						System.out.println("Mac : "+ mac + " IP Address : "+splitted[0] );


//						Toast.makeText(
//								getApplicationContext(),
//								"Mac_Count  " + macCount + "   MAC_ADDRESS  "
//										+ mac, Toast.LENGTH_SHORT).show();
						if(splitted[0].equals(x)){
						return mac;
						}

					}
               /* for (int i = 0; i < splitted.length; i++)
                    System.out.println("Addressssssss     "+ splitted[i]);*/

				}
			}
		} catch(Exception e) {

		}
		return "NOT_FOUND";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		server.onDestroy();
	}

	
}