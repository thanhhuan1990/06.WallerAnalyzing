package local.wallet.analyzing.Utils;

import android.util.Log;

import local.wallet.analyzing.BuildConfig;

/**
 * This class provide the method to print out the log for VoipTester The tag log
 * is define {@link #TAG}
 */
public class LogUtils {

	/****************************************************************************************************
	 * Public members
	 ***************************************************************************************************/
	public enum LogModule {
		SUPREE, AudioEngine
	}

	/****************************************************************************************************
	 * Private members
	 ***************************************************************************************************/
    private static int MAX_TAG  = 30;
	private static final String PADDING_STRING = " ";
	private static int sMethodStackLevel = 0;

	/****************************************************************************************************
	 * Public method
	 ***************************************************************************************************/

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print message in DEBUG mode
	 * 
	 * @param message
	 */
	public static void trace(String tag, String message) {
		if (BuildConfig.DEBUG) {
            String strTid = String.valueOf(Thread.currentThread().getId());
			Log.d(tag, getPaddingString(tag) + "=[Thread_id:" + strTid + "]  " + message);
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print error message in DEBUG mode
	 * 
	 * @param message
	 */
	public static void error(String tag, String message) {
		if (BuildConfig.DEBUG) {
            String strTid = String.valueOf(Thread.currentThread().getId());
			Log.e(tag,getPaddingString(tag) + "=[Thread_id:" + strTid + "]  " + message);
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print error message in DEBUG mode
	 * 
	 * @param throwable
	 */
	public static void error(String tag, Throwable throwable) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, " ", throwable);

			throwable.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print the log when enter the function
	 * 
	 */
	public static void logEnterFunction(String tag, String param) {
		if (BuildConfig.DEBUG) {
			String strTid = String.valueOf(Thread.currentThread().getId());
			Log.i(tag, getPaddingString(tag) + "=[Thread_id:" + strTid + "]──[ENTER]───── " + getMethodName() + "(" + (param != null ? param : "") + ") ──────────┐");
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print the log when leave the function
	 */
	public static void logLeaveFunction(String tag, String param, String result) {
		if (BuildConfig.DEBUG) {
			String strTid = String.valueOf(Thread.currentThread().getId());

			String temp = "";

			if(param != null) {
				for(int i = 0; i < param.length(); i++) {
					temp+= "-";
				}
			}

			Log.i(tag, getPaddingString(tag) + "=[Thread_id:" + strTid + "]──[LEAVE]───── " + getMethodName() + "() " + temp + "──────────┘" + (result != null ? "→ return " + result : ""));
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Get the current method name Use {@link getStackTrace()} function
	 * 
	 * @return the name of method that call this method
	 */
	private static String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[4].getMethodName();
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Get the padding string use to append to the begin of the log This will be
	 * calculate base on the level of the current method in he stack trace
	 * 
	 * @return
	 */
    private static String getPaddingString(String tag) {
        String strPad = "";

        for(int i = 0; i < MAX_TAG - tag.length(); i++) {
            strPad += PADDING_STRING;
        }

        return strPad;
    }
}
