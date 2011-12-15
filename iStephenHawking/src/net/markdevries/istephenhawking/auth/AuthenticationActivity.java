package net.markdevries.istephenhawking.auth;

import java.io.IOException;

import net.markdevries.istephenhawking.R;
import net.markdevries.istephenhawking.db.DatabaseManager;
import net.markdevries.istephenhawking.utils.Validation;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techventus.server.voice.Voice;

public class AuthenticationActivity extends Activity implements Runnable
{
	public static final int AUTHENTICATED = 2323;
	
	private static final String TAG = "AuthenticationActivity";
	
	private Button okayBtn, cancelBtn;
	private EditText emailText, passwordText;
	private ProgressDialog dialog;
	
	private DatabaseManager dbManager;
	
	private boolean isLoggedIn = false;
	private String authToken = null;
	private String phoneNumber = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.authentication );
		dbManager = new DatabaseManager( this );
		initViews();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbManager.destroy();
		dbManager = null;
		if( dialog != null )
			dialog.dismiss();
		dialog = null;
		okayBtn.setOnClickListener( null );
		cancelBtn.setOnClickListener( null );
		okayBtn = null;
		cancelBtn = null;
		emailText = null;
		passwordText = null;
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}
	
	
	private void initViews()
	{
		okayBtn = (Button) findViewById( R.auth.okay_btn );
		cancelBtn = (Button) findViewById( R.auth.cancel_btn );
		emailText = (EditText) findViewById( R.auth.username );
		passwordText = (EditText) findViewById( R.auth.password );
		
		okayBtn.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				doLogin();
			}
		});
		cancelBtn.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	
	private void doLogin()
	{
		
		if( Validation.isValidEmailAddress( emailText.getText().toString() ) )
		{
		
			dialog = new ProgressDialog( this );
			dialog.setCancelable( false );
			dialog.setMessage( getString( R.string.authenticating_copy ) );
			dialog.show();
			
			Thread thread = new Thread(this);
			thread.start();
		}else
		{
			Toast.makeText( this.getBaseContext(), getString( R.string.not_valid_email ), Toast.LENGTH_SHORT ).show();
		}
	}
	
	
	private void handleLoginStatus()
	{
		if( isLoggedIn && authToken != null && phoneNumber != null )
		{
			long result = dbManager.updateGoogleAuth( authToken, phoneNumber );
			if( result > -1 )
			{
				this.setResult( RESULT_OK );
				finish();
			}else
			{
				Toast.makeText( this, getString( R.string.failed_login ), Toast.LENGTH_LONG ).show();
			}
		}else
		{
			Toast.makeText( this, getString( R.string.failed_login ), Toast.LENGTH_LONG ).show();
		}
	}
	
	
	@Override
	public void run()
	{
		try {
			Voice voice = new Voice( emailText.getText().toString(), passwordText.getText().toString() );
			isLoggedIn = voice.isLoggedIn();
			authToken = voice.getAuthToken();
			phoneNumber = voice.getPhoneNumber();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		mHandler.sendMessage(msg);
	}
	
	
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if( dialog != null )
				dialog.dismiss();
			handleLoginStatus();
		}
	};
}
