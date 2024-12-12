package org.smartregister.chw.tbleprosy.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.tbleprosy.contract.TbLeprosyProfileContract;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.util.AppExecutors;
import org.smartregister.chw.tbleprosy.util.TbLeprosyUtil;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public class BaseTbLeprosyProfileInteractor implements TbLeprosyProfileContract.Interactor {
    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseTbLeprosyProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseTbLeprosyProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, TbLeprosyProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            callback.refreshMedicalHistory(true);
            callback.refreshUpComingServicesStatus("TbLeprosy Visit", AlertStatus.normal, new Date());
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final String jsonString, final TbLeprosyProfileContract.InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                TbLeprosyUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appExecutors.diskIO().execute(runnable);
    }
}
