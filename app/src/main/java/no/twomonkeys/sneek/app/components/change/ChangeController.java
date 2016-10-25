package no.twomonkeys.sneek.app.components.change;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.block.BlockListAdapter;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.AuthHelper;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;
import no.twomonkeys.sneek.app.shared.views.LoadingView;
import no.twomonkeys.sneek.app.shared.views.MessageView;

/**
 * Created by simenlie on 16.06.16.
 */
public class ChangeController extends RelativeLayout {

    ImageButton backBtn;
    Button saveBtn;
    EditText firstEditText, secondEditText, emailEditText;
    boolean passwordMode;
    UserModel userModel;
    TextView firstError, secondError, changeInfoTxt, headerTextView;
    LoadingView loadingView;
    MessageView messageView;

    public ChangeController(Context context) {
        super(context);
        initializeViews(context);
    }

    public ChangeController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ChangeController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.change_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        backBtn = (ImageButton) findViewById(R.id.backBtn3);
        //UIHelper.layoutBtnRelative(getContext(), backBtn, "BACK");
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateOut();
            }
        });
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        firstEditText = (EditText) findViewById(R.id.firstEditText);
        secondEditText = (EditText) findViewById(R.id.secondEditText);
        firstEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                removeErrors();
                enableSave();
            }
        });

        secondEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                removeErrors();
                enableSave();
            }
        });

        firstError = (TextView) findViewById(R.id.firstError);
        secondError = (TextView) findViewById(R.id.secondError);
        firstError.setVisibility(GONE);
        secondError.setVisibility(GONE);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    //  UIHelper.layoutBtn(getContext(), saveBtn, "SAVE");
        enableSave();

        loadingView = (LoadingView) findViewById(R.id.loadingView);
        loadingView.removeBg();
        loadingView.blackTheme();
        loadingView.setVisibility(GONE);

        messageView = (MessageView) findViewById(R.id.messageViewChange);
        messageView.hide();

        changeInfoTxt = (TextView) findViewById(R.id.changeInfoTxt);
        headerTextView = (TextView) findViewById(R.id.headerTextView);
    }

    public void enableSave() {
        if (passwordMode) {
            if (passwordIsValid()) {
                saveBtn.setEnabled(true);
                saveBtn.setTextColor(getContext().getColor(R.color.white));
            } else {
                saveBtn.setEnabled(false);
                saveBtn.setTextColor(getContext().getColor(R.color.gray));
            }
        } else {
            if (firstEditText.getText().toString().length() > 0) {
                saveBtn.setEnabled(true);
                saveBtn.setTextColor(getContext().getColor(R.color.white));
            } else {
                saveBtn.setEnabled(false);
                saveBtn.setTextColor(getContext().getColor(R.color.gray));
            }
        }
    }

    public void animateInPassword() {
        passwordMode = true;
        headerTextView.setText(getContext().getString(R.string.change_pass_txt));
        firstEditText.setHint(getContext().getString(R.string.new_pass_txt));
        changeInfoTxt.setText(getContext().getString(R.string.change_pass_info));
        emailEditText.setVisibility(GONE);
        firstEditText.setHint(getContext().getString(R.string.new_pass_txt));
        secondEditText.setHint(getContext().getString(R.string.repeat_pass_txt));
        secondEditText.setVisibility(VISIBLE);
        firstEditText.setVisibility(VISIBLE);
        firstEditText.setText("");
        secondEditText.setText("");
        animateIn();
    }

    public void animateInEmail() {
        passwordMode = false;
        headerTextView.setText(getContext().getString(R.string.change_email_txt));
        emailEditText.setHint(getContext().getString(R.string.new_email_txt));
        changeInfoTxt.setText(getContext().getString(R.string.change_email_info));
        secondEditText.setVisibility(GONE);
        firstEditText.setVisibility(GONE);
        emailEditText.setVisibility(VISIBLE);
        emailEditText.setText(DataHelper.getEmail());
        animateIn();
    }

    public void animateIn() {
        setVisibility(VISIBLE);

        (new Handler()).postDelayed(new Runnable() {

            public void run() {
                firstEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                firstEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 100);
    }

    public void animateOut() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(firstEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(secondEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
        setVisibility(GONE);
        firstError.setVisibility(GONE);
        secondError.setVisibility(GONE);
    }

    public void save() {
        if (passwordMode) {
            if (passwordIsValid()) {
                changePassword();
            }
        } else {
            changePassword();
        }
    }

    public void changePassword() {
        userModel = new UserModel();
        userModel.setId(AuthHelper.getUserId());
        if (passwordMode) {
            userModel.setShouldRepeatPassword(true);
            userModel.setPassword(firstEditText.getText().toString());
            userModel.setPasswordAgain(secondEditText.getText().toString());
        } else {
            userModel.setEmail(firstEditText.getText().toString());
        }

        loadingView.startAnimate();
        saveBtn.setVisibility(GONE);

        userModel.update(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                saveBtn.setVisibility(VISIBLE);
                loadingView.stopAnimation();
                if (errorModel == null) {
                    //Success
                    if (!passwordMode) {
                        DataHelper.storeEmail(emailEditText.getText().toString());
                    } else {
                        firstEditText.setText("");
                        secondEditText.setText("");
                    }
                    messageView.setMessageTypeSuccess();
                    messageView.setMessage(getContext().getString(passwordMode ? R.string.pass_saved : R.string.email_saved));
                    messageView.animateIn();
                } else {
                    if (errorModel.errorForKey("email") != null) {
                        showError(errorModel.errorForKey("email"), firstError);
                    }
                    if (errorModel.errorForKey("password") != null) {
                        showError(errorModel.errorForKey("password"), firstError);
                    }
                    if (errorModel.errorForKey("passwordAgain") != null) {
                        showError(errorModel.errorForKey("passwordAgain"), secondError);
                    }
                }
            }
        });
    }

    private boolean passwordIsValid() {
        if (firstEditText.getText().toString().length() > 0 && secondEditText.getText().toString().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void showError(String error, TextView errorView) {
        errorView.setVisibility(VISIBLE);
        errorView.setText(error);
    }

    private void removeErrors() {
        firstError.setVisibility(GONE);
        secondError.setVisibility(GONE);
    }
}
