package org.smartregister.chw.tbleprosy.contract;

import android.content.Context;

public interface BaseTbLeprosyCallDialogContract {

    interface View {
        void setPendingCallRequest(Dialer dialer);
        Context getCurrentContext();
    }

    interface Dialer {
        void callMe();
    }
}
