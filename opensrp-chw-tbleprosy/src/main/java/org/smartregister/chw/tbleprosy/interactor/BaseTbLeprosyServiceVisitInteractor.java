package org.smartregister.chw.tbleprosy.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.TbLeprosyLibrary;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosyHtsActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosyMedicalHistoryActionHelper;
import org.smartregister.chw.tbleprosy.contract.BaseTbLeprosyVisitContract;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.chw.tbleprosy.model.BaseTbLeprosyVisitAction;
import org.smartregister.chw.tbleprosy.util.AppExecutors;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseTbLeprosyServiceVisitInteractor extends BaseTbLeprosyVisitInteractor {

    protected BaseTbLeprosyVisitContract.InteractorCallBack callBack;

    String visitType;
    private final TbLeprosyLibrary tbLeprosyLibrary;
    private final LinkedHashMap<String, BaseTbLeprosyVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;


    @VisibleForTesting
    public BaseTbLeprosyServiceVisitInteractor(AppExecutors appExecutors, TbLeprosyLibrary TbLeprosyLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.tbLeprosyLibrary = TbLeprosyLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseTbLeprosyServiceVisitInteractor(String visitType) {
        this(new AppExecutors(), TbLeprosyLibrary.getInstance(), TbLeprosyLibrary.getInstance().getEcSyncHelper());
        this.visitType = visitType;
    }

    @Override
    protected String getCurrentVisitType() {
        if (StringUtils.isNotBlank(visitType)) {
            return visitType;
        }
        return super.getCurrentVisitType();
    }

    @Override
    protected void populateActionList(BaseTbLeprosyVisitContract.InteractorCallBack callBack) {
        this.callBack = callBack;
        final Runnable runnable = () -> {
            try {
                evaluateTbLeprosySource(details);
                evaluateTbLeprosyObservation(details);
                evaluateTbLeprosySample(details);

            } catch (BaseTbLeprosyVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateTbLeprosySource(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosyMedicalHistoryActionHelper actionHelper = new TbLeprosyMedicalHistory(mContext, memberObject);
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_source))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_SOURCE)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_source), action);

    }

    private void evaluateTbLeprosyObservation(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosyPhysicalExamActionHelper actionHelper = new TbLeprosyPhysicalExamActionHelper(mContext, memberObject);
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_investigation))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_INVESTIGATION)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_investigation), action);
    }

    private void evaluateTbLeprosySample(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosyHtsActionHelper actionHelper = new TbLeprosyHtsActionHelper(mContext, memberObject);
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_sample))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_SAMPLE)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_sample), action);
    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.TB_LEPROSY_SERVICES;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.TBLEPROSY_SERVICES;
    }

    private class TbLeprosyMedicalHistory extends TbLeprosyMedicalHistoryActionHelper {

        public TbLeprosyMedicalHistory(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateTbLeprosyObservation(details);
                    evaluateTbLeprosySample(details);
                } catch (BaseTbLeprosyVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

    private class TbLeprosyPhysicalExamActionHelper extends org.smartregister.chw.tbleprosy.actionhelper.TbLeprosyPhysicalExamActionHelper {
        public TbLeprosyPhysicalExamActionHelper(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }
        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateTbLeprosySample(details);
                } catch (BaseTbLeprosyVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

}
