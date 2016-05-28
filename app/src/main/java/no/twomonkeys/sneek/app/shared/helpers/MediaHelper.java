package no.twomonkeys.sneek.app.shared.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by simenlie on 28.05.16.
 */
public class MediaHelper {


    public static Bitmap mirrorImage(File imageFile) {
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        matrix.preScale(-1, 1);

        Bitmap originalBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        Log.v("TEST","width " + originalBitmap.getWidth() + " " + originalBitmap.getHeight());
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth(), originalBitmap.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public static File bitmapToFile(Bitmap bitmap)
    {
        File file = getOutputMediaFile();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static Bitmap bitmapImage(File imageFile)
    {
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public void applyFilterToImage() {

    }

    public void mirrorVideo() {

    }

    public void applyFilterToVideo() {

    }
}
