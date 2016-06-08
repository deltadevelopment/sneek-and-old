package no.twomonkeys.sneek.app.components.Camera;

import android.content.Context;
import android.text.InputType;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;

/**
 * Created by simenlie on 19.05.16.
 */
public class CaptionEditView extends RelativeLayout {

    EditText editText;
    SimpleCallback2 onCaptionDone;

    public CaptionEditView(Context context) {
        super(context);
        initializeViews(context);
    }

    public CaptionEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CaptionEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.caption_edit_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        editText = (EditText) findViewById(R.id.captionEditText);
        editText.setLineSpacing(2,1.85f);

        editText.setTextSize(15);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(INVISIBLE);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                onCaptionDone.callbackCall();
            }
        });

    }

    public void startCaption() {
        setVisibility(VISIBLE);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean hasCaption() {
        if (editText.getText().length() > 0) {
            return true;
        }
        return false;
    }

    public Layout getEditLayout() {
        return editText.getLayout();
    }

    public String captionTxt() {
        return editText.getText().toString();
    }

}
