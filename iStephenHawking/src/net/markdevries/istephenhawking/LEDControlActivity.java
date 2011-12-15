package net.markdevries.istephenhawking;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import at.abraxas.amarino.Amarino;

public class LEDControlActivity extends Activity
{
	private static final String DEVICE_ID = "00:06:66:07:B0:E1";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.led_control );
		initViews();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		Amarino.connect( this, DEVICE_ID );
	}
	
	
	@Override
	protected void onStop()
	{
		super.onStop();
		Amarino.disconnect( this, DEVICE_ID );
	}
	
	private void initViews()
	{
		findViewById( R.id.led_normal ).setOnClickListener( buttonClickListener );
		findViewById( R.id.led_party ).setOnClickListener( buttonClickListener );
		findViewById( R.id.led_strobe ).setOnClickListener( buttonClickListener );
		findViewById( R.id.led_applause ).setOnClickListener( buttonClickListener );
		findViewById( R.id.led_boo ).setOnClickListener( buttonClickListener );
	}
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch( v.getId() )
			{
				case R.id.led_normal:
					Amarino.sendDataToArduino( LEDControlActivity.this, DEVICE_ID, 'A', 0 );
					break;
				case R.id.led_party:
					Amarino.sendDataToArduino( LEDControlActivity.this, DEVICE_ID, 'A', 1 );
					break;
				case R.id.led_strobe:
					Amarino.sendDataToArduino( LEDControlActivity.this, DEVICE_ID, 'A', 2 );
					break;
				case R.id.led_applause:
					Amarino.sendDataToArduino( LEDControlActivity.this, DEVICE_ID, 'A', 3 );
					break;
				case R.id.led_boo:
					Amarino.sendDataToArduino( LEDControlActivity.this, DEVICE_ID, 'A', 4 );
					break;
				default:
					break;
			}
		}
	};
}
