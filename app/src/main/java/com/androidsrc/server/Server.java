package com.androidsrc.server;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Server {
	MainActivity activity;
	RequestQueue queue;
	ServerSocket serverSocket;
	String message = "";
	ArrayList<String> L1= new ArrayList<String>();;
	static final int socketServerPORT = 8080;



	public Server(MainActivity activity,RequestQueue queue) {
		this.activity = activity;
		this.queue=queue;
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}

	public int getPort() {
		return socketServerPORT;
	}

	public void onDestroy() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private class SocketServerThread extends Thread {

		int count = 0;
		String r="Test";
		String url ="http://app.automated-attendance.tk/attendance/";
		String vstatus=new String();

		@Override
		public void run() {

			try {
				serverSocket = new ServerSocket(socketServerPORT);


				while (true) {
					Socket socket = serverSocket.accept();
					count++;

					message += "#" + count + " from "
							+ socket.getInetAddress() + ":"
							+ socket.getPort() + "\n";
					final InetAddress address = socket.getInetAddress();
					final String mac=activity.getMacByIp(address.getHostAddress().toString());
					if(!L1.contains(mac)){
						L1.add(mac);
						r="OK";
						url=url+activity.course+"/";
						url=url+mac;

						// Request a string response from the provided URL.
						StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
								new Response.Listener<String>() {
									@Override
									public void onResponse(String response) {
										// Display the first 500 characters of the response string.
//										mTextView.setText("Response is: "+ response.substring(0,500));
										vstatus=response;
										activity.head.setText("Attendance Given");
										activity.stoast("Done");
									}
								}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
//								mTextView.setText("That didn't work!");
								vstatus="Error";
								r="Database Connection Error";
								activity.stoast(url);
								activity.head.setText(r);
							}
						});

						// Add the request to the RequestQueue.
						queue.add(stringRequest);


					}
					else{
						r="Attendance already given";
//						activity.head.setText(r);
					}




					activity.runOnUiThread(new Runnable() {


						@Override
						public void run() {
							activity.msg.setText(message);

							activity.stoast(mac);
						}
					});

					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
							socket, count, r);
					socketServerReplyThread.run();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		int cnt;
		String reply1;

		SocketServerReplyThread(Socket socket, int c, String rply) {
			hostThreadSocket = socket;
			cnt = c;
			reply1= rply;
		}

		@Override
		public void run() {
			OutputStream outputStream;
			String msgReply = reply1;

			try {
				outputStream = hostThreadSocket.getOutputStream();
				PrintStream printStream = new PrintStream(outputStream);
				printStream.print(msgReply);
				printStream.close();

				message += "replayed: " + msgReply + "\n";

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.msg.setText(message);
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					activity.msg.setText(message);
				}
			});
		}

	}

	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
							.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "Server running at : "
								+ inetAddress.getHostAddress();
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}
}
