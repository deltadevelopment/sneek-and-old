package no.twomonkeys.sneek.app.shared.helpers;

import android.text.LoginFilter;
import android.util.Log;


import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by simenlie on 25.05.16.
 */
public class VideoRenderer {

    private int frameLength;
    private List<Frame> frames;


    public VideoRenderer() {
        frames = new ArrayList<>();
    }


    public void test(File inputFile){
        FileChannelWrapper ch = null;
        try {
            ch = NIOUtils.readableFileChannel(new File("filename.mp4"));
            FrameGrab fg = new FrameGrab(ch);
            fg.getNativeFrame();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JCodecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void getFrames(File inputFile) {
        Log.v("HEllo","from the other side");
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputFile.getAbsolutePath());
        if (frameGrabber == null)
        {
            Log.v("GRABBER NULL","IS NULL");
        }
        Log.v("IS here","and here");
        //frameLength = frameGrabber.getLengthInFrames();

        try {
            frameGrabber.start();
            Frame img;

            for (int i = 0; i < 10; i++) {
                img = frameGrabber.grab();
                frames.add(img);
            }
            frameGrabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }


    public void createMovie(File outputFile) {
        try {
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile.getAbsolutePath(), 640, 480);
            //int frameCount = iplimage.length;
/*

            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AMR_NB);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
            recorder.setVideoBitrate(120000);
            recorder.setFrameRate(30); //CHange this

            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setFormat("mp4");

            recorder.start();


            for (int i = 0; i < frameLength; i++) {
                Frame f = frames.get(i);
                recorder.record(f);
            }

            recorder.stop();
*/
        } catch (Exception e) {
            Log.e("problem", "problem", e);
        }
    }

}
