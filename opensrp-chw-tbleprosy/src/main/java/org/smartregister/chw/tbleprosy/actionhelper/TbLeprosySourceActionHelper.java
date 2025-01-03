package org.smartregister.chw.tbleprosy.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.chw.tbleprosy.model.BaseTbLeprosyVisitAction;
import org.smartregister.chw.tbleprosy.util.JsonFormUtils;
import org.smartregister.chw.tbleprosy.util.VisitUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import timber.log.Timber;

public class TbLeprosySourceActionHelper implements BaseTbLeprosyVisitAction.TbLeprosyVisitActionHelper {

    protected static String is_client_diagnosed_with_any;

    protected static String any_complaints;

    protected static String complications_previous_surgical;

    protected static String any_hematological_disease_symptoms;

    protected static String known_allergies;

    protected static String type_of_blood_for_glucose_test;

    protected static String blood_for_glucose;

    protected static String blood_for_glucose_test;

    protected static String client_diagnosed_other;

    protected String jsonPayload;

    protected String medical_history;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;

    private HashMap<String, Boolean> checkObject = new HashMap<>();


    public TbLeprosySourceActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            JSONObject clientNumber = JsonFormUtils.getFieldJSONObject(fields, "namba_ya_mteja");

            clientNumber.put("mask","##-##-##-######-#/KK/" + Calendar.getInstance().get(Calendar.YEAR) + "/#");



            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            checkObject.clear();

            checkObject.put("has_client_had_any_sti", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "has_client_had_any_sti")));
            checkObject.put("any_complaints", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "any_complaints")));
            checkObject.put("is_client_diagnosed_with_any", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "is_client_diagnosed_with_any")));
            checkObject.put("surgical_procedure", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "surgical_procedure")));
            checkObject.put("known_allergies", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "known_allergies")));
            checkObject.put("tetanus_vaccination", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "tetanus_vaccination")));
            checkObject.put("any_hematological_disease_symptoms", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "any_hematological_disease_symptoms")));


            is_client_diagnosed_with_any = JsonFormUtils.getValue(jsonObject, "is_client_diagnosed_with_any");
            any_complaints = JsonFormUtils.getValue(jsonObject, "any_complaints");
            complications_previous_surgical = JsonFormUtils.getValue(jsonObject, "complications_previous_surgical");
            any_hematological_disease_symptoms = JsonFormUtils.getValue(jsonObject, "any_hematological_disease_symptoms");
            known_allergies = JsonFormUtils.getValue(jsonObject, "known_allergies");
            type_of_blood_for_glucose_test = JsonFormUtils.getValue(jsonObject, "type_of_blood_for_glucose_test");
            blood_for_glucose = JsonFormUtils.getValue(jsonObject, "blood_for_glucose");
            blood_for_glucose_test = JsonFormUtils.getValue(jsonObject, "blood_for_glucose_test");
            client_diagnosed_other = JsonFormUtils.getValue(jsonObject, "is_client_diagnosed_with_any_others");

            medical_history = JsonFormUtils.getValue(jsonObject, "has_client_had_any_sti");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseTbLeprosyVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String jsonPayload) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);
            JSONObject medicalHistoryCompletionStatus = JsonFormUtils.getFieldJSONObject(fields, "medical_history_completion_status");
            assert medicalHistoryCompletionStatus != null;
            medicalHistoryCompletionStatus.put(com.vijay.jsonwizard.constants.JsonFormConstants.VALUE, VisitUtils.getActionStatus(checkObject));
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (jsonObject != null) {
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseTbLeprosyVisitAction.Status evaluateStatusOnPayload() {
        String status = VisitUtils.getActionStatus(checkObject);

        if (status.equalsIgnoreCase(VisitUtils.Complete)) {
            return BaseTbLeprosyVisitAction.Status.COMPLETED;
        }
        if (status.equalsIgnoreCase(VisitUtils.Ongoing)) {
            return BaseTbLeprosyVisitAction.Status.PARTIALLY_COMPLETED;
        }
        return BaseTbLeprosyVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseTbLeprosyVisitAction baseTbLeprosyVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
