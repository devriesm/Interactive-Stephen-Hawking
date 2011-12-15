package net.markdevries.istephenhawking.service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import net.markdevries.istephenhawking.db.DatabaseManager;
import net.markdevries.istephenhawking.utils.BadWords;
import net.markdevries.istephenhawking.utils.Log;
import net.markdevries.istephenhawking.utils.SharedPrefsUtil;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import com.techventus.server.voice.Voice;
import com.techventus.server.voice.datatypes.records.SMS;
import com.techventus.server.voice.datatypes.records.SMSThread;

public class SMSService extends Service
{
	private static final String TAG = SMSService.class.getSimpleName();
	private static final long SMS_DELAY = 300000L; //5 minutes
	private static final long TTS_DELAY = 60000L; //1 minute
	public static final String ACTION_UPDATED_SMS = "ACTION_UPDATED_SMS";
	
	private DatabaseManager dbManager;
	private Voice voice;
	private Timer smsTimer;
	private Timer ttsTimer;
	private TextToSpeech tts;
	
	@Override
	public IBinder onBind( Intent intent )
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		dbManager = new DatabaseManager( this );
		voice = getVoice();
	}
	
	@Override
	public void onDestroy()
	{
		Log.e( TAG, "onDestroy" );
		super.onDestroy();
		
		if( dbManager != null )
			dbManager.destroy();
		dbManager = null;
		
		if( voice != null )
			voice = null;
		
		if( tts != null )
		{
			tts.setOnUtteranceCompletedListener( null );
			tts.shutdown();
		}
		tts = null;
		
		if( smsTimer != null )
		{
			smsTimer.cancel();
			smsTimer.purge();
			smsTimer = null;
			
		}
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if( smsTimer == null )
		{
			smsTimer = new Timer();
			smsTimer.schedule( smsTask, 0, SMS_DELAY );
		}
		if( ttsTimer == null )
		{
			ttsTimer = new Timer();
			ttsTimer.schedule( ttsTask, 0, TTS_DELAY );
		}
		return super.onStartCommand( intent, flags, startId );
	}
	
	public Voice getVoice()
	{
		if( voice == null )
		{
			String authToken = dbManager.getGoogleAuthToken();
			String phoneNumber = dbManager.getGooglePhoneNumber();
			
			if( authToken != "" && phoneNumber != "" )
			{
				voice = new Voice();
				voice.setAuthToken( authToken );
				voice.setPhoneNumber( phoneNumber );
			}
		}
		return voice;
	}
	
	private TimerTask smsTask = new TimerTask()
	{
		@Override
		public void run()
		{
			synchronized( this )
			{
				Thread t = new Thread( new Updater() );
				t.start();
			}
		}
	};
	
	private TimerTask ttsTask = new TimerTask()
	{
		@Override
		public void run()
		{
			Log.i( TAG, "ttsTask Run");
			if( !SharedPrefsUtil.getSpeechEnabled( getApplicationContext() ) )
			{
				return;
			}
			
			synchronized( this )
			{
				SMS sms = dbManager.getNextSMS();
				final String messageToSpeak = sms.getContent();
				
				dbManager.markSMSAsRead( sms );
				sendBroadcast( new Intent( ACTION_UPDATED_SMS ) );
				
				tts = new TextToSpeech( getApplicationContext(), new OnInitListener()
				{
					@Override
					public void onInit( int status )
					{
						if( tts == null ) return;
						
						tts.setOnUtteranceCompletedListener( new TextToSpeech.OnUtteranceCompletedListener()
						{
							@Override
							public void onUtteranceCompleted(String utteranceId)
							{
								tts.setOnUtteranceCompletedListener( null );
								tts.shutdown();
							}
						});
						HashMap<String, String> params = new HashMap<String, String>();
						params.put( TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId" ); //allows for onUtteranceComplete to be called
						tts.speak( messageToSpeak, TextToSpeech.QUEUE_FLUSH, params );
					}
				});
				Log.i( TAG, "ttsTask complete" );
			}
		}
	};
	
	
	class Updater implements Runnable
	{
		
		@Override
		public void run()
		{
			Log.i( TAG, "Service Running" );
			
			if( getVoice() != null && getVoice().isLoggedIn() )
			{
				try
				{
					Collection<SMSThread> smsThreads = getVoice().getSMSThreads();
					
					for( SMSThread thread : smsThreads )
					{
						Collection<SMS> smses = thread.getAllSMS();
						for( SMS sms : smses )
						{
							if( BadWords.hasBadWords( sms.getContent() ) )
							{
								dbManager.addSMSToBadTable( sms );
							}else
							{
								if( sms.getFrom().getNumber() != voice.getPhoneNumber() )
									dbManager.addSMS( sms );
							}
						}
					}
					smsThreads.clear();
					smsThreads = null;
				} catch (IOException e)
				{
					Log.e( TAG, e.getMessage() );
					e.printStackTrace();
				}
			}
			sendBroadcast( new Intent( ACTION_UPDATED_SMS ) );
			Log.i( TAG, "Service Complete" );
		}
	}
	
	public static Thread backgroundThread( final Runnable runnable )
	{
		final Thread t = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					runnable.run();
				} finally
				{
					
				}
			}
		};
		t.start();
		return t;
	}
}
