package com.saurabh.carefreetravel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RemoteReceiver extends BroadcastReceiver {
	 public void onReceive(Context context, Intent intent) {
		 
		 Log.d("service1", "Broadcast recieved");
		 context.stopService(new Intent(context, Antitheftservice.class));      
	  }

}
