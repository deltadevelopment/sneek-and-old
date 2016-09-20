package no.twomonkeys.sneek.app.components.feed;

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
public class BottomBarFragment extends android.support.v4.app.Fragment {
    public TextView whatsupText;
    private Button camBtn;
    public SimpleCallback2 onMoreClb;
    public SimpleCallback2 onCameraClb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottombar_fragment, container, false);
        whatsupText = (TextView) view.findViewById(R.id.whatsupText);
        camBtn = (Button) view.findViewById(R.id.camBtn);
        whatsupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMoreClb.callbackCall();
            }
        });
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClb.callbackCall();
            }
        });


        return view;
    }
}
