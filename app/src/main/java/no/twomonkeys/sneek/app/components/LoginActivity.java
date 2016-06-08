package no.twomonkeys.sneek.app.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.googlecode.mp4parser.authoring.Edit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.KeyboardUtil;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;
import no.twomonkeys.sneek.app.shared.models.UserSession;
import no.twomonkeys.sneek.app.shared.views.CustomNumberPicker;
import no.twomonkeys.sneek.app.shared.views.LoadingView;

/**
 * Created by simenlie on 01.06.16.
 */
public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    RelativeLayout usernameScreen;
    FrameLayout passwordScreen;
    Button loginBtn, registerBtn;

    EditText usernameEditText;
    RelativeLayout rl;
    boolean keyboardSet;
    TextView loginErrorTxt;
    LoadingView loadingView;
    Button loginBackBtn;
    String username;
    String password;
    boolean isRegistering;
    RelativeLayout ageScreen;
    CustomNumberPicker customNumberPicker;
    ArrayList<String> pickerValues;
    Button kissBtn;
    UserModel userModel;
    UserSession userSession;
    LoadingView mainLoadingView;

    private enum LoginState {
        USERNAME, PASSWORD, AGE
    }

    LoginState loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataHelper.setContext(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Remove top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        rl = (RelativeLayout) findViewById(R.id.rootLogin);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        usernameScreen = (RelativeLayout) findViewById(R.id.usernameScreen);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        loginErrorTxt = (TextView) findViewById(R.id.loginErrorTxt);
        loadingView = (LoadingView) findViewById(R.id.loginLoadingView);
        loginBackBtn = (Button) findViewById(R.id.loginBackBtn);
        loadingView.removeBg();
        loadingView.setVisibility(View.INVISIBLE);
        loginBackBtn.setVisibility(View.INVISIBLE);

        mainLoadingView = (LoadingView) findViewById(R.id.mainLoadingView);
        mainLoadingView.removeBg();
        mainLoadingView.setVisibility(View.INVISIBLE);

        ageScreen = (RelativeLayout) findViewById(R.id.ageScreen);
        ageScreen.setVisibility(View.INVISIBLE);

        kissBtn = (Button) findViewById(R.id.kissBtn);

        kissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Register here
                loginRegister();
            }
        });

        enableKiss(0);

        customNumberPicker = (CustomNumberPicker) findViewById(R.id.customNumberPicker);
        customNumberPicker.setMinValue(14);
        customNumberPicker.setMaxValue(100);
        customNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        pickerValues = new ArrayList<>();
        pickerValues.add("SNEEK WANT AGE");
        for (int i = 14; i < 101; i++) {
            pickerValues.add("" + i);
        }

        customNumberPicker.setMinValue(0);
        customNumberPicker.setMaxValue(pickerValues.size() - 1);
        customNumberPicker.setWrapSelectorWheel(false);

        String[] stringArray = pickerValues.toArray(new String[0]);
        customNumberPicker.setDisplayedValues(stringArray);
        customNumberPicker.setValue(0);
        customNumberPicker.setFormatter(new CustomNumberPicker.Formatter() {
            @Override
            public String format(int value) {
                // TODO Auto-generated method stub
                return pickerValues.get(value);
            }
        });

        customNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                enableKiss(newVal);
            }
        });


        checkButtonsEnable();

        loginState = LoginState.USERNAME;
        loginErrorTxt.setVisibility(View.INVISIBLE);


        loginBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backwards();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (loginState) {
                    case USERNAME: {
                        isRegistering = true;
                        goToPassword();
                        break;
                    }
                    case PASSWORD: {
                        if (isRegistering) {
                            goToAge();
                        } else {
                            Log.v(TAG, "logging in");
                            password = usernameEditText.getText().toString();
                            loginRegister();
                        }

                        break;
                    }
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (loginState) {
                    case USERNAME: {
                        isRegistering = false;
                        forward();
                        break;
                    }
                    case PASSWORD: {
                        //Login here

                        break;
                    }
                }
            }
        });

        //initialize the KeyboardUtil (you can do this global)

        KeyboardUtil keyboardUtil = new KeyboardUtil(this, rl);

//enable it
        keyboardUtil.enable();

