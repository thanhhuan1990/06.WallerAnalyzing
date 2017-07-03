package local.wallet.analyzing.utils;

import android.util.Log;

/**
 * This class provide the method to print out the log for WalletAnalyzing
 */
public class LogUtils {

	/****************************************************************************************************
	 * Private members
	 ***************************************************************************************************/
	private static int MAX_TAG  = 30;
	private static final String PADDING_STRING = " ";

	private static int LOG_LEVEL   = Log.INFO;
	/****************************************************************************************************
	 * Public method
	 ***************************************************************************************************/
	public static void setLogLevel(int level) {
		LOG_LEVEL   = level;
	}

	// ------------------------------------------------------------------------------------------------

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
		if (LOG_LEVEL <= Log.DEBUG) {
			String strTid = String.valueOf(Thread.currentThread().getId());
			Log.d(tag, getPaddingString(tag) + "=[Thread_id:" + strTid + "]  " + message);
		}
	}

    /**
     * Print message in INFO mode
     *
     * @param tag
     * @param message
     */
    public static void info(String tag, String message) {
		if (LOG_LEVEL <= Log.INFO) {
			String strTid = String.valueOf(Thread.currentThread().getId());
			Log.i(tag, getPaddingString(tag) + "=[Thread_id:" + strTid + "]  " + message);
		}
    }

    /**
     * Print message in WARNING mode
     *
     * @param tag
     * @param message
     */
    public static void warn(String tag, String message) {
		if (LOG_LEVEL <= Log.WARN) {
			String strTid = String.valueOf(Thread.currentThread().getId());
			Log.w(tag, getPaddingString(tag) + "=[Thread_id:" + strTid + "]  " + message);
		}
    }
	// ------------------------------------------------------------------------------------------------
	/**
	 * Print error message in DEBUG mode
	 * 
	 * @param message
	 */
	public static void error(String tag, String message) {
		if (LOG_LEVEL <= Log.ERROR) {
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
		if (LOG_LEVEL <= Log.ERROR) {
			Log.e(tag, " ", throwable);
			throwable.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print the log when enter the function
	 *
	 */
	public static void logEnterFunction(String tag) {
		if (LOG_LEVEL <= Log.INFO) {
			Log.i(tag, createEnterMessage(tag, getMethodName()));
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print the log when enter the function
	 * 
	 */
	public static void logEnterFunction(String tag, String param) {
		if (LOG_LEVEL <= Log.INFO) {
			Log.i(tag, createEnterMessage(tag, getMethodName()));
			if(param != null) {
				trace(tag, param);
			}
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Create enter message
	 *
	 */
	private static String createEnterMessage(String tag, String methodName) {
		String strTid = String.valueOf(Thread.currentThread().getId());
		return getPaddingString(tag) + "=[Thread_id:" + strTid + "]──[ENTER]───── " + methodName + "() ──────────┐";
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print the log when leave the function
	 */
	public static void logLeaveFunction(String tag) {
		if (LOG_LEVEL <= Log.INFO) {
			Log.i(tag, createLeaveMessage(tag, getMethodName()));
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Print the log when leave the function
	 */
	public static void logLeaveFunction(String tag, String result) {
		if (LOG_LEVEL <= Log.INFO) {
			Log.i(tag, createLeaveMessage(tag, getMethodName()) + (result != null ? "→ return " + result : ""));
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Create leave message
	 *
	 */
	private static String createLeaveMessage(String tag, String methodName) {
		String strTid = String.valueOf(Thread.currentThread().getId());
		return getPaddingString(tag) + "=[Thread_id:" + strTid + "]──[LEAVE]───── " + methodName + "() ──────────┘";
	}

	// ------------------------------------------------------------------------------------------------
	/**
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
