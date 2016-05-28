package no.twomonkeys.sneek.app.shared.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by simenlie on 25.05.16.
 */
public class VideoRenderer2 {
    ArrayList<Picture> frames;


    public VideoRenderer2() {
        frames = new ArrayList<>();

    }

    public void recieveFrame(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,640,480,true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


        frames.add(fromBitmap(rotatedBitmap));
    }

    public void renderFrames(File outputFile) {
        Log.v("Size ","size is " + frames.size());
        try {
            VideoRenderer3 vr3 = new VideoRenderer3(outputFile);
            for (int i = 0; i < frames.size(); i++) {

                vr3.encodeNativeFrame(frames.get(i));

            }
            vr3.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        SequenceEncoder sequenceEncoder;

        try {
            sequenceEncoder = new SequenceEncoder(outputFile);
            for (int i = 0; i < frames.size(); i++) {

                    sequenceEncoder.encodeNativeFrame(frames.get(i));

            }
            sequenceEncoder.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    public static Picture fromBitmap(Bitmap src) {
        Picture dst = Picture.create((int) src.getWidth(), (int) src.getHeight(), ColorSpace.RGB);
        fromBitmap(src, dst);
        return dst;
    }

    public static void fromBitmap(Bitmap src, Picture dst) {
        int[] dstData = dst.getPlaneData(0);
        int[] packed = new int[src.getWidth() * src.getHeight()];

        src.getPixels(packed, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());

        for (int i = 0, srcOff = 0, dstOff = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++, srcOff++, dstOff += 3) {
                int rgb = packed[srcOff];
                dstData[dstOff] = (rgb >> 16) & 0xff;
                dstData[dstOff + 1] = (rgb >> 8) & 0xff;
                dstData[dstOff + 2] = rgb & 0xff;
            }
        }
    }



}
