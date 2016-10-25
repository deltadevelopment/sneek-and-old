package no.twomonkeys.sneek.app.components.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.block.BlockController;
import no.twomonkeys.sneek.app.components.change.ChangeController;
import no.twomonkeys.sneek.app.components.settings.SettingsAdapter;
import no.twomonkeys.sneek.app.components.web.WebController;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.AuthHelper;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.SettingsModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;

/**
 * Created by simenlie on 14.06.16
 */
public class SettingsActivity extends Activity {
    ImageButton backBtn;
    SimpleDraweeView profilePicture;
    ProgressBar profilePictureProgress;
    ListView accountList, helpList;
    TextView accountHeadView, helpHeadView;
    BlockController blockController;
    ChangeController changeController;
    WebController webController;
    Button deleteAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.settings_activity);
        final ArrayList<SettingsModel> accountListArray = new ArrayList<>();
        ArrayList<SettingsModel> helpListArray = new ArrayList<>();

        accountListArray.add(new SettingsModel("Username", SettingsModel.SettingsType.INFORMATION));
        accountListArray.add(new SettingsModel("Change password", SettingsModel.SettingsType.NAVIGATION));
        accountListArray.add(new SettingsModel("Change email", SettingsModel.SettingsType.NAVIGATION));
        accountListArray.add(new SettingsModel("Blocked users", SettingsModel.SettingsType.NAVIGATION));
        accountListArray.add(new SettingsModel("Log out", SettingsModel.SettingsType.POPUP));

        helpListArray.add(new SettingsModel("Help", SettingsModel.SettingsType.NAVIGATION));
        helpListArray.add(new SettingsModel("Privacy Policy", SettingsModel.SettingsType.NAVIGATION));
        helpListArray.add(new SettingsModel("Terms of Service", SettingsModel.SettingsType.NAVIGATION));


        profilePicture = (SimpleDraweeView) findViewById(R.id.profilePicture);
        profilePictureProgress = (ProgressBar) findViewById(R.id.profilePictureProgress);
        backBtn = (ImageButton) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        accountList = (ListView) findViewById(R.id.accountList);
        helpList = (ListView) findViewById(R.id.helpList);


        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header, accountList, false);
        accountList.addHeaderView(header, null, false);

        accountHeadView = (TextView) header.findViewById(R.id.headerView);
        accountHeadView.setText("ACCOUNT");
        accountList.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ListAdapter listAdapter = new SettingsAdapter(this, accountListArray);
        accountList.setAdapter(listAdapter);

        ViewGroup header2 = (ViewGroup) inflater.inflate(R.layout.header, helpList, false);
        helpList.addHeaderView(header2, null, false);
        helpList.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        helpHeadView = (TextView) header2.findViewById(R.id.headerView);
        helpHeadView.setText("MORE");

        ListAdapter listAdapter2 = new SettingsAdapter(this, helpListArray);
        helpList.setAdapter(listAdapter2);

       // UIHelper.layoutBtnRelative(this, backBtn, "BACK");

        accountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int indexPostion = position - 1;
                SettingsModel settingsModel = accountListArray.get(indexPostion);
                Log.v("tag", "position " + indexPostion);
                if (indexPostion == 1) {
                    changeController.animateInPassword();
                } else if (indexPostion == 2) {
                    //swap to email here
                    changeController.animateInEmail();
                }
                if (indexPostion == 3) {
                    blockController.animateIn();
                }
                if (indexPostion == 4) {
                    popLogOutScreen();
                }
            }
        });

        helpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int indexPostion = position - 1;
                webController.loadView(indexPostion);
                webController.animateIn();
            }
        });

        blockController = (BlockController) findViewById(R.id.blockController);
        blockController.setVisibility(View.GONE);

        changeController = (ChangeController) findViewById(R.id.changeController);
        changeController.setVisibility(View.GONE);

        webController = (WebController) findViewById(R.id.webController);
        webController.setVisibility(View.GONE);


        setListViewHeightBasedOnChildren(helpList);
        setListViewHeightBasedOnChildren(accountList);

        deleteAccountBtn = (Button) findViewById(R.id.deleteAccountBtn);
        final SettingsActivity self = this;

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(self)
                        .setTitle(R.string.delete_confirm_title)
                        .setMessage(R.string.delete_confirm_msg)
                        .setPositiveButton(R.string.delete_btn, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                DataHelper.startActivity.logout();
                                UserModel userModel = new UserModel();
                                userModel.setId(AuthHelper.getUserId());
                                userModel.delete(new SimpleCallback() {
                                    @Override
                                    public void callbackCall(ErrorModel errorModel) {
                                        DataHelper.startActivity.logout();
                                    }
                                });
                                //Reset different stuff here, username, token, user_id, location?
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
        profilePicture.setVisibility(View.INVISIBLE);
        profilePictureProgress.getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        final UserModel userModel = new UserModel();
            userModel.setId(96);
        userModel.fetch(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                Log.v("USERNAME","usernm " + userModel.getUsername());
                DataHelper.storeUsername(userModel.getUsername());


                if (userModel.getProfile_picture_key() != null) {
                    userModel.loadPhoto(profilePicture, new SimpleCallback2() {
                        @Override
                        public void callbackCall() {
                            profilePictureProgress.setVisibility(View.INVISIBLE);
                            profilePicture.setVisibility(View.VISIBLE);
                        }

                    });

                }
                else {
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.circle);
                    profilePicture.setImageDrawable(drawable);
                    profilePictureProgress.setVisibility(View.INVISIBLE);
                    profilePicture.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    public void popLogOutScreen() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.log_out_h)
                .setMessage(R.string.log_out_b)
                .setPositiveButton(R.string.ok_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        DataHelper.startActivity.logout();
                        //Reset different stuff here, username, token, user_id, location?
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
