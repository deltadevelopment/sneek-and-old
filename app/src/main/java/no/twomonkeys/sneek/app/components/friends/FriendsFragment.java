package no.twomonkeys.sneek.app.components.friends;

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
 * Created by Sondre on 20.09.2016.
 */
public class FriendsFragment extends android.support.v4.app.Fragment {

        public TextView friendsHomeBtn;
        public TextView friendsTitleBtn;
        public TextView friendsSearchBtn;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.friends_top_fragment, container, false);
            friendsHomeBtn = (TextView) view.findViewById(R.id.friendsHomeBtn);
            friendsTitleBtn = (TextView) view.findViewById(R.id.friendsTitleBtn);
            friendsSearchBtn = (TextView) view.findViewById(R.id.friendsSearchBtn);
            return view;
        }

}
