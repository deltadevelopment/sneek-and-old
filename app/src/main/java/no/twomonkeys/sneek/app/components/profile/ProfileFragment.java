package no.twomonkeys.sneek.app.components.profile;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.Callback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
/**
 * Created by Sondre on 30.09.2016.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {
    public TextView profileHomeTxt;
    public TextView profileYouTxt;
    public TextView profileSettingsTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_top_fragment, container, false);
        profileHomeTxt = (TextView) view.findViewById(R.id.profileHomeTxt);
        profileYouTxt = (TextView) view.findViewById(R.id.profileYouTxt);
        profileSettingsTxt = (TextView) view.findViewById(R.id.profileSettingsTxt);
        return view;
    }
}
