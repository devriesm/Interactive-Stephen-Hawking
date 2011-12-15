package net.markdevries.istephenhawking.db;

import java.util.ArrayList;
import java.util.Date;

import net.markdevries.istephenhawking.utils.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.techventus.server.voice.datatypes.Contact;
import com.techventus.server.voice.datatypes.records.SMS;

public class DatabaseManager
{
	private static final String TAG = DatabaseManager.class.getSimpleName();
	
	private static final String DATABASE_NAME = "hawking_db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_GOOGLE = "google";
	private static final String TABLE_SMS = "sms";
	private static final String TABLE_SMS_BAD = "sms_bad";
	
	private static final String GOOGLE_AUTH_TOKEN = "auth_token";
	private static final String GOOGLE_PHONE_NUMBER = "phone_number";
	private static final String[] ALL_GOOGLE_COLUMNS = { GOOGLE_AUTH_TOKEN, GOOGLE_PHONE_NUMBER };
	
	private static final String SMS_ID = "id";
	private static final String SMS_MESSAGE = "message";
	private static final String SMS_TIME = "time";
	private static final String SMS_FROM = "phone_from";
	private static final String SMS_MARKED_READ = "read";
	private static final String[] ALL_SMS_COLUMNS = { SMS_ID, SMS_MESSAGE, SMS_TIME, SMS_FROM, SMS_MARKED_READ };
	
	private static final String DROP_GOOGLE = "DROP TABLE "+TABLE_GOOGLE+";";
	private static final String DELETE_ALL_ROWS_FROM_GOOGLE = "DELETE FROM "+TABLE_GOOGLE+";";
	
	private static final String CREATE_GOOGLE = "CREATE TABLE IF NOT EXISTS " +
			TABLE_GOOGLE +"(" +
			GOOGLE_AUTH_TOKEN + " VARCHAR PRIMARY KEY, " + 
			GOOGLE_PHONE_NUMBER + " VARCHAR );";

	private static final String CREATE_SMS = "CREATE TABLE IF NOT EXISTS " +
			TABLE_SMS + "(" + 
			SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			SMS_MESSAGE +  " VARCHAR, " +
			SMS_FROM + " VARCHAR, " +
			SMS_TIME + " INTEGER, " +
			SMS_MARKED_READ + " INTEGER DEFAULT 0 );";
	
	private static final String CREATE_SMS_BAD = "CREATE TABLE IF NOT EXISTS " +
			TABLE_SMS_BAD + "(" + 
			SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			SMS_MESSAGE +  " VARCHAR, " +
			SMS_FROM + " VARCHAR, " +
			SMS_TIME + " INTEGER );";
	
