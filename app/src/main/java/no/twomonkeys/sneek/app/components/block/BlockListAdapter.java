package no.twomonkeys.sneek.app.components.block;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.SettingsModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;

/**
 * Created by simenlie on 14.06.16.
 */
public class BlockListAdapter extends ArrayAdapter<UserModel> {
    TextView usernameTextView;
    Button unblockBtn;

    public interface BlockTapped {
        void onTapped(UserModel userModel);
    }

    public BlockTapped unblockTapped;

    public BlockListAdapter(Context context, ArrayList<UserModel> blockedUsers) {
        super(context, R.layout.settings_row, blockedUsers);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.v("chaning", "CHANING");
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.block_row, parent, false);
        Drawable drawable = view.getBackground();

        usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
        unblockBtn = (Button) view.findViewById(R.id.unblockBtn);
      //  UIHelper.layoutBtnRelativeSize(getContext(), unblockBtn, "Unblock");


        final UserModel userModel = getItem(position);


        unblockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userModel.getBlockModel().delete(new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        if (errorModel != null) {
                            unblockTapped.onTapped(userModel);
                        }
                    }
                });
            }
        });

        usernameTextView.setText(userModel.getUsername());

        return view;
    }

    public void reloadData(ArrayList<UserModel> blockedUsers) {
        notifyDataSetChanged();
    }

}
