package no.twomonkeys.sneek.app.components.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.ProgressRequestBody;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.views.CaptionView;
import no.twomonkeys.sneek.app.shared.views.ProgressIndicator;

/**
 * Created by simenlie on 19.05.16.
 */
public class CameraEditView extends RelativeLayout {

    SimpleDraweeView photoTakenView;
    Button backBtn, captionBtn, saveBtn, uploadBtn;
    public SimpleCallback onCancelEdit;
    public SimpleCallback onMediaPosted;
    public CaptionEditView captionEditView;
    public CaptionView captionView;
    float oldY;
    boolean isVideo;
    int captionPosition;
    File media;
    View progressView;

    public CameraEditView(Context context) {
        super(context);
        initializeViews(context);
    }

    public CameraEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CameraEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.camera_edit_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.

        /*
        maxSize = UIHelper.dpToPx(getContext(), 35);
        progressIndicatorOutline = new ProgressIndicator(getContext(), false);
        RelativeLayout.LayoutParams prms = new RelativeLayout.LayoutParams(maxSize, maxSize);
        progressIndicatorOutline.setLayoutParams(prms);

        progressIndicatorFill = new ProgressIndicator(getContext(), true);
        ViewGroup.LayoutParams prms2 = new ViewGroup.LayoutParams(0, maxSize);
        progressIndicatorFill.setLayoutParams(prms2);

        addView(progressIndicatorOutline);
        addView(progressIndicatorFill);
        */
        final CameraEditView self = this;

        saveBtn = (Button) findViewById(R.id.cameraEditSaveBtn);


        captionBtn = (Button) findViewById(R.id.cameraEditCaptionBtn);
        backBtn = (Button) findViewById(R.id.cameraEditBackBtn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                self.setVisibility(INVISIBLE);
                onCancelEdit.callbackCall();
            }
        });
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMediaToDisk();
            }
        });
        captionBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCaption();
            }
        });

        photoTakenView = (SimpleDraweeView) findViewById(R.id.photoTaken);
        captionEditView = (CaptionEditView) findViewById(R.id.cameraEditCaptionEdit);
        captionEditView.setVisibility(INVISIBLE);
        uploadBtn = (Button) findViewById(R.id.cameraEditUploadBtn);
        uploadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMedia();
            }
        });

        captionView = (CaptionView) findViewById(R.id.cameraEditCaptionView);
        captionView.setVisibility(INVISIBLE);
        captionPosition = 0;
        captionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editCaption();
            }
        });

        captionEditView.onCaptionDone = new SimpleCallback() {
            @Override
            public void callbackCall() {
                updateCaption();
            }
        };

        progressView = (View) findViewById(R.id.progressUpload);
    }

    public void updateCaption() {
        if (captionEditView.hasCaption()) {
            captionView.setVisibility(VISIBLE);

            //captionView.updateCaption(layout.getLineCount(), layout.getLineStart(layout.getLineCount() - 1), caption);
            Layout l = captionEditView.getEditLayout();
            captionView.updateCaption(l.getLineCount(), l.getLineStart(l.getLineCount() - 1), captionEditView.captionTxt());
        }
        showTools();
    }

    public void addPhoto(File file) {
        media = file;
        photoTakenView.setImageURI(Uri.fromFile(file));
        isVideo = false;
    }

    public void saveMediaToDisk() {

    }

    public void addCaption() {
        if (captionEditView.hasCaption()) {
            //Move around
            switch (captionPosition) {
                case 0: {
                    oldY = captionView.getY();
                    moveCaptionToY(UIHelper.screenHeight(getContext()) / 2);
                    captionPosition = 1;
                    break;
                }
                case 1: {
                    moveCaptionToY(UIHelper.screenHeight(getContext()) - 150);
                    captionPosition = 2;
                    break;
                }
                case 2: {
                    moveCaptionToY(oldY);
                    captionPosition = 0;
                    break;
                }
            }
        } else {
            //start adding caption
            editCaption();
        }
    }

    public void compressImage() {


        //create a file to write bitmap data
        File f = new File(getContext().getCacheDir(), "tmp.jpeg");
        try {
            f.createNewFile();
            //Convert bitmap to byte array
            Bitmap bitmap = BitmapFactory.decodeFile(media.getAbsolutePath());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            media = f;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public void uploadMedia() {
        uploadBtn.setVisibility(INVISIBLE);
        media = saveBitmapToFile(media);
        MomentModel momentModel = new MomentModel();
        momentModel.setMedia_type(isVideo ? 1 : 0);

        //Check if moment on disk is null

        momentModel.setLatitude(0);
        momentModel.setLongitude(0);

        if (captionEditView.hasCaption()) {
            momentModel.setCaption_position(captionPosition);
            momentModel.setCaption(captionEditView.captionTxt());
        }
        momentModel.setMedia(media);
        if (isVideo) {
            momentModel.setThumbnail_url(null);
            //set disk file path?
        }
        // momentModel setplaceId

        momentModel.progress = new MomentModel.MomentCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                float percentageOfScreen = (UIHelper.screenWidth(getContext()) * percentage) / 100;

                LayoutParams lr = new LayoutParams(0, UIHelper.dpToPx(getContext(), 2));
                lr.width = (int) percentageOfScreen;
                progressView.setLayoutParams(lr);
            }
        };

        momentModel.saveWithProgression(new SimpleCallback() {
            @Override
            public void callbackCall() {
                Log.v("POSTED TO BEN", "POSTED MOMENT");
                onMediaPosted();
            }
        });
    }

    public void onMediaPosted() {
        LayoutParams lr = new LayoutParams(0, UIHelper.dpToPx(getContext(), 2));
        lr.width = 0;
        progressView.setLayoutParams(lr);
        setVisibility(INVISIBLE);
        onMediaPosted.callbackCall();
        uploadBtn.setVisibility(VISIBLE);
    }

    public void moveCaptionToY(float yPos) {
        captionView.setY(yPos);
    }

    public void editCaption() {
        captionEditView.setVisibility(VISIBLE);
        captionEditView.startCaption();
        hideTools();
    }

    public void hideTools() {
        backBtn.setVisibility(INVISIBLE);
        uploadBtn.setVisibility(INVISIBLE);
        saveBtn.setVisibility(INVISIBLE);
        captionBtn.setVisibility(INVISIBLE);
        captionView.setVisibility(INVISIBLE);
    }

    public void showTools() {
        backBtn.setVisibility(VISIBLE);
        uploadBtn.setVisibility(VISIBLE);
        saveBtn.setVisibility(VISIBLE);
        captionBtn.setVisibility(VISIBLE);
    }

}
