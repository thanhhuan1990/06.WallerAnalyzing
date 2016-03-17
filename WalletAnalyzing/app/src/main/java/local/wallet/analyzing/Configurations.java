package local.wallet.analyzing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

import local.wallet.analyzing.model.Currency;

class Configurations {

	private static final String Tag = "Configurations";
	private static final String PreferencesFile = "wallet_analyzing_preferences";

	public static boolean LogTrace = false;

	private static void trace(String message) {
		if (LogTrace) {
			Log.d(Tag, message);
		}
	}

	public static enum Key {
        Locale,
        Passcode,
        Currency;
	}

	private SharedPreferences mPreferences;

	Configurations(Context context) {

		InputStream fis = null;
		try {
			final String packageName = context.getPackageName();

			final String path = Environment.getExternalStorageDirectory() + File.separator + packageName + ".conf";
			LogTrace = new File(path).exists();
			if(LogTrace) {
				Log.i(Tag, "Extra configuration");
			} else {
				Log.i(Tag, "No configuration");
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		trace("Configurations");
		if (!load(context)) {
			Log.e(Tag, "load error");
		}
	}

	private synchronized boolean load(Context context) {
		trace("load");

		mPreferences = context.getSharedPreferences(PreferencesFile, Context.MODE_PRIVATE);
		Editor editor = mPreferences.edit();

		editor.putString(Key.Passcode.name(), mPreferences.getString(Key.Passcode.name(), "0000"));
        editor.putString(Key.Locale.name(), mPreferences.getString(Key.Locale.name(), "vn"));
        editor.putInt(Key.Currency.name(), mPreferences.getInt(Key.Currency.name(), Currency.CurrencyList.VND.getValue()));

		boolean result = editor.commit();
		if (!result) {
			Log.e(Tag, "Failed to save configurations.");
		}

		return result;
	}

	// Floatは当面必要ないので定義しない.

	synchronized void setBoolean(Key key, boolean value) {
		trace("setBoolean(" + key + ", " + value + ")");
		final Editor editor = mPreferences.edit();
		editor.putBoolean(key.name(), value);
		editor.commit();
	}

	synchronized void setInt(Key key, int value) {
		trace("setInt(" + key + ", " + value + ")");
		final Editor editor = mPreferences.edit();
		editor.putInt(key.name(), value);
		editor.commit();
	}

	synchronized void setLong(Key key, long value) {
		trace("setLong(" + key + ", " + value + ")");
		final Editor editor = mPreferences.edit();
		editor.putLong(key.name(), value);
		editor.commit();
	}

	synchronized void setString(Key key, String value) {
		final Editor editor = mPreferences.edit();
		editor.putString(key.name(), value);
		editor.commit();
	}

	synchronized boolean getBoolean(Key key) {
		final boolean value = mPreferences.getBoolean(key.name(), false);
		trace("getBoolean(" + key + ", " + value + ")");
		return value;
	}

	synchronized int getInt(Key key) {
		final int value = mPreferences.getInt(key.name(), 0);
		trace("getInt(" + key + ", " + value + ")");
		return value;
	}

	synchronized long getLong(Key key) {
		final long value = mPreferences.getLong(key.name(), 0);
		trace("getLong(" + key + ", " + value + ")");
		return value;
	}

	synchronized String getString(Key key) {
		final String value = mPreferences.getString(key.name(), "");
		return value;
	}

}
