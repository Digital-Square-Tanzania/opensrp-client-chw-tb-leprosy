package org.smartregister.chw.tbleprosy.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.TbLeprosyLibrary;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosySampleActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosyInvestigationActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosySourceActionHelper;
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
    private final LinkedHashMap<String, BaseTbLeprosyVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;


    @VisibleForTesting
    public BaseTbLeprosyServiceVisitInteractor(AppExecutors appExecutors, TbLeprosyLibrary TbLeprosyLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.mContext = TbLeprosyLibrary.getInstance().context().applicationContext();
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

    /**
     * this method is used to list all actions
     * @param callBack
     */
    @Override
    protected void populateActionList(BaseTbLeprosyVisitContract.InteractorCallBack callBack) {
        this.callBack = callBack;
        final Runnable runnable = () -> {
            try {
                evaluateTbLeprosySource(details);
                evaluateTbLeprosyInvestigation(details);
                evaluateTbLeprosySample(details);

            } catch (BaseTbLeprosyVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    /**
     * this action deals Type of Contact (Njia ya kuchunguza Kifua Kikuu)
     * @param details
     * @throws BaseTbLeprosyVisitAction.ValidationException
     */
    private void evaluateTbLeprosySource(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosySourceActionHelper actionHelper = new TbLeprosySourceActionHelper(mContext, memberObject);
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_source))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_SOURCE)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_source), action);

    }

    /**
     * this action deals with TB Investigation (Uchunguzi wa Kifua Kikuu)
     * @param details
     * @throws BaseTbLeprosyVisitAction.ValidationException
     */
    private void evaluateTbLeprosyInvestigation(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosyInvestigationActionHelper actionHelper = new TbLeprosyInvestigationActionHelper(mContext, memberObject);
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_investigation))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_INVESTIGATION)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_investigation), action);
    }

    /**
     * this action deals with sample collection (kuchukua sampuli)
     * @param details
     * @throws BaseTbLeprosyVisitAction.ValidationException
     */
    private void evaluateTbLeprosySample(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosySampleActionHelper actionHelper = new TbLeprosySampleActionHelper(mContext, memberObject);
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




}
