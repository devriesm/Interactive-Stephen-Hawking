package net.markdevries.istephenhawking.utils;

import android.util.Patterns;

public class Validation
{
	
	public static boolean isValidEmailAddress( String target )
	{
		try
		{
			return Patterns.EMAIL_ADDRESS.matcher( target ).matches();
		}catch( NullPointerException e )
		{
			return false;
		}
	}
	
}
