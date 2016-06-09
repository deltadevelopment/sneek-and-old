package no.twomonkeys.sneek.app.shared.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;

/**
 * Created by simenlie on 10.06.16.
 */
public class ReportView extends RelativeLayout {

    Button reportUserBtn, reportMomentBtn, backBtn;
    public int momentId, userId;

    public ReportView(Context context) {
        super(context);
        initializeViews(context);
    }

    public ReportView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.

        reportMomentBtn = (Button) findViewById(R.id.reportMoment);
        reportUserBtn = (Button) findViewById(R.id.reportUser);
        backBtn = (Button) findViewById(R.id.reportBackBtn);

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(INVISIBLE);
            }
        });
        reportMomentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.report_moment_h)
                        .setMessage(R.string.report_moment_b)
                        .setPositiveButton(R.string.ok_txt, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MomentModel momentModel = new MomentModel();
                                momentModel.setId(momentId);
                                momentModel.getMomentFlagModel().save(new SimpleCallback() {
                                    @Override
                                    public void callbackCall(ErrorModel errorModel) {
                                        //Reported moment
                                    }
                                });
                                // continue with delete

                            }
                        })
                        .setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        reportUserBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.report_user_h)
                        .setMessage(R.string.report_user_b)
                        .setPositiveButton(R.string.ok_txt, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                UserModel userModel = new UserModel();
                                userModel.setId(userId);
                                userModel.getUserFlagModel().save(new SimpleCallback() {
                                    @Override
                                    public void callbackCall(ErrorModel errorModel) {
                                        //reported user
                                    }
                                });

                            }
                        })
                        .setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        UIHelper.layoutBtnRelative(getContext(), backBtn, "BACK");
        UIHelper.layoutBtn(getContext(), reportUserBtn, "REPORT USER");
        UIHelper.layoutBtn(getContext(), reportMomentBtn, "REPORT MOMENT");
    }
}
