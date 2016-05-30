package no.twomonkeys.sneek.app.shared.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

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

    public interface VideoMirroredCallback {
        void onMirrored(File file);
    }
    public interface VideoFilterCallback {
        void onFiltered(File file);
    }


    public static VideoMirroredCallback videoMirrored;
    public static VideoFilterCallback bwFiltered;
    public static VideoFilterCallback vintageFiltered;
    public static String fileNum = "";


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

    public static void blackWhiteVideo(File inputFile, Context context)
    {
        String s = inputFile.getAbsolutePath();
        fileNum = "bw";
        final File outFile = getOutputMediaFile(2);
        String s2 = outFile.getAbsolutePath();

        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            String[] test2 = new String[]{
                    "-y",                // Overwrite output files
                    "-i",                // Input file
                    s,
                    "-vf",
                    "hue=s=0",
                    "-threads",
                    "5",
                    "-preset",
                    "ultrafast",
                    "-acodec",           // Audio codec
                    "copy",
                    s2 // Output file
            };
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(test2, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.v("Progress is", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.v("Failure is", message);
                    bwFiltered.onFiltered(null);
                }

                @Override
                public void onSuccess(String message) {
                    Log.v("Message is", message);
                    Log.v("Completed", "COMPLETED NOW");
                    bwFiltered.onFiltered(outFile);
                    //loadVideo(output);
                }
                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    public static void vintageVideo(File inputFile, Context context)
    {
        String s = inputFile.getAbsolutePath();
        fileNum = "vin";
        final File outFile = getOutputMediaFile(2);
        String s2 = outFile.getAbsolutePath();

        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            String[] test2 = new String[]{
                    "-y",                // Overwrite output files
                    "-i",                // Input file
                    s,
                    "-vf",
                    "curves=r='0/0.11 .42/.51 1/0.95':g='0.50/0.48':b='0/0.22 .49/.44 1/0.8'",
                    "-threads",
                    "5",
                    "-preset",
                    "ultrafast",
                    "-pix_fmt",
                    "yuv420p",
                    "-acodec",           // Audio codec
                    "copy",
                    s2 // Output file
            };
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(test2, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.v("Progress is", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.v("Failure is", message);
                    vintageFiltered.onFiltered(null);
                }

                @Override
                public void onSuccess(String message) {
                    Log.v("Message is", message);
                    Log.v("Completed", "COMPLETED NOW");
                    vintageFiltered.onFiltered(outFile);
                    //loadVideo(output);
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    public static void mirrorVideo(File inputFile, Context context) {
        String s = inputFile.getAbsolutePath();
        final File outFile = getOutputMediaFile(2);
        String s2 = outFile.getAbsolutePath();

        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            String[] test2 = new String[]{
                    "-y",                // Overwrite output files
                    "-i",                // Input file
                    s,
                    "-vf",
                    "hflip",
                    "-threads",
                    "5",
                    "-preset",
                    "ultrafast",
                    "-acodec",           // Audio codec
                    "copy",
                    s2 // Output file
            };
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(test2, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.v("Progress is", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.v("Failure is", message);
                    videoMirrored.onMirrored(null);
                }

                @Override
                public void onSuccess(String message) {
                    Log.v("Message is", message);
                    Log.v("Completed", "COMPLETED NOW");
                    videoMirrored.onMirrored(outFile);
                    //loadVideo(output);
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }

    }

    public void applyFilterToVideo() {

    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + fileNum + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
