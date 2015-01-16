package com.thetorine.android.samaritan;

import com.thetorine.samaritan.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferencesActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	public static class SettingsFragment extends PreferenceFragment{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}
