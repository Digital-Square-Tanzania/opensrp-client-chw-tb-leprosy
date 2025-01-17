package org.smartregister.chw.tbleprosy_sample.activity;

import static org.smartregister.chw.tbleprosy.util.Constants.JSON_FORM_EXTRA.EVENT_TYPE;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.factory.FileSourceFactoryHelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.activity.BaseTbLeprosyProfileActivity;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.domain.Visit;
import org.smartregister.chw.tbleprosy.util.Constants;

import timber.log.Timber;


public class UpdateTbLeprosyMemberProfileActivity extends BaseTbLeprosyProfileActivity {
    private final Visit enrollmentVisit = null;
    private final Visit serviceVisit = null;

    private String encounterType;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, UpdateTbLeprosyMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivity(intent);
    }

    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        return EntryActivity.getSampleMember();
    }

    @Override
    protected void setupButtons() {
        textViewRecordTbLeprosy.setVisibility(View.VISIBLE);
//        textViewRecordTbLeprosy.setText("Record TB Leprosy Visit");

        if (StringUtils.isNotBlank(encounterType)) {
            if (encounterType.equalsIgnoreCase(Constants.EVENT_TYPE.TB_LEPROSY_ENROLLMENT)) {
                textViewRecordTbLeprosy.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void openFollowupVisit() {
        try {
            startForm("tbleprosy_record_visit");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void openRecordTbContactVisit() {
        // Implementations here
    }


    @Override
    public void observationResults() {
        // Implementations Here.
    }

    @Override
    public void openTbContactFollowUpVisit() {

    }

    private void startForm(String formName) throws Exception {
        JSONObject jsonForm = FileSourceFactoryHelper.getFileSource("").getFormFromFile(getApplicationContext(), formName);

        String currentLocationId = "Tanzania";
        if (jsonForm != null) {
            jsonForm.getJSONObject("metadata").put("encounter_location", currentLocationId);
            Intent intent = new Intent(this, JsonWizardFormActivity.class);
            intent.putExtra("json", jsonForm.toString());

            Form form = new Form();
            form.setWizard(true);
            form.setNextLabel("Next");
            form.setPreviousLabel("Previous");
            form.setSaveLabel("Save");
            form.setHideSaveLabel(true);

            intent.putExtra("form", form);
            startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);

        }

    }

    @Override
    public void startServiceForm() {
        TbLeprosyServiceActivity.startTbLeprosyVisitActivity(this, memberObject.getBaseEntityId(), false);
    }


    @Override
    public void continueService() {

    }


    @Override
    public void continueDischarge() {

    }

    @Override
    protected Visit getServiceVisit() {
        return serviceVisit;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                encounterType = form.getString(EVENT_TYPE);

            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        delayRefreshSetupViews();
    }


    private void delayRefreshSetupViews() {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::setupViews, 300);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
