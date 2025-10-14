package org.smartregister.chw.tbleprosy.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.chw.tbleprosy.model.BaseTbLeprosyVisitAction;
import org.smartregister.chw.tbleprosy.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class TbLeprosyInvestigationActionHelper implements BaseTbLeprosyVisitAction.TbLeprosyVisitActionHelper {

    protected String jsonPayload;

    protected String tbleprosyObservation;
    protected String screeningStatus;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;


    public TbLeprosyInvestigationActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            screeningStatus = JsonFormUtils.getValue(jsonObject, "screening_status");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }


    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            tbleprosyObservation = JsonFormUtils.getValue(jsonObject, "majibu_ya_uchunguzi_tb");

            if (StringUtils.isBlank(tbleprosyObservation)){
                tbleprosyObservation = JsonFormUtils.getValue(jsonObject, "majibu_ya_uchunguzi_ukoma");
            }

            screeningStatus = JsonFormUtils.getValue(jsonObject, "screening_status");

        } catch (JSONException e) {
           Timber.e(e);
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
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseTbLeprosyVisitAction.Status evaluateStatusOnPayload() {
        if(StringUtils.isNotBlank(tbleprosyObservation)){
            return BaseTbLeprosyVisitAction.Status.COMPLETED;
        }
        return BaseTbLeprosyVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseTbLeprosyVisitAction baseTbLeprosyVisitAction) {
        Timber.v("onPayloadReceived");
    }

    public String getScreeningStatus() {
        return screeningStatus;
    }

    public boolean isTbPresumptive() {
        return StringUtils.equalsIgnoreCase(screeningStatus, "tb-presumptive");
    }
}
