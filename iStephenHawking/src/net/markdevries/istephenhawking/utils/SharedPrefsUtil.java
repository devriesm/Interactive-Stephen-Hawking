package net.markdevries.istephenhawking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefsUtil
{
	private static final String PREF_NAME = "IStephenHawking";
	private static final String FIRST_RUN = "first_run";
	private static final String SPEECH_ENABLED = "speech_enabled";
	
	public static SharedPreferences getSharedPrefs( Context c )
	{
		return c.getSharedPreferences( PREF_NAME, Context.MODE_PRIVATE );
	}
	
	
	public static Editor getSharedPrefsEditor( Context c )
	{
		return getSharedPrefs( c ).edit();
	}
	
	
	public static void setFirstRunComplete( Context c )
	{
		Editor edit = getSharedPrefsEditor( c );
		edit.putBoolean( FIRST_RUN, false );
		edit.commit();
	}
	
	
	public static boolean getIsFirstRun( Context c, String name )
	{
		SharedPreferences prefs = getSharedPrefs( c );
		return prefs.getBoolean( FIRST_RUN, true );
	}
	
	
	public static void setSpeechEnabled( Context c, boolean enabled )
	{
		Editor edit = getSharedPrefsEditor( c );
		edit.putBoolean( SPEECH_ENABLED, enabled );
		edit.commit();
	}
	
	
	public static boolean getSpeechEnabled( Context c )
	{
		SharedPreferences prefs = getSharedPrefs( c );
		return prefs.getBoolean( SPEECH_ENABLED, false );
	}
}
