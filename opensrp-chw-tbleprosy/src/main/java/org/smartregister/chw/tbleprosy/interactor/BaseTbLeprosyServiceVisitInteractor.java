package org.smartregister.chw.tbleprosy.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.TbLeprosyLibrary;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosyInvestigationActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosySampleActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TbLeprosySourceActionHelper;
import org.smartregister.chw.tbleprosy.contract.BaseTbLeprosyVisitContract;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.chw.tbleprosy.model.BaseTbLeprosyVisitAction;
import org.smartregister.chw.tbleprosy.util.AppExecutors;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class BaseTbLeprosyServiceVisitInteractor extends BaseTbLeprosyVisitInteractor {

    protected BaseTbLeprosyVisitContract.InteractorCallBack callBack;

    String visitType;
    private final LinkedHashMap<String, BaseTbLeprosyVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;
    private TbLeprosyInvestigationActionHelper contactTbInvestigationHelper;


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
                evaluateContactTbInvestigation(details);
                evaluateContactLeprosyInvestigation(details);
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
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_INDEX_CLIENT_DETAILS_SOURCE)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_source), action);

    }

    /**
     * this action deals with TB Investigation (Uchunguzi wa Kifua Kikuu)
     * @param details
     * @throws BaseTbLeprosyVisitAction.ValidationException
     */
    private void evaluateContactTbInvestigation(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosyInvestigationActionHelper actionHelper = new TbLeprosyInvestigationActionHelper(mContext, memberObject);
        contactTbInvestigationHelper = actionHelper;
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_contact_tb_investigation))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(getSourceContactTypeValidator("tb"))
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_CONTACT_TB_INVESTIGATION)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_contact_tb_investigation), action);
    }

    /**
     * this action deals with Leprosy Investigation (Uchunguzi wa Ukoma)
     * @param details
     * @throws BaseTbLeprosyVisitAction.ValidationException
     */
    private void evaluateContactLeprosyInvestigation(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosyInvestigationActionHelper actionHelper = new TbLeprosyInvestigationActionHelper(mContext, memberObject);
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_contact_leprosy_investigation))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(getSourceContactTypeValidator("leprosy"))
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_CONTACT_LEPROSY_INVESTIGATION)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_contact_leprosy_investigation), action);
    }

    /**
     * this action deals with sample collection (kuchukua sampuli)
     * @param details
     * @throws BaseTbLeprosyVisitAction.ValidationException
     */
    private void evaluateTbLeprosySample(Map<String, List<VisitDetail>> details) throws BaseTbLeprosyVisitAction.ValidationException {

        TbLeprosySampleActionHelper actionHelper = new TbLeprosySampleActionHelper(mContext, memberObject, contactTbInvestigationHelper);
        BaseTbLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_sample))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(getSampleEligibilityValidator(actionHelper))
                .withFormName(Constants.TbLeprosy_FOLLOWUP_FORMS.TBLEPROSY_SAMPLE)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_sample), action);
    }

    private BaseTbLeprosyVisitAction.Validator getSourceContactTypeValidator(final String requiredType) {
        return new BaseTbLeprosyVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return isSourceContactTypeSelected(requiredType);
            }

            @Override
            public boolean isEnabled(String key) {
                return isValid(key);
            }

            @Override
            public void onChanged(String key) {
                // UI refresh handles visibility changes when data updates
            }
        };
    }

    private boolean isSourceContactTypeSelected(String requiredType) {
        if (StringUtils.isBlank(requiredType) || context == null) {
            return false;
        }

        BaseTbLeprosyVisitAction sourceAction = actionList.get(context.getString(R.string.tbleprosy_source));
        if (sourceAction == null) {
            return false;
        }

        String payload = sourceAction.getJsonPayload();
        if (StringUtils.isBlank(payload)) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(payload);
            List<String> selections = extractSelections(jsonObject, "contact_lives_with_patient_type");

            if (selections.isEmpty()) {
                selections = extractSelections(jsonObject, "anaishi_karibu_na_mgonjwa");
            }

            if (selections.isEmpty()) {
                selections = extractSelections(jsonObject, "relationship_to_index_client");
            }

            if (selections.isEmpty()) {
                selections = extractSelections(jsonObject, "aina_ya_ukaribu_na_mgonjwa");
            }

            String normalizedRequiredType = requiredType.toLowerCase(Locale.ENGLISH);
            if ("tuberculosis".equals(normalizedRequiredType)) {
                normalizedRequiredType = "tb";
            } else if ("ukoma".equals(normalizedRequiredType)) {
                normalizedRequiredType = "leprosy";
            }

            for (String selection : selections) {
                if (StringUtils.isBlank(selection)) {
                    continue;
                }

                String normalizedSelection = selection.toLowerCase(Locale.ENGLISH);
                if ("tuberculosis".equals(normalizedSelection)) {
                    normalizedSelection = "tb";
                } else if ("ukoma".equals(normalizedSelection)) {
                    normalizedSelection = "leprosy";
                }

                if (normalizedSelection.equals(normalizedRequiredType)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    private List<String> extractSelections(JSONObject jsonObject, String key) {
        List<String> selections = new ArrayList<>();
        if (jsonObject == null || StringUtils.isBlank(key)) {
            return selections;
        }

        try {
            JSONObject fieldObject = FormUtils.getFieldFromForm(jsonObject, key);
            if (fieldObject == null || !fieldObject.has(JsonFormConstants.VALUE)) {
                return selections;
            }

            Object rawValue = fieldObject.get(JsonFormConstants.VALUE);
            if (rawValue instanceof JSONArray) {
                JSONArray array = (JSONArray) rawValue;
                for (int i = 0; i < array.length(); i++) {
                    String value = array.optString(i);
                    if (StringUtils.isNotBlank(value)) {
                        selections.add(value.trim());
                    }
                }
            } else if (rawValue instanceof String) {
                String value = (String) rawValue;
                if (StringUtils.isNotBlank(value)) {
                    String[] tokens = value.split("[,\\s]+");
                    for (String token : tokens) {
                        if (StringUtils.isNotBlank(token)) {
                            selections.add(token.trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return selections;
    }

    private BaseTbLeprosyVisitAction.Validator getSampleEligibilityValidator(final TbLeprosySampleActionHelper sampleActionHelper) {
        return new BaseTbLeprosyVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return sampleActionHelper != null
                        && isSourceContactTypeSelected("tb")
                        && sampleActionHelper.isEligibleForSampleCollection();
            }

            @Override
            public boolean isEnabled(String key) {
                return isValid(key);
            }

            @Override
            public void onChanged(String key) {
                // No-op
            }
        };
    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.TBLEPROSY_CONTACT_VISIT;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.TBLEPROSY_SERVICES;
    }




}
