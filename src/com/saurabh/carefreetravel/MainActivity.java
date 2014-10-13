package 
com.saurabh.carefreetravel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;



public class MainActivity extends PreferenceActivity {
	static private boolean a;                          //for first start
	static private boolean sp;                        //for checkbox

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("start", false);
		addPreferencesFromResource(R.xml.prefs);
		SharedPreferences firststart = PreferenceManager.getDefaultSharedPreferences(this);
		a = firststart.getBoolean("first", true);
		if(a==true)
		Toast.makeText(this, R.string.first_start,Toast.LENGTH_LONG).show();
		
		bindPreferenceSummaryToValue(findPreference("ac_time_out"));
		bindPreferenceSummaryToValue(findPreference("tone"));
		bindPreferenceSummaryToValue(findPreference("button"));
		/*
		 * code for first start info
		 */
		if(a==true)
		{
		SharedPreferences.Editor editor= firststart.edit();
		editor.putBoolean("first", false);
		editor.commit();
		}
			 final Preference service= findPreference("start");
		
		service.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent= new Intent(MainActivity.this,Antitheftservice.class);
				if(sp==false)
				{ Log.d("service1", "Startservice invoked");
					startService(intent);
				sp=true;
				}
				else if(sp==true)
					{ 
					Log.d("service1", "Stopservice invoked");
					stopService(intent);
					sp=false;
					}
				return false;
			}
		});
		Preference button =findPreference("button");
		button.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Toast.makeText(MainActivity.this,R.string.button_warning,Toast.LENGTH_LONG).show();
				return false;
			}
		});
		}
	
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();
			
			
			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone
								.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} 
		
			return true;
		}
	}; 
}