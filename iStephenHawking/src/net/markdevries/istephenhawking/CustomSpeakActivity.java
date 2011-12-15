package net.markdevries.istephenhawking;

import java.util.HashMap;

import net.markdevries.istephenhawking.utils.SharedPrefsUtil;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CustomSpeakActivity extends Activity
{
	private TextToSpeech tts;
	private boolean enabledSpeechPref = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.custom_speak );
		initViews();
	}
	
	private void initViews()
	{
		findViewById( R.id.send_btn ).setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EditText speakTxt = (EditText) findViewById( R.id.custom_speak_txt );
				final Button sendButton = (Button) v;
				sendButton.setEnabled( false );
				final String message = speakTxt.getText().toString();
				tts = new TextToSpeech( CustomSpeakActivity.this, new OnInitListener()
				{
					@Override
					public void onInit(int status)
					{
						tts.setOnUtteranceCompletedListener( new OnUtteranceCompletedListener()
						{
							@Override
							public void onUtteranceCompleted(String utteranceId)
							{
								runOnUiThread( new Runnable()
								{
									@Override
									public void run()
									{
										tts.setOnUtteranceCompletedListener( null );
										tts.shutdown();
										sendButton.setEnabled( true );
									}
								});
								
							}
						});
						HashMap<String, String> params = new HashMap<String, String>();
						params.put( TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId" ); //allows for onUtteranceComplete to be called

						tts.speak( message, TextToSpeech.QUEUE_FLUSH, params );
					}
				});
				
				speakTxt.setText( "" );
			}
		});
	}
	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		SharedPrefsUtil.setSpeechEnabled( getApplicationContext(), enabledSpeechPref );
	}
	

	@Override
	protected void onResume()
	{
		super.onResume();
		//disable background speech while we have this activity open
		enabledSpeechPref = SharedPrefsUtil.getSpeechEnabled( getApplicationContext() );
		SharedPrefsUtil.setSpeechEnabled( getApplicationContext(), false );
	}
	
}
