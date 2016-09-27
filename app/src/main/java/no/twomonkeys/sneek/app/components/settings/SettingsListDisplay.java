package no.twomonkeys.sneek.app.components.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import no.twomonkeys.sneek.R;

/**
 * Created by Sondre on 27.09.2016.
 */

public class SettingsListDisplay extends Activity {
    String[] settingsArray = {"Username" + "   test","Change password","Change email","Blocked users","Log out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.settings_view, settingsArray);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}



