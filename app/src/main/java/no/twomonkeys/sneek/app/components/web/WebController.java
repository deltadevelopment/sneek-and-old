package no.twomonkeys.sneek.app.components.web;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.Configuration;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 16.06.16.
 */
public class WebController extends RelativeLayout {

    WebView webView;
    ImageButton backbtn;
    TextView titleView;
    String webUrl;
    public SimpleCallback2 scb2;

    public WebController(Context context) {
        super(context);
        initializeViews(context);
    }

    public WebController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public WebController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.web_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        titleView = (TextView) findViewById(R.id.titleView);
        backbtn = (ImageButton) findViewById(R.id.backBtnWeb);
        //UIHelper.layoutBtnRelative(getContext(), backbtn, "BACK");
        backbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateOut();
            }
        });
        webView = (WebView) findViewById(R.id.webView);
    }

    public void loadView(int type) {
        String title = "";
        switch (type) {
            case 0: {
                title = "Help";
                webUrl = "help.html";
                break;
            }
            case 1: {
                title = "Privacy Policy";
                webUrl = "privacy.html";
                break;
            }
            case 2: {
                title = "Terms of Service";
                webUrl = "terms.html";
                break;
            }
            case 3: {
                title = "Forgot password";
                webUrl = "forgot_password.html";
                break;
            }
        }
        titleView.setText(title);
    }

    public void animateIn() {
        webView.loadUrl(Configuration.WEB_ENDPOINT + webUrl);
        setVisibility(VISIBLE);
    }

    public void animateOut() {
        setVisibility(GONE);
        if (scb2 != null) {
            scb2.callbackCall();
        }
    }

}
