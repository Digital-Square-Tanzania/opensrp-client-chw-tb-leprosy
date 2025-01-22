package org.smartregister.chw.tbleprosy.listener;


import android.view.View;

import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.fragment.BaseTbLeprosyCallDialogFragment;

public class BaseTbLeprosyCallWidgetDialogListener implements View.OnClickListener {

    private BaseTbLeprosyCallDialogFragment callDialogFragment;

    public BaseTbLeprosyCallWidgetDialogListener(BaseTbLeprosyCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tbleprosy_call_close) {
            callDialogFragment.dismiss();
        }
    }
}
