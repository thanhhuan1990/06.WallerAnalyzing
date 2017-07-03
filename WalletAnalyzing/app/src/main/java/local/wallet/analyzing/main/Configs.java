package local.wallet.analyzing.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.model.Currency;

public class Configs {

	private static final String Tag 					= "Configurations";
	private static final String PreferencesFile 		= "wallet_analyzing_preferences";

	private final String 	TAG_PASS_CODE 				= Key.PassCode.name();
	private final String 	TAG_LOCALE            		= Key.Locale.name();
	private final String 	TAG_CURRENCY             	= Key.Currency.name();
	private final String 	TAG_LOG_LEVEL               = "log_level";

	private final String 	DEFAULT_PASS_CODE 			= "0000";
	private final String 	DEFAULT_LOCALE        		= "vn";
	private final int   	DEFAULT_CURRENCY         	= Currency.CurrencyList.VND.getValue();

	public enum Key {
        Locale,
        PassCode,
        Currency;
	}

	private SharedPreferences mPreferences;

	public Configs(Context context) {
		mPreferences = context.getSharedPreferences(PreferencesFile, Context.MODE_PRIVATE);
	}


	/**
	 * Load debug config from debug_config.txt in SDCard
	 * @param context
	 */
	public void loadDebugConfig(Context context) {
		LogUtils.logEnterFunction(Tag);

		// Set Debug config
		String path = Environment.getExternalStorageDirectory().getPath()
				+ File.separator
				+ context.getApplicationInfo().loadLabel(context.getPackageManager())
				+ File.separator
				+ "Debug_config.json";

		FileInputStream stream = null;
		try {
			stream = new FileInputStream(new File(path));

			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			String jString = Charset.defaultCharset().decode(bb).toString();

			JSONObject jsonConfig = new JSONObject(jString);

			// Default Value
			setString(Key.PassCode,   	jsonConfig.optString(TAG_PASS_CODE, DEFAULT_PASS_CODE));
			setString(Key.Locale, 		jsonConfig.optString(TAG_LOCALE, 	DEFAULT_LOCALE));
			setInt(Key.Currency,  		jsonConfig.optInt(TAG_CURRENCY, 	DEFAULT_CURRENCY));

			// Log Level
			LogUtils.setLogLevel(jsonConfig.has(TAG_LOG_LEVEL) ? (jsonConfig.getInt(TAG_LOG_LEVEL) + 2) : Log.ERROR);

		} catch (IOException e) {
			e.printStackTrace();
			useDefaultConfigurations();
		} catch (JSONException je) {
			je.printStackTrace();
			useDefaultConfigurations();
		} finally {
			try {
				if(stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Floatは当面必要ないので定義しない.

	synchronized void setBoolean(Key key, boolean value) {
		LogUtils.trace(Tag, "setBoolean(" + key + ", " + value + ")");
		final Editor editor = mPreferences.edit();
		editor.putBoolean(key.name(), value);
		editor.commit();
	}

	synchronized void setInt(Key key, int value) {
		LogUtils.trace(Tag, "setInt(" + key + ", " + value + ")");
		final Editor editor = mPreferences.edit();
		editor.putInt(key.name(), value);
		editor.commit();
	}

	synchronized void setLong(Key key, long value) {
		LogUtils.trace(Tag, "setLong(" + key + ", " + value + ")");
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
		LogUtils.trace(Tag, "getBoolean(" + key + ", " + value + ")");
		return value;
	}

	synchronized public int getInt(Key key) {
		final int value = mPreferences.getInt(key.name(), 0);
		LogUtils.trace(Tag, "getInt(" + key + ", " + value + ")");
		return value;
	}

	synchronized long getLong(Key key) {
		final long value = mPreferences.getLong(key.name(), 0);
		LogUtils.trace(Tag, "getLong(" + key + ", " + value + ")");
		return value;
	}

	synchronized String getString(Key key) {
		final String value = mPreferences.getString(key.name(), "");
		return value;
	}

	/**
	 * Save default configuration to SharedPreferences
	 */
	private void useDefaultConfigurations() {
		LogUtils.logEnterFunction(Tag);
		setString(Key.PassCode, DEFAULT_PASS_CODE);
		setString(Key.Locale, 	DEFAULT_LOCALE);
		setInt(Key.Currency, 	DEFAULT_CURRENCY);
		LogUtils.logLeaveFunction(Tag);
	}

}
