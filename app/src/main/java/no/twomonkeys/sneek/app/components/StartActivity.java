package no.twomonkeys.sneek.app.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.MainActivity;

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
        openActivity();
    }

    private void openActivity() {
        Intent getMainScreenIntent = new Intent(this, LoginActivity.class);
        final int result = 1;

        getMainScreenIntent.putExtra("callingActivity", "StartActivity");

        startActivity(getMainScreenIntent);
        //startActivityForResult(getMainScreenIntent, result);
    }
}
