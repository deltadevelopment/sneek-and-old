package no.twomonkeys.sneek.app.components.story;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.views.LoadingFragment;

/**
 * Created by simenlie on 13.05.16.
 */

// Instances of this class are fragments representing a single
// object in our collection.
public class MomentFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    private static SimpleDraweeView draweeView;
    private MomentModel momentModel;
    private LoadingFragment loadingFragment;
    ControllerListener controllerListener;

    public MomentFragment() {

    }

    public void setMomentModel(MomentModel momentModel) {
        this.momentModel = momentModel;
        Log.v("MOMENT MODEL", " id " + momentModel.id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        Log.v("CREATED", "on created");
        View rootView = inflater.inflate(
                R.layout.moment_fragment, container, false);
        Bundle args = getArguments();


        RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.momentFragment);
        draweeView = (SimpleDraweeView) rootView.findViewById(R.id.draweeView);
        FragmentManager fragmentManager = getChildFragmentManager();
        loadingFragment = (LoadingFragment) fragmentManager.findFragmentById(R.id.loadingFragment);

        GenericDraweeHierarchy hierarchy = draweeView.getHierarchy();
        hierarchy.setFadeDuration(0);
        hierarchy.setPlaceholderImage(R.drawable.splash2);

        Bundle bundle = getArguments();
        loadingFragment.startAnimate();

        //here is your list array
        String[] myStrings = bundle.getStringArray("elist");
        Log.v("arr", "args" + getArguments());
        updateView();
        return rootView;
    }

    public void hideLoading() {
        loadingFragment.stopAnimation();
    }

    public void updateView() {
        if (momentModel != null && draweeView != null) {
            if (momentModel.media_type == 0) {
                if (draweeView == null) {
                    Log.v("MOMENT ", " NULL");
                }
                Uri uri = Uri.parse(momentModel.getMedia_url());
                loadImageFromUri(uri);
            } else {
                Uri uri = Uri.parse(momentModel.getThumbnail_url());
                loadImageFromUri(uri);
            }
        }

    }

    public void loadImageFromUri(Uri uri) {
        Log.v("Called", "called");
        controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {

                hideLoading();
                if (imageInfo == null) {
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                FLog.d("Final image received! " +
                                "Size %d x %d",
                        "Quality level %d, good enough: %s, full quality: %s",
                        imageInfo.getWidth(),
                        imageInfo.getHeight(),
                        qualityInfo.getQuality(),
                        qualityInfo.isOfGoodEnoughQuality(),
                        qualityInfo.isOfFullQuality());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                // FLog.d("Intermediate image received")
                Log.v("Img Re", "Recieved");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                FLog.e(getClass(), throwable, "Error loading %s", id);
            }
        };

        Log.v("URI IS", "uri " + uri);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setTapToRetryEnabled(true)
                .setOldController(draweeView.getController())
                .setControllerListener(controllerListener)
                .build();
        draweeView.clearAnimation();
        draweeView.setController(controller);
        draweeView.setFadingEdgeLength(0);
        // draweeView.setImageURI(uri);
    }

}
