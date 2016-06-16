package no.twomonkeys.sneek.app.components.block;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.settings.SettingsAdapter;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.BlockModel;
import no.twomonkeys.sneek.app.shared.models.SettingsModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;

/**
 * Created by simenlie on 14.06.16.
 */
public class BlockController extends RelativeLayout {

    Button backBtn;
    ListView blockedList;
    ArrayList<UserModel> blockedUsers;
    BlockListAdapter listAdapter;
    TextView blockedInfoTextView;

    public BlockController(Context context) {
        super(context);
        initializeViews(context);
    }

    public BlockController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public BlockController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.block_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        blockedUsers = new ArrayList<>();

        backBtn = (Button) findViewById(R.id.backBtn2);
        UIHelper.layoutBtnRelative(getContext(), backBtn, "BACK");
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateOut();
            }
        });
        blockedList = (ListView) findViewById(R.id.blockedList);

        listAdapter = new BlockListAdapter(getContext(), blockedUsers);
        blockedList.setAdapter(listAdapter);

        blockedInfoTextView = (TextView) findViewById(R.id.blockedInfoTextView);
        blockedList.setVisibility(GONE);

        listAdapter.unblockTapped = new BlockListAdapter.BlockTapped() {
            @Override
            public void onTapped(UserModel userModel) {
                blockedUsers.remove(userModel);
                listAdapter.notifyDataSetChanged();
            }
        };

    }

    private void refreshBlockedUsers() {
        BlockModel.fetchAll(new BlockModel.ArrayCallback() {
            @Override
            public void callbackCall(ArrayList arrayList) {
                blockedUsers.addAll(arrayList);
                if (blockedUsers.size() > 0) {
                    blockedList.setVisibility(VISIBLE);
                    blockedInfoTextView.setVisibility(GONE);
                } else {
                    blockedList.setVisibility(GONE);
                    blockedInfoTextView.setVisibility(VISIBLE);
                }
                blockedList.invalidateViews();
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    public void animateIn() {
        setVisibility(VISIBLE);
        refreshBlockedUsers();
    }

    public void animateOut() {
        blockedUsers.removeAll(blockedUsers);
        setVisibility(GONE);
    }

}
