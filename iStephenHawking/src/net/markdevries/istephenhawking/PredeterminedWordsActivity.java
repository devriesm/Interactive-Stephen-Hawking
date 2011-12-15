package net.markdevries.istephenhawking;

import java.util.HashMap;

import net.markdevries.istephenhawking.utils.SharedPrefsUtil;

import android.app.ListActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PredeterminedWordsActivity extends ListActivity
{
	private TextToSpeech tts;
	private boolean enabledSpeechPref = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.predetermined_words_list_item, getResources().getStringArray( R.array.predetermined_words_array ) ) );
		
		ListView lv = getListView();
		lv.setTextFilterEnabled( true );
		lv.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				String[] items = getResources().getStringArray( R.array.predetermined_words_array );
				final String phrase = items[position];
				tts = new TextToSpeech( getApplicationContext(), new OnInitListener()
				{
					@Override
					public void onInit(int status)
					{
						tts.setOnUtteranceCompletedListener( new OnUtteranceCompletedListener()
						{
							@Override
							public void onUtteranceCompleted(String utteranceId) {
								tts.setOnUtteranceCompletedListener( null );
								tts.shutdown();
							}
						});
						HashMap<String, String> params = new HashMap<String, String>();
						params.put( TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId" ); //allows for onUtteranceComplete to be called
						tts.speak( phrase, TextToSpeech.QUEUE_FLUSH, params );
					}
				});
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
