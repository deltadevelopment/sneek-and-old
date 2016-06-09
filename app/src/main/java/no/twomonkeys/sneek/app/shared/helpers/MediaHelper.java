package no.twomonkeys.sneek.app.shared.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import org.jcodec.common.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import no.twomonkeys.sneek.app.shared.views.VintageFilter;

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

    public interface FileProcessedCallback {
        void onProcessed(File file);
    }


    public static FileProcessedCallback videoProcessed;
    public static FileProcessedCallback imageProcessed;

    public static VideoMirroredCallback videoMirrored;
    public static VideoFilterCallback bwFiltered;
    public static VideoFilterCallback vintageFiltered;
    public static String fileNum = "";


    public static Bitmap mirrorImage(File imageFile) {
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        matrix.preScale(-1, 1);

        Bitmap originalBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        Log.v("TEST", "width " + originalBitmap.getWidth() + " " + originalBitmap.getHeight());
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth(), originalBitmap.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public static File bitmapToFile(Bitmap bitmap) {
        File file = getOutputMediaFile();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
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

    public static Bitmap bitmapImage(File imageFile) {
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public void applyFilterToImage() {

    }

    public static void blackWhiteVideo(File inputFile, Context context) {
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

    public static void vintageVideo(File inputFile, Context context) {
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

    public static void processImage(Bitmap image, int filterNumber, Context context) {
        int targetWidth = 640; // your arbitrary fixed limit
        int targetHeight = (int) (image.getHeight() * targetWidth / (double) image.getWidth()); // casts to avoid truncating
        Bitmap smaller = Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);

        GPUImage mGPUImage = new GPUImage(context);

        switch (filterNumber) {
            case 0: {
                mGPUImage.setFilter(new GPUImageFilter());
                break;
            }
            case 1: {
                mGPUImage.setFilter(new GPUImageGrayscaleFilter());
                break;
            }
            case 2: {
                mGPUImage.setFilter(new VintageFilter(context));
                break;
            }
        }
        mGPUImage.setImage(smaller);
        Bitmap uncompressed = mGPUImage.getBitmapWithFilterApplied();
        File file = bitmapToFile(uncompressed);
        // Get length of file in bytes
        long fileSizeInBytes = file.length();
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        Log.v("file size is", "file sise " + fileSizeInMB + " KB " + fileSizeInKB +  " B " + fileSizeInBytes  );
        imageProcessed.onProcessed(file);
    }

    public static void processVideo(File inputFile, boolean flip, int filterNumber, Context context) {
        final File outFile = getOutputMediaFile(2);
        String s = inputFile.getAbsolutePath();
        String s2 = outFile.getAbsolutePath();
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add("-y");
        cmd.add("-i");
        cmd.add(s);

        FFmpeg ffmpeg = FFmpeg.getInstance(context);

        ArrayList videoEffects = new ArrayList();

        if (filterNumber > 0 || flip) {
            cmd.add("-vf");

            if (flip) {
                videoEffects.add("hflip");
                //cmd.add("hflip, hue=s=0");
            }
            if (filterNumber == 1) {
                videoEffects.add("hue=s=0");
            } else if (filterNumber == 2) {
                videoEffects.add("curves=r='0/0.11 .42/.51 1/0.95':g='0.50/0.48':b='0/0.22 .49/.44 1/0.8'");
            }

            cmd.add(StringUtils.join(videoEffects.toArray(), ", "));

            if (filterNumber == 2) {
                cmd.add("-pix_fmt");
                cmd.add("yuv420p");
            }


            // to execute "ffmpeg -version" command you just need to pass "-version"

        } else {
            //No more stuff to do

        }
        cmd.add("-codec:v");
        cmd.add("libx264");
        cmd.add("-movflags");
        cmd.add("faststart");

        cmd.add("-threads");
        cmd.add("5");
        cmd.add("-preset");
        cmd.add("ultrafast");
        cmd.add("-acodec");
        cmd.add("copy");
        cmd.add(s2);

        Object[] objectList = cmd.toArray();
        String[] cmdArray = Arrays.copyOf(objectList, objectList.length, String[].class);


        try {
            ffmpeg.execute(cmdArray, new ExecuteBinaryResponseHandler() {

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
                    videoProcessed.onProcessed(null);
                }

                @Override
                public void onSuccess(String message) {
                    Log.v("Message is", message);
                    Log.v("Completed", "COMPLETED NOW");
                    // Get length of file in bytes
                    long fileSizeInBytes = outFile.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;

                    Log.v("MEDIAHELPER", "file size is " + fileSizeInMB);
                    videoProcessed.onProcessed(outFile);
                    //loadVideo(output);
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
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
