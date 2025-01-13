package org.smartregister.chw.tbleprosy_sample.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.room.util.StringUtil;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.factory.FileSourceFactoryHelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.contract.BaseTbLeprosyVisitContract;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.chw.tbleprosy.util.DBConstants;
import org.smartregister.chw.tbleprosy.util.JsonFormUtils;
import org.smartregister.chw.tbleprosy_sample.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.SecuredActivity;

import java.time.Year;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class EntryActivity extends SecuredActivity implements View.OnClickListener, BaseTbLeprosyVisitContract.VisitView {
    private static MemberObject tbleprosyMemberObject;

    public static MemberObject getSampleMember() {
        if (tbleprosyMemberObject == null) {
            tbleprosyMemberObject = new MemberObject();
            tbleprosyMemberObject.setFirstName("Glory");
            tbleprosyMemberObject.setLastName("Juma");
            tbleprosyMemberObject.setMiddleName("Ali");
            tbleprosyMemberObject.setGender("Female");
            tbleprosyMemberObject.setMartialStatus("Married");
            tbleprosyMemberObject.setDob("1982-01-18T03:00:00.000+03:00");
            tbleprosyMemberObject.setUniqueId("3503504");
            tbleprosyMemberObject.setBaseEntityId("3503504");
            tbleprosyMemberObject.setFamilyBaseEntityId("3503504");
        }

        return tbleprosyMemberObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale("sw");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.tbleprosy_activity).setOnClickListener(this);
        findViewById(R.id.tbleprosy_screening).setOnClickListener(this);
        findViewById(R.id.tbleprosy_visit_record).setOnClickListener(this);
        findViewById(R.id.tbleprosy_mobilization).setOnClickListener(this);
        findViewById(R.id.tbleprosy_matokeo_ya_uchunguzi).setOnClickListener(this);
        findViewById(R.id.tbleprosy_matokeo_ya_uchunguzi_contact).setOnClickListener(this);
        findViewById(R.id.tbleprosy_ufuatiliaji_wa_mteja).setOnClickListener(this);
        findViewById(R.id.tbleprosy_home_visit).setOnClickListener(this);
        findViewById(R.id.tbleprosy_profile).setOnClickListener(this);
        findViewById(R.id.tbleprosy_contact_visit).setOnClickListener(this);
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onCreation() {
        Timber.v("onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("onCreation");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tbleprosy_activity:
                startActivity(new Intent(this, TbLeprosyRegisterActivity.class));
                break;
            case R.id.tbleprosy_home_visit:
                TbLeprosyServiceActivity.startTbLeprosyVisitActivity(this, "12345", true);
                break;
            case R.id.tbleprosy_profile:
                TbLeprosyMemberProfileActivity.startMe(this, "12345");
                break;
            case R.id.tbleprosy_contact_visit:
                TbLeprosyServiceActivity.startTbLeprosyVisitActivity(this, "98765", false);
                break;
            case R.id.tbleprosy_screening:
                try {
                    startForm("tbleprosy_screening");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.tbleprosy_mobilization:
                try {
                    startForm("tbleprosy_mobilization_session");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.tbleprosy_matokeo_ya_uchunguzi:
                try {
                    startForm("tbleprosy_matokeo_ya_uchunguzi");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.tbleprosy_ufuatiliaji_wa_mteja:
                try {
                    startForm("tbleprosy_ufuatiliaji_wa_mteja");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.tbleprosy_visit_record:
                try {
                    startForm("tbleprosy_record_visit");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.tbleprosy_matokeo_ya_uchunguzi_contact:
                try {
                   startForm("tbleprosy_matokeo_uchunguzi_contact");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
    }

    @SuppressLint("TimberArgCount")
    private void startForm(String formName) throws Exception {
        JSONObject jsonForm = FileSourceFactoryHelper.getFileSource("").getFormFromFile(getApplicationContext(), formName);

        String currentLocationId = "Tanzania";
        if (jsonForm != null) {

            JSONArray dataFields = jsonForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            JSONObject clientID = JsonFormUtils.getFieldJSONObject(dataFields, "namba_ya_mteja_tb");
            JSONObject clientIdUkoma = JsonFormUtils.getFieldJSONObject(dataFields, "namba_ya_mteja_ukoma");

             if(clientID != null){
                 clientID.put("mask", "##-##-##-######-#/KK/" +Calendar.getInstance().get(Calendar.YEAR)+ "/#");
             }

             if(clientIdUkoma != null ){
                 clientID.put("mask", "##-##-##-######-#/KK/" +Calendar.getInstance().get(Calendar.YEAR)+ "/#");
             }

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
    public void onDialogOptionUpdated(String jsonString) {
        Timber.v("onDialogOptionUpdated %s", jsonString);
    }

    @Override
    public Context getMyContext() {
        return this;
    }
}