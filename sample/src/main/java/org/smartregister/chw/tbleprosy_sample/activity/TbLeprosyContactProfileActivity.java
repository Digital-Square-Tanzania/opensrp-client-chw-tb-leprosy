package org.smartregister.chw.tbleprosy_sample.activity;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
import static org.smartregister.chw.tbleprosy.util.Constants.EVENT_TYPE.TBLEPROSY_CONTACT_VISIT;
import static org.smartregister.chw.tbleprosy.util.Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE;
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
import org.smartregister.chw.tbleprosy.activity.BaseTbLeprosyVisitActivity;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.domain.Visit;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.chw.tbleprosy_sample.R;

import timber.log.Timber;


public class TbLeprosyContactProfileActivity extends BaseTbLeprosyProfileActivity {
    private Visit enrollmentVisit = null;
    private Visit serviceVisit = null;

    private String encounterType;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, TbLeprosyContactProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivity(intent);
    }

    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        return EntryActivity.getSampleMember();
    }

    @Override
    protected void setupButtons() {
        textViewRecordTbContactVisit.setVisibility(View.VISIBLE);

        if (StringUtils.isNotBlank(encounterType)) {
            if (encounterType.equalsIgnoreCase(TBLEPROSY_CONTACT_VISIT)) {

                textViewRecordTbContactVisit.setVisibility(View.GONE);

                rlLastVisit.setVisibility(View.VISIBLE);
                rlObservationResults.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void openFollowupVisit() {
      // Implementation here.
    }

    @Override
    public void openRecordTbContactVisit() {
      try {
        startServiceForm();
      } catch (Exception e){
          Timber.e(e);
      }
    }

    @Override
    public void openMatukioYaUchunguzi() {
        // Implementation Here
        try {
            startForm("tbleprosy_matokeo_ya_uchunguzi");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void observationResults() {
        try {
            startForm("tbleprosy_matokeo_ya_uchunguzi");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public void openMedicalHistory() {
        //implementation here
        try {
            startForm("tbleprosy_matokeo_ya_uchunguzi");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
