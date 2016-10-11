package no.twomonkeys.sneek.app.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.MainActivity;
import no.twomonkeys.sneek.app.components.settings.SettingsActivity;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;

/**
 * Created by simenlie on 01.06.16.
 */
public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_activity);

        /*
//Should be in the class that gets called
        Intent activityThaCalled = getIntent();
        String previousAcitvity = activityThaCalled.getExtras().getString("callingActivity");
        */
        DataHelper.setStartActivity(this);
        DataHelper.setContext(this);
        /*
        if (DataHelper.getAuthToken() == null) {
            openLoginActivity();
        } else {
            openMainActivity();
        }
*/

        Intent getMainScreenIntent = new Intent(this, SettingsActivity.class);
        final int result = 1;

        getMainScreenIntent.putExtra("callingActivity", "SettingsAcvtivity");

        startActivity(getMainScreenIntent);

    }

    public void logout() {
        DataHelper.storeCredentials(null, 0);
        openLoginActivity();
    }

    private void openLoginActivity() {
        Intent getMainScreenIntent = new Intent(this, LoginActivity.class);
        final int result = 1;

        getMainScreenIntent.putExtra("callingActivity", "StartActivity");

        startActivity(getMainScreenIntent);
        //startActivityForResult(getMainScreenIntent, result);
    }

    private void openMainActivity() {
        Intent getMainScreenIntent = new Intent(this, MainActivity.class);
        final int result = 1;

        getMainScreenIntent.putExtra("callingActivity", "MainActivity");

        startActivity(getMainScreenIntent);
        //startActivityForResult(getMainScreenIntent, result);
    }
}
