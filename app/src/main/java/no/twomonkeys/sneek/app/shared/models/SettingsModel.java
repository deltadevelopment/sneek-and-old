package no.twomonkeys.sneek.app.shared.models;

/**
 * Created by simenlie on 14.06.16.
 */
public class SettingsModel {

    public enum SettingsType{
        NAVIGATION, INFORMATION, POPUP
    }

    private String title, summary, url;
    private SettingsType type;

    public SettingsModel(String title, SettingsType type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public SettingsType getType() {
        return type;
    }
}