	private Context context;
	private SQLiteDatabase db;
	
	
	public DatabaseManager( Context context )
	{
		this.context = context;
		HawkingSQLiteOpenHelper helper = new HawkingSQLiteOpenHelper( context );
		this.db = helper.getWritableDatabase();
	}
	
	
	public long updateGoogleAuth( String token, String phoneNumber )
	{
		long returnVal = -1;
		if( db != null )
		{
			try
			{
				db.execSQL( DELETE_ALL_ROWS_FROM_GOOGLE );
				
				ContentValues cv = new ContentValues();
				cv.put( GOOGLE_AUTH_TOKEN, token );
				cv.put( GOOGLE_PHONE_NUMBER, phoneNumber );
				try
				{
					returnVal = db.insert( TABLE_GOOGLE, null, cv );
				}catch( Exception e2 )
				{
					Log.e( TAG, e2.getMessage() );
					e2.printStackTrace();
				}
			}catch( Exception e1 )
			{
				Log.e( TAG, e1.getMessage() );
				e1.printStackTrace();
			}
		}
		return returnVal;
	}
	
	
	public String getGoogleAuthToken()
	{
		String token = "";
		if( db != null )
		{
			try
			{
				Cursor cursor = db.query( TABLE_GOOGLE, new String[]{ GOOGLE_AUTH_TOKEN }, null, null, null, null, null );
				if( cursor != null )
				{
					cursor.moveToFirst();
					token = cursor.getString( cursor.getColumnIndex( GOOGLE_AUTH_TOKEN ) );
					cursor.close();
				}
			}catch( Exception e )
			{
				Log.e( TAG, e.getMessage() );
			}
		}
		
		return token;
	}
	
	
	public String getGooglePhoneNumber()
	{
		String phoneNumber = "";
		if( db != null )
		{
			try
			{
				Cursor cursor = db.query( TABLE_GOOGLE, new String[]{ GOOGLE_PHONE_NUMBER }, null, null, null, null, null );
				if( cursor != null )
				{
					cursor.moveToFirst();
					phoneNumber = cursor.getString( cursor.getColumnIndex( GOOGLE_PHONE_NUMBER ) );
					cursor.close();
				}
			}catch( Exception e )
			{
				
			}
		}
		
		return phoneNumber;
	}
	
	
	public long addSMS( SMS sms )
	{
		long returnVal = -1;
		
		if( db != null && sms != null && !smsExists( sms ) )
		{
			try
			{
				returnVal = db.insert( TABLE_SMS, null, getSMSAsValues( sms ) );
			}catch( Exception e )
			{
				Log.e( TAG, e.toString() );
				e.printStackTrace();
			}
		}
		return returnVal;
	}
	
	
	public ArrayList<SMS> getUnreadSMS()
	{
		ArrayList<SMS> unread = new ArrayList<SMS>();
		
		if( db != null )
		{
			String selection = SMS_MARKED_READ + " = 0";
			//TODO - Too large of a try/catch. split it out for easier debugging
			try
			{
				Cursor cursor = db.query( TABLE_SMS, ALL_SMS_COLUMNS, selection, null, null, null, SMS_TIME + " ASC" );
				if( cursor != null )
				{
					cursor.moveToFirst();
					while( !cursor.isAfterLast() )
					{
						String message = cursor.getString( cursor.getColumnIndex( SMS_MESSAGE ) );
						String from = cursor.getString( cursor.getColumnIndex( SMS_FROM ) );
						long time = cursor.getLong( cursor.getColumnIndex( SMS_TIME ) );
						
						Contact c = new Contact( "", "", from, "" );
						
						SMS sms = new SMS( c, message, new Date( time ) );
						unread.add( sms );
						
						cursor.moveToNext();
					}
					cursor.close();
				}
			}catch( Exception e )
			{
				Log.e( TAG, e.getMessage() );
				e.printStackTrace();
			}
		}
		return unread;
	}
	
	
	public void markSMSAsRead( SMS sms )
	{
		if( db != null && sms != null && smsExists( sms ) )
		{
			ContentValues values = new ContentValues();
			values.put( SMS_MARKED_READ, 1 );
			try
			{
				db.update( TABLE_SMS, values, SMS_TIME + "=" + sms.getDateTime().getTime() + " AND " + SMS_FROM + "= '" + sms.getFrom().getNumber() +"'", null );
			}catch( Exception e )
			{
				Log.e( TAG, e.getMessage() );
				e.printStackTrace();
			}
		}
	}
	
	
	public void markAllSMSAsRead()
	{
		if( db != null )
		{
			ContentValues values = new ContentValues();
			values.put( SMS_MARKED_READ, 1 );
			try
			{
				db.update( TABLE_SMS, values, SMS_MARKED_READ + " = " + 0, null );
			}catch( Exception e )
			{
				Log.e( TAG, "Error : " + e.getMessage() );
				e.printStackTrace();
			}
		}
	}
	
	
	public SMS getNextSMS()
	{
		SMS sms = new SMS( new Contact("", "", "", ""), "", new Date() );
		if( db != null )
		{
			String selection = SMS_MARKED_READ + " = 0";
			Cursor cursor = db.query(TABLE_SMS, ALL_SMS_COLUMNS, selection, null, null, null, SMS_TIME + " ASC", "1");
			if( cursor != null && cursor.getCount() > 0 )
			{
				cursor.moveToFirst();
				String message = cursor.getString( cursor.getColumnIndex( SMS_MESSAGE ) );
				String from = cursor.getString( cursor.getColumnIndex( SMS_FROM ) );
				long time = cursor.getLong( cursor.getColumnIndex( SMS_TIME ) );
				
				Contact c = new Contact( "", "", from, "" );
				
				sms = new SMS( c, message, new Date( time ) );
				cursor.close();
			}
		}
		return sms;
	}
	
	
	private ContentValues getSMSAsValues( SMS sms )
	{
		ContentValues values = new ContentValues();
		if( sms != null )
		{
			values.put( SMS_MESSAGE, sms.getContent().replaceAll("[^A-Za-z0-9 ]", "") ); //remove extra characters, could cause issues.
			values.put( SMS_TIME, sms.getDateTime().getTime() );
			values.put( SMS_FROM, sms.getFrom().getNumber() );
		}
		return values;
	}
	
	
	public boolean smsExists( SMS s )
	{
		boolean exists = false;
		if( db != null && s != null )
		{
			try
			{
				Cursor cursor = db.rawQuery( "SELECT 1 FROM " + TABLE_SMS + " WHERE " + SMS_FROM + " = '" + s.getFrom().getNumber() + "' AND " + SMS_TIME + " = " + s.getDateTime().getTime() + "", null );
				if( cursor != null )
				{
					exists = (cursor.getCount() > 0 );
					cursor.close();
				}
			}catch( Exception e )
			{
				Log.e( TAG, e.getMessage() );
				e.printStackTrace();
			}
		}
		return exists;
	}
	
	public boolean smsBadExists( SMS s )
	{
		boolean exists = false;
		if( db != null && s != null )
		{
			try
			{
				Cursor cursor = db.rawQuery( "SELECT 1 FROM " + TABLE_SMS_BAD + " WHERE " + SMS_FROM + " = '" + s.getFrom().getNumber() + "' AND " + SMS_TIME + " = " + s.getDateTime().getTime() + "", null );
				if( cursor != null )
				{
					exists = (cursor.getCount() > 0 );
					cursor.close();
				}
			}catch( Exception e )
			{
				Log.e( TAG, e.getMessage() );
				e.printStackTrace();
			}
		}
		return exists;
	}
	
	
	public long addSMSToBadTable( SMS sms )
	{
		long returnVal = -1;
		
		if( db != null && sms != null && !smsBadExists( sms ) )
		{
			try
			{
				returnVal = db.insert( TABLE_SMS_BAD, null, getSMSAsValues( sms ) );
			}catch( Exception e )
			{
				Log.e( TAG, e.toString() );
				e.printStackTrace();
			}
		}
		return returnVal;
	}
	
	
	public void destroy()
	{
		this.context = null;
		this.db.close();
		this.db = null;
	}
	
	
	private class HawkingSQLiteOpenHelper extends SQLiteOpenHelper
	{
		
		public HawkingSQLiteOpenHelper( Context context )
		{
			super( context, DATABASE_NAME, null, DATABASE_VERSION );
		}
		
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL( CREATE_GOOGLE );
			db.execSQL( CREATE_SMS );
			db.execSQL( CREATE_SMS_BAD );
		}
		
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			
		}
	}
}
