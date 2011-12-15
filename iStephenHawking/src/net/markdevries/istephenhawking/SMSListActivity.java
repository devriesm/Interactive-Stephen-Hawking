package net.markdevries.istephenhawking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import net.markdevries.istephenhawking.auth.AuthenticationActivity;
import net.markdevries.istephenhawking.db.DatabaseManager;
import net.markdevries.istephenhawking.service.SMSService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.techventus.server.voice.datatypes.records.SMS;

public class SMSListActivity extends Activity
{
	public static final String TAG = SMSListActivity.class.getSimpleName();
	
	private ProgressDialog dialog;
	private ArrayList<SMS> smsItems = new ArrayList<SMS>();
	private ListView smsListView;
	private SMSListAdapter smsListAdapter;
	
	private DatabaseManager dbManager;
	private BroadcastReceiver mReceiver;
	
	private TextToSpeech tts;
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		Log.e( TAG, "onCreate" );
		super.onCreate( savedInstanceState );
		setContentView( R.layout.sms_list );
		
		dbManager = new DatabaseManager( this );
		
		smsListView = (ListView) findViewById( R.id.list_view );
		smsListAdapter = new SMSListAdapter();
		smsListView.setAdapter( smsListAdapter );
		smsListView.setOnItemLongClickListener( new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				final SMS sms = smsListAdapter.getItem( position );
				final String message = sms.getContent();
				//tts.speak( sms.getContent(), TextToSpeech.QUEUE_FLUSH, null );
				AlertDialog.Builder builder = new AlertDialog.Builder(SMSListActivity.this);
				final CharSequence[] items = {"Speak", "Mark As Read"};
				
				builder.setItems(items, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if( which == 0 )
						{
							tts = new TextToSpeech(getApplicationContext(), new OnInitListener()
							{
								@Override
								public void onInit(int status)
								{
									tts.setOnUtteranceCompletedListener( new OnUtteranceCompletedListener()
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
									tts.speak( message, TextToSpeech.QUEUE_FLUSH, params );
								}
							});
						}else if( which == 1 )
						{
							dbManager.markSMSAsRead( sms );
							initSMSListItems();
						}
						dialog.dismiss();
					}
				}).show();
				return false;
			}
		});
		startService( new Intent( this, SMSService.class ) );
    }
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		checkAuthToken();
	}
	
	
	@Override
	protected void onStart()
	{
		super.onStart();
		mReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive( Context context, Intent intent )
			{
				SMSListActivity.this.runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						initSMSListItems();
					}
				});
			}
		};
		registerReceiver( mReceiver, new IntentFilter( SMSService.ACTION_UPDATED_SMS ) );
	}
	
	
	@Override
	protected void onStop()
	{
		unregisterReceiver( mReceiver );
		if( tts != null )
			tts.shutdown();
		super.onStop();
	}
	
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy()
	{
		hideDialog();
		
		super.onDestroy();
		
		if( dbManager != null )
			dbManager.destroy();
		dbManager = null;
		
		if( smsListView != null )
			smsListView.setAdapter( null );
		smsListView = null;
		
		smsItems = null;
		smsListAdapter = null;
		if( smsItems != null )
			smsItems.clear();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( resultCode == RESULT_OK )
		{
			switch( requestCode )
			{
				case AuthenticationActivity.AUTHENTICATED:
					//continue on
					
					break;
				default:
					finish();
			}
		}else
		{
			finish();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private void initAuthenticationActivity()
	{
		Intent intent = new Intent( this.getBaseContext(), AuthenticationActivity.class );
		startActivityForResult( intent, AuthenticationActivity.AUTHENTICATED );
	}
	
	
	private void checkAuthToken()
	{
		String authToken = dbManager.getGoogleAuthToken();
		String phoneNumber = dbManager.getGooglePhoneNumber();
		if( authToken == "" || phoneNumber == "" )
		{
			initAuthenticationActivity();
		}else
		{
			initSMSListItems();
		}
	}
	
	
	private void initSMSListItems()
	{
		smsItems = dbManager.getUnreadSMS();
		smsListAdapter.notifyDataSetChanged();
	}
	
	
	private void hideDialog()
	{
		if( dialog != null )
			dialog.dismiss();
		dialog = null;
	}
	
	
	private class SMSListAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return smsItems.size();
		}

		@Override
		public SMS getItem(int position)
		{
			return smsItems.get( position );
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if( convertView == null )
			{
				convertView = View.inflate( smsListView.getContext(), R.layout.sms_list_item, null );
			}
			SMS s = (SMS) getItem( position );
			TextView fromText = (TextView) convertView.findViewById( R.sms.from );
			TextView messageText = (TextView) convertView.findViewById( R.sms.message );
			TextView timeText = (TextView) convertView.findViewById( R.sms.time_stamp );
			
			fromText.setText( s.getFrom().getNumber() );
			messageText.setText( s.getContent() );
			
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy KK:mm a", Locale.US);
			timeText.setText( df.format( s.getDateTime() ) );
			
			return convertView;
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.sms_menu, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if( item.getItemId() == R.id.mark_read )
		{
			dbManager.markAllSMSAsRead();
			initSMSListItems();
		}
		return false;
	}
}