package local.wallet.analyzing.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class FileUtils {

	private static final int MAX_BUFFER = 8192;
	private static final String TAG = FileUtils.class.getSimpleName();

	private FileUtils() {
	}

	private final static String Tag = "FileUtils";

	/**
	 * Get file size as string
	 * 
	 * @param size
	 * 
	 * @return string of size. For example: 2048 => 2.0KB, 31212 => 30.48KB
	 */
	public static String getFileSizeAsString(long size) {
		String b = "B";
		String kb = "KB";
		String mb = "MB";
		String gb = "GB";

		int unit = 1024;
		int kunit = unit * unit;
		int munit = kunit * unit;

		if (size < unit)
			return size + b;

		if (size < kunit) {
			int size100 = (int) (size * 100 / unit);
			return (size100 / 100.0) + kb;
		}

		if (size < munit) {
			int size100 = (int) (size * 100 / kunit);
			return (size100 / 100.0) + mb;
		}

		int size100 = (int) (size * 100 / munit);
		return (size100 / 100.0) + gb;
	}

	/**
	 * Load bitmap from the local memory
	 * 
	 * @param imagePath
	 * @param requiredWidth
	 *            if -1, use size of image
	 * @param requiredHeight
	 *            if -1, use size of image
	 * 
	 * @return bitmap or null if the image is not found.
	 */
	public static Bitmap loadBitmap(String imagePath, int requiredWidth,
	        int requiredHeight) {

		if (TextUtils.isEmpty(imagePath)) {
			return null;
		}

		File imageFile = FileUtils.createFile(imagePath);
		if (imageFile == null) {
			return null;
		}
		if (!imageFile.exists()) {
			return null;
		}

		// First decode with inJustDecodeBounds=true to check dimensions
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap scaledBitmap = BitmapFactory.decodeFile(imagePath, options);

		if (scaledBitmap == null) {
			return null;
		}

		ExifInterface exifInterface = null;
		try {
			exifInterface = new ExifInterface(imagePath);
		} catch (IOException e) {
			return scaledBitmap;
		}

		// Process orientation
		int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
		        ExifInterface.ORIENTATION_NORMAL);
		Matrix matrix = new Matrix();

		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			matrix.postRotate(90);
			break;

		case ExifInterface.ORIENTATION_ROTATE_180:
			matrix.postRotate(180);
			break;

		case ExifInterface.ORIENTATION_ROTATE_270:
			matrix.postRotate(270);
			break;

		default:
			break;
		}

		return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), matrix, true);

	}

	/**
	 * Calculate image size sample
	 * 
	 * @param options
	 * @param reqWidth
	 *            if -1, use size of image
	 * @param reqHeight
	 *            if -1, use size of image
	 * @return image size sample
	 */
	private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {

		if (reqHeight == -1 || reqWidth == -1)
			return 1;
		
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		int inSampleSize = 1;

		// Calculate the largest inSampleSize value that is a power of 2 and
		// keeps both height and width larger than the requested height and
		// width.
		while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
			inSampleSize *= 2;
		}

		return inSampleSize;
	}

	/**
	 * Get the file name from a download URL
	 * 
	 * @param url
	 *            with "/"
	 * 
	 * @return return empty if the URL is invalid or the file type is missing
	 */
	public static String getFileNameFromUrl(String path) {
		String[] parts;
		if (TextUtils.isEmpty(path) || path.indexOf('.') == -1
		        || (parts = path.split("[/]")).length == 0)
			return "";

		return parts[parts.length - 1];
	}

	/**
	 * Get file data as String (encode {@link Base64})
	 * 
	 * @param filePath
	 */
	public static String getFileAsString(String filePath) {

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
		} catch (Exception e) {
			return "";
		}

		int num = 0;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[MAX_BUFFER];

		try {
			while ((num = inputStream.read(bytes)) > 0) {
				outputStream.write(bytes, 0, num);
			}

			return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

		} catch (IOException e) {
			return "";

		} finally {
			try {
				inputStream.close();
			} catch (IOException e1) {
			}

			try {
				outputStream.close();
			} catch (IOException e1) {
			}
		}
	}

	/**
	 * Create a {@link File} object. That is a folder or a file.
	 *
	 * @param path
	 * @return null if the path is invalid to create a file
	 */
	public static File createFile(String path) {
		if (path == null || TextUtils.isEmpty(path.trim()))
			return null;

		File file;
		try {
			file = new File(path);
		} catch (Exception e) {
			return null;
		}

		return file;
	}

}
