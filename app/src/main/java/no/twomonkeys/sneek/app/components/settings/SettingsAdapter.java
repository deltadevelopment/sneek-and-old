package no.twomonkeys.sneek.app.components.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.models.SettingsModel;

/**
 * Created by simenlie on 14.06.16.
 */
public class SettingsAdapter extends ArrayAdapter<SettingsModel> {

    TextView titleTextView, summaryTextView;

    public SettingsAdapter(Context context, ArrayList<SettingsModel> settingsModels) {
        super(context, R.layout.settings_row, settingsModels);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.settings_row, parent, false);

        SettingsModel settingsModel = getItem(position);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(settingsModel.getTitle());
        summaryTextView = (TextView) view.findViewById(R.id.summaryTextView);

        if (settingsModel.getType() == SettingsModel.SettingsType.INFORMATION) {
            summaryTextView.setText(DataHelper.getUsername());
            summaryTextView.setVisibility(View.VISIBLE);
        } else {
            summaryTextView.setVisibility(View.GONE);
        }

        return view;
    }
}
