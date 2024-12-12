package org.smartregister.chw.tbleprosy.custom_views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.fragment.BaseTbLeprosyCallDialogFragment;
import org.smartregister.chw.tbleprosy.R;

public class BaseTbLeprosyFloatingMenu extends LinearLayout implements View.OnClickListener {
    private MemberObject MEMBER_OBJECT;

    public BaseTbLeprosyFloatingMenu(Context context, MemberObject MEMBER_OBJECT) {
        super(context);
        initUi();
        this.MEMBER_OBJECT = MEMBER_OBJECT;
    }

    protected void initUi() {
        inflate(getContext(), R.layout.view_tbleprosy_floating_menu, this);
        FloatingActionButton fab = findViewById(R.id.tbleprosy_fab);
        if (fab != null)
            fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tbleprosy_fab) {
            Activity activity = (Activity) getContext();
            BaseTbLeprosyCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }  else if (view.getId() == R.id.refer_to_facility_layout) {
            Activity activity = (Activity) getContext();
            BaseTbLeprosyCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }
    }
}