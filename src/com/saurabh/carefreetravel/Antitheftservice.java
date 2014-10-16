
package com.saurabh.carefreetravel;



import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class Antitheftservice extends IntentService implements SensorEventListener {
private boolean check;
private short ac_time;
private boolean vibrate;
private String tone;
private String button;
private final int mId=0;
private SensorManager mSensorManager;
private Sensor mProximity;
private SharedPreferences settings;
private PowerManager pm;
private Context context;
private static boolean proxy;
ComponentName name;
private NotificationCompat.Builder mBuilder;
AudioManager am;
private IntentFilter filter;
private RemoteReceiver receiver;
//private PowerManager.WakeLock lock;
private MediaPlayer blank;
	public Antitheftservice() 
	 {
	super("Anti Theft Service");
	}
	@Override
		public void onCreate() {
			check=true;
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		    mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		    context = getApplicationContext();
		    name=new ComponentName(getApplicationContext(),RemoteReceiver.class);
		    am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			pm= (PowerManager) this.getSystemService(Context.POWER_SERVICE);
	//		lock= pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "antitheftwakelock");
			settings= PreferenceManager.getDefaultSharedPreferences(this);
			ac_time= Short.parseShort(settings.getString("ac_time_out", "60"));
			vibrate= settings.getBoolean("vibrate", false);
			tone= settings.getString("tone", "siren1");
			button=settings.getString("button", "vol");
			filter= new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
			filter.addAction("android.intent.action.PHONE_STATE");
			switch(button)
			{
			case "power": filter.addAction("android.intent.action.SCREEN_ON");
			              break;
			case "cam"  : filter.addAction("android.intent.action.CAMERA_BUTTON");
			              break;
			}
			receiver = new RemoteReceiver();
			mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_stat_notify)
			        .setContentTitle(getText(R.string.app_name))
			        .setContentText(getText(R.string.notif_content))
			        .setTicker(getText(R.string.notif_ticker))
			        .setOngoing(true)
			        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_stat_notify_large));
			// Creates an explicit intent for an Activity in app
			Intent resultIntent = new Intent(this, MainActivity.class);

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(mId, mBuilder.build());
		//	lock.acquire();
			super.onCreate();
		}


	@SuppressWarnings("deprecation")
	protected void onHandleIntent(Intent intent) {	
		blank = MediaPlayer.create(this, R.raw.blank);
		blank.setLooping(true);
	while(check){
		try{
		switch(ac_time){
		case 15 : Thread.sleep(15000);
			      break;
		case 30:  Thread.sleep(30000);
		          break;
		case 60 : Thread.sleep(60000);
		          break;
		case 120: Thread.sleep(120000);
		          break;
		case 300: Thread.sleep(300000);
                  break;          
                 }
		}
		catch (InterruptedException e) {
            e.printStackTrace();
            }
		if(!pm.isScreenOn()){
			Log.d("service1", "Screen is now off!");
			blank.start();
	    break;
	    }
	}
	if(check)
	{mSensorManager.registerListener(Antitheftservice.this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
	this.registerReceiver(receiver, filter);
	}
	while(check){
		Log.d("service1", "Maximum Value: " +mProximity.getMaximumRange());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	}

		

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
		
		    
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		blank.stop();
		//lock.release();
		check=false;
		NotificationManager mNotificationManager =
		        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		    // Because the ID remains unchanged, the existing notification is updated.
		    mNotificationManager.cancel(mId);
		mSensorManager.unregisterListener(this);
		this.unregisterReceiver(receiver);
		SharedPreferences.Editor editor= settings.edit();
		editor.putBoolean("start", false);
		editor.commit();
	}
	
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		proxy=true;
		if(event.values[0]<mProximity.getMaximumRange())
			{proxy=false;
			Log.d("service1", "Proxy check here1:"+proxy);
			Log.d("service1", "Current low value is:" +event.values[0] );
			}
		 if(event.values[0]>=mProximity.getMaximumRange()){
			 Log.d("service1", "Current high value is:" +event.values[0] );
			 new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Log.d("service1", "Proxy check here alarm:"+proxy);
					if(proxy)
					 {
					switch(tone)
					{
					case "siren2" :Alertplayer(R.raw.siren2);
		                           break;
					case "siren3" :Alertplayer(R.raw.siren3);
		                           break;
					default :      Alertplayer(R.raw.siren1); 
		                           break;
					}
					if(vibrate)
					{
					Vibrator v= (Vibrator) getSystemService(context.VIBRATOR_SERVICE);
					v.vibrate(2000);
					}
					 }
					
				}
			},1000);		 
		
			}
	}
	private void Alertplayer(int a)
	{AudioManager mAudioManager = (AudioManager) this.context.getSystemService(AUDIO_SERVICE);
		MediaPlayer player= MediaPlayer.create(this, a);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), 0);
        player.start();
		}
}