//disable it
        //keyboardUtil.disable();


        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Log.v(TAG, "TExt before changed");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Log.v(TAG, "TExt changed");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.v(TAG, "TExt after changed");
                removeErrors();
                checkButtonsEnable();
            }
        });

        int width = UIHelper.screenWidth(this);
        loginBtn.setWidth(width / 2);
        loginBtn.setX(0);
        registerBtn.setWidth(width / 2);
        registerBtn.setX(0);


        (new Handler()).postDelayed(new Runnable() {

            public void run() {

                usernameEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                usernameEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        keyboardSet = true;
                    }
                }, 200);

            }
        }, 100);

    }

    public void enableKiss(int pickerValue) {
        if (pickerValue != 0) {
            kissBtn.setEnabled(true);
            kissBtn.setAlpha(1.0f);
        } else {
            kissBtn.setEnabled(false);
            kissBtn.setAlpha(0.5f);
        }
    }

    public void goToPassword() {
        //Try to register here
        final Activity self = this;
        removeErrors();
        loadingView.setVisibility(View.VISIBLE);
        loadingView.startAnimate();
        ErrorModel errorModel = validateUsername();
        if (errorModel == null) {
            //Try to login  here
            UserModel.exists(usernameEditText.getText().toString(), new SimpleCallback() {
                @Override
                public void callbackCall(ErrorModel errorModel) {
                    //Handle error here at some time

                }
            }, new UserModel.UserExistsCallback() {
                @Override
                public void exists(boolean exists) {
                    loadingView.stopAnimation();
                    if (exists) {
                        ErrorModel errorModel = new ErrorModel(self);
                        errorModel.addError("validation_username_exists", "username");
                        loginErrorTxt.setVisibility(View.VISIBLE);
                        loginErrorTxt.setText(errorModel.errorForKey("username"));
                    } else {
                        //Can go to next step
                        forward();
                    }
                }
            });

        } else {
            //There were errrors
            loadingView.stopAnimation();
            loginErrorTxt.setVisibility(View.VISIBLE);
            loginErrorTxt.setText(errorModel.errorForKey("username"));
        }
    }

    public void goToAge() {
        if (isRegistering) {
            forward();
        } else {
            //Try to login
        }
    }

    public boolean isPasswordValid() {
        if (usernameEditText.getText().toString().length() > 5 && usernameEditText.getText().toString().length() < 21) {
            return true;
        } else {
            return false;
        }
    }

    public void toggleState() {
        ageScreen.setVisibility(View.INVISIBLE);
        registerBtn.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
        usernameEditText.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
        switch (loginState) {
            case USERNAME: {
                usernameEditText.setHint("username");
                loginBtn.setText("Login");
                registerBtn.setText("Register");
                break;
            }
            case PASSWORD: {
                usernameEditText.setHint("password");
                loginBtn.setText("Forgot");
                registerBtn.setText("I'm ready");
                break;
            }
            case AGE: {
                ageScreen.setVisibility(View.VISIBLE);
                registerBtn.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);
                usernameEditText.setVisibility(View.INVISIBLE);
                imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);
                usernameEditText.clearFocus();
                try {
                    Method method = customNumberPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
                    method.setAccessible(true);
                    method.invoke(customNumberPicker, true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


                break;
            }
        }
    }

    public void forward() {
        switch (loginState) {
            case USERNAME: {
                username = usernameEditText.getText().toString();
                loginState = LoginState.PASSWORD;
                loginBackBtn.setVisibility(View.VISIBLE);
                break;
            }
            case PASSWORD: {
                password = usernameEditText.getText().toString();
                loginState = LoginState.AGE;
                break;
            }
            case AGE: {
                //LOgin/Register to mainactivity
                break;
            }
        }
        usernameEditText.setText("");
        toggleState();
    }

    public void backwards() {
        switch (loginState) {
            case USERNAME: {
                //Cant go back
                break;
            }
            case PASSWORD: {
                Log.v("pas", "ss " + username);
                loginBackBtn.setVisibility(View.INVISIBLE);
                loginState = LoginState.USERNAME;
                usernameEditText.setText(username);
                break;
            }
            case AGE: {
                loginState = LoginState.PASSWORD;
                usernameEditText.setText(password);
                break;
            }
        }
        toggleState();
    }

    public void removeErrors() {
        loginErrorTxt.setVisibility(View.INVISIBLE);
    }

    public void checkButtonsEnable() {
        boolean enable = false;

        if (usernameEditText.getText().toString().length() > 0 && loginState == LoginState.USERNAME) {
            enable = true;
        } else if (isPasswordValid() && loginState == LoginState.PASSWORD) {
            enable = true;
        }
        if (enable) {
            registerBtn.setAlpha(1.0f);
            registerBtn.setEnabled(true);
            if (loginState == LoginState.USERNAME) {
                loginBtn.setEnabled(true);
                loginBtn.setAlpha(1.0f);
            }
        } else {
            registerBtn.setEnabled(false);
            registerBtn.setAlpha(0.5f);
            if (loginState == LoginState.USERNAME) {
                loginBtn.setEnabled(false);
                loginBtn.setAlpha(0.5f);
            }

        }
    }

    public ErrorModel validateUsername() {
        String usernameTxt = usernameEditText.getText().toString();
        ErrorModel errorModel = new ErrorModel(this);
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(usernameTxt);
        boolean found = matcher.find();
        if (found) {
            errorModel.addError("validation_username_space", "username");
            return errorModel;
        }
        if (usernameTxt.length() > 3 && usernameTxt.length() < 16) {
            //length is valid
        } else {
            //Contains 3 characters - not valid
            errorModel.addError("validation_username_length", "username");
            return errorModel;
        }

        Pattern p = Pattern.compile("[^a-zA-Z0-9_]");
        boolean hasSpecialChar = p.matcher(usernameTxt).find();

        if (hasSpecialChar) {
            errorModel.addError("validation_username_alphanumeric", "username");
            return errorModel;
        }

        return null;
    }


    public void storeAndShowSession() {
        // [DataHelper storeUsername:viewModel.username];
        if (isRegistering) {

        } else {

        }
    }

    public int yearBorn() {
        int yearNow = Calendar.getInstance().get(Calendar.YEAR);
        int yearBorn = yearNow - Integer.parseInt(pickerValues.get(customNumberPicker.getValue()));
        return yearBorn;
    }

    public void loginRegister() {
        mainLoadingView.setVisibility(View.VISIBLE);
        mainLoadingView.startAnimate();

        userModel = new UserModel();
        userModel.setUsername(username);
        userModel.setPassword(password);

        if (isRegistering) {
            Log.v(TAG, "GOT HERE");
            userModel.setYear_born(yearBorn());
            userModel.save(new SimpleCallback() {
                @Override
                public void callbackCall(ErrorModel errorModel) {
                    onResponseRecieved(errorModel);
                }
            });
        } else {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);
            usernameEditText.clearFocus();
            userSession = new UserSession();
            userSession.setUserModel(userModel);
            userSession.save(new SimpleCallback() {
                @Override
                public void callbackCall(ErrorModel errorModel) {
                    onResponseRecieved(errorModel);
                }
            });
        }
    }

    public void onResponseRecieved(ErrorModel errorModel) {
        if (errorModel == null) {
            if (isRegistering) {
                Log.v("Storing token","Storing reg");
                DataHelper.storeCredentials(userModel.getUserSession().getAuth_token(), userModel.getId());

            } else {
                Log.v("Storing token","Storing log");
                DataHelper.storeCredentials(userSession.getAuth_token(), userSession.getUser_id());
            }
            showMain();
        } else {
            if (errorModel.errorForKey("password") != null) {
                showError(errorModel.errorForKey("password"));
            }
        }
    }

    public void showError(String errorString) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
        mainLoadingView.stopAnimation();
        loginErrorTxt.setVisibility(View.VISIBLE);
        loginErrorTxt.setText(errorString);
    }

    public void showMain() {
        Intent getMainScreenIntent = new Intent(this, MainActivity.class);
        final int result = 1;

        getMainScreenIntent.putExtra("loginActivity", "MainActivity");
        mainLoadingView.stopAnimation();
        startActivity(getMainScreenIntent);
        //startActivityForResult(getMainScreenIntent, result);
    }

}
