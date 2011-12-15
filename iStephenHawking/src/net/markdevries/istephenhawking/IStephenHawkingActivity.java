package net.markdevries.istephenhawking;

import net.markdevries.istephenhawking.utils.SharedPrefsUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class IStephenHawkingActivity extends Activity
{
	private static final String TAG = IStephenHawkingActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.stephen_main );
		initViews();
	}
	
	
	private void initViews()
	{
		findViewById( R.id.sms_list_btn ).setOnClickListener( buttonClickListener );
		findViewById( R.id.led_control_btn ).setOnClickListener( buttonClickListener );
		findViewById( R.id.predetermined_words_btn ).setOnClickListener( buttonClickListener );
		findViewById( R.id.custom_speak_btn ).setOnClickListener( buttonClickListener );
		
		final ToggleButton enableSpeechBtn = (ToggleButton) findViewById( R.id.toggle_background_speak );
		enableSpeechBtn.setChecked( SharedPrefsUtil.getSpeechEnabled( getApplicationContext() ) );
		enableSpeechBtn.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SharedPrefsUtil.setSpeechEnabled( IStephenHawkingActivity.this, enableSpeechBtn.isChecked() );
			}
		});
	}
	
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch( v.getId() )
			{
				case R.id.sms_list_btn:
					startActivity( new Intent( getBaseContext(), SMSListActivity.class ) );
					break;
				case R.id.led_control_btn:
					startActivity( new Intent( getBaseContext(), LEDControlActivity.class ) );
					break;
				case R.id.predetermined_words_btn:
					startActivity( new Intent( getBaseContext(), PredeterminedWordsActivity.class ) );
					break;
				case R.id.custom_speak_btn:
					startActivity( new Intent( getBaseContext(), CustomSpeakActivity.class ) );
					break;
			}
		}
	};
	
}
