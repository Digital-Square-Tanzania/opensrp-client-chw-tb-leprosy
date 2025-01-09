package org.smartregister.chw.tbleprosy.actionhelper;

import android.content.Context;

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

import timber.log.Timber;

public class TbLeprosySampleActionHelper implements BaseTbLeprosyVisitAction.TbLeprosyVisitActionHelper {
    protected String jsonPayload;

    protected String sampleCollection;
    protected String observation;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;


    public TbLeprosySampleActionHelper(Context context, MemberObject memberObject) {
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
            JSONObject global = jsonObject.getJSONObject("global");
            global.put("observation", observation);
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
            sampleCollection = JsonFormUtils.getValue(jsonObject, "amechukuliwa_sampuli");


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


        if(StringUtils.isNotBlank(sampleCollection)){
            return BaseTbLeprosyVisitAction.Status.COMPLETED;
        }
        return BaseTbLeprosyVisitAction.Status.PENDING;

    }

    @Override
    public void onPayloadReceived(BaseTbLeprosyVisitAction baseTbLeprosyVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
