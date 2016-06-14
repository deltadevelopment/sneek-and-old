package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

import no.twomonkeys.sneek.app.shared.SimpleCallback;

/**
 * Created by simenlie on 14.06.16.
 */
public class ViewsModel extends CRUDModel {
    int view_count;
    @Override
    void build(Map map) {

    }

    public void fetch(SimpleCallback scb)
    {

    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }
}
