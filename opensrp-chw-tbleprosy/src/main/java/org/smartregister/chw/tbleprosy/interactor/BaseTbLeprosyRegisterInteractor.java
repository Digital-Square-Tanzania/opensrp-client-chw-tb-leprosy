package org.smartregister.chw.tbleprosy.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.tbleprosy.contract.TbLeprosyRegisterContract;
import org.smartregister.chw.tbleprosy.util.AppExecutors;
import org.smartregister.chw.tbleprosy.util.TbLeprosyUtil;

public class BaseTbLeprosyRegisterInteractor implements TbLeprosyRegisterContract.Interactor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    BaseTbLeprosyRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseTbLeprosyRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveRegistration(final String jsonString, final TbLeprosyRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            try {
                TbLeprosyUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved());
        };
        appExecutors.diskIO().execute(runnable);
    }
}
