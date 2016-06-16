package no.twomonkeys.sneek.app.components.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.block.BlockController;
import no.twomonkeys.sneek.app.components.change.ChangeController;
import no.twomonkeys.sneek.app.components.settings.SettingsAdapter;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.SettingsModel;

/**
 * Created by simenlie on 14.06.16.
 */
public class SettingsActivity extends Activity {
    Button backBtn;
    ListView accountList, helpList;
    TextView accountHeadView, helpHeadView;
    BlockController blockController;
    ChangeController changeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        backBtn = (Button) findViewById(R.id.backBtn);
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

        ListAdapter listAdapter = new SettingsAdapter(this, accountListArray);
        accountList.setAdapter(listAdapter);

        ViewGroup header2 = (ViewGroup) inflater.inflate(R.layout.header, helpList, false);
        helpList.addHeaderView(header2, null, false);

        helpHeadView = (TextView) header2.findViewById(R.id.headerView);
        helpHeadView.setText("SCROLL OF DEATH");

        ListAdapter listAdapter2 = new SettingsAdapter(this, helpListArray);
        helpList.setAdapter(listAdapter2);

        UIHelper.layoutBtnRelative(this, backBtn, "BACK");

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

            }
        });

        blockController = (BlockController) findViewById(R.id.blockController);
        blockController.setVisibility(View.GONE);

        changeController = (ChangeController) findViewById(R.id.changeController);
        changeController.setVisibility(View.GONE);
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


}
