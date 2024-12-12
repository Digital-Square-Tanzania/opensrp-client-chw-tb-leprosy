package org.smartregister.chw.tbleprosy.util;

import static org.smartregister.chw.tbleprosy.util.Constants.ENCOUNTER_TYPE;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.TbLeprosyLibrary;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String IMAGE = "image";
    public static final String HOME_VISIT_GROUP = "home_visit_group";
    private static final String V_REQUIRED = "v_required";

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    public static Event processVisitJsonForm(AllSharedPreferences allSharedPreferences, String entityId, String encounterType, Map<String, String> jsonStrings, String tableName) {

        // aggregate all the fields into 1 payload
        JSONObject jsonForm = null;
        JSONObject metadata = null;

        List<JSONObject> fields_obj = new ArrayList<>();

        for (Map.Entry<String, String> map : jsonStrings.entrySet()) {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(map.getValue());

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            if (jsonForm == null) {
                jsonForm = registrationFormParams.getMiddle();
            }

            if (metadata == null) {
                metadata = getJSONObject(jsonForm, METADATA);
            }

            // add all the fields to the event while injecting a new variable for grouping
            JSONArray local_fields = registrationFormParams.getRight();
            int x = 0;
            while (local_fields.length() > x) {
                try {
                    JSONObject obj = local_fields.getJSONObject(x);
                    obj.put(HOME_VISIT_GROUP, map.getKey());
                    fields_obj.add(obj);
                } catch (JSONException e) {
                    Timber.e(e);
                }
                x++;
            }
        }

        if (metadata == null) {
            metadata = new JSONObject();
        }

        JSONArray fields = new JSONArray(fields_obj);
        String derivedEncounterType = StringUtils.isBlank(encounterType) && jsonForm != null ? getString(jsonForm, ENCOUNTER_TYPE) : encounterType;

        return org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag(allSharedPreferences), entityId, derivedEncounterType, tableName);
    }

    public static Event prepareEvent(AllSharedPreferences allSharedPreferences, String entityId, String jsonString, String tableName) throws JSONException {

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

        if (!registrationFormParams.getLeft()) {
            return null;
        }

        JSONObject jsonForm = registrationFormParams.getMiddle();
        String encounterType = jsonForm.getString("encounter_type");
        JSONObject metadata = getJSONObject(jsonForm, METADATA);
        JSONArray fields = registrationFormParams.getRight();

        return org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag(allSharedPreferences), entityId, encounterType, tableName);
    }

    public static Event createUntaggedEvent(String baseEntityId, String eventType, String table) {

        try {
            AllSharedPreferences allSharedPreferences = TbLeprosyLibrary.getInstance().context().allSharedPreferences();

            return org.smartregister.util.JsonFormUtils.createEvent(new JSONArray(), new JSONObject(), formTag(allSharedPreferences), baseEntityId, eventType, table);

        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public static Event processJsonForm(AllSharedPreferences allSharedPreferences, String jsonString, String table) {

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

        if (!registrationFormParams.getLeft()) {
            return null;
        }

        JSONObject jsonForm = registrationFormParams.getMiddle();
        JSONArray fields = registrationFormParams.getRight();
        String entityId = getString(jsonForm, ENTITY_ID);

        return org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, getString(jsonForm, ENCOUNTER_TYPE), table);
    }

    public static FormTag formTag(AllSharedPreferences allSharedPreferences) {
        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = TbLeprosyLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = TbLeprosyLibrary.getInstance().getDatabaseVersion();
        return formTag;
    }

    public static void tagEvent(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientApplicationVersion(TbLeprosyLibrary.getInstance().getApplicationVersion());
        event.setClientDatabaseVersion(TbLeprosyLibrary.getInstance().getDatabaseVersion());
    }

    public static String locationId(AllSharedPreferences allSharedPreferences) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String userLocationId = allSharedPreferences.fetchUserLocalityId(providerId);
        if (StringUtils.isBlank(userLocationId)) {
            userLocationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        }

        return userLocationId;
    }

    public static void getRegistrationForm(JSONObject jsonObject, String entityId, String currentLocationId) throws JSONException {
        jsonObject.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);
        jsonObject.put(org.smartregister.util.JsonFormUtils.ENTITY_ID, entityId);
    }


    /**
     * Returns a value from json form field
     *
     * @param jsonObject native forms jsonObject
     * @param key        field object key
     * @return value
     */
    public static String getValue(JSONObject jsonObject, String key) {
        try {
            JSONObject formField = com.vijay.jsonwizard.utils.FormUtils.getFieldFromForm(jsonObject, key);
            if (formField != null && formField.has(JsonFormConstants.VALUE)) {
                return formField.getString(JsonFormConstants.VALUE);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    public static String getFirstObjectKey(JSONObject jsonObject) {
        return getObjectKey(jsonObject, 0);
    }

    public static String getObjectKey(JSONObject jsonObject, int position) {
        try {
            JSONArray jsonArray = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            if (jsonArray.length() > position && position > -1) {
                return jsonArray.getJSONObject(position - 1).getString(JsonFormConstants.KEY);

            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    /**
     * Returns a value from a native forms checkbox field and returns an comma separated string
     *
     * @param jsonObject native forms jsonObject
     * @param key        field object key
     * @return value
     */
    public static String getCheckBoxValue(JSONObject jsonObject, String key) {
        try {
            JSONArray jsonArray = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

            JSONObject jo = null;
            int x = 0;
            while (jsonArray.length() > x) {
                jo = jsonArray.getJSONObject(x);
                if (jo.getString(JsonFormConstants.KEY).equalsIgnoreCase(key)) {
                    break;
                }
                x++;
            }

            StringBuilder resBuilder = new StringBuilder();
            if (jo != null) {
                // read all the checkboxes
                JSONArray jaOptions = jo.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                int optionSize = jaOptions.length();
                int y = 0;
                while (optionSize > y) {
                    JSONObject options = jaOptions.getJSONObject(y);
                    if (options.getBoolean(JsonFormConstants.VALUE)) {
                        resBuilder.append(options.getString(JsonFormConstants.TEXT)).append(", ");
                    }
                    y++;
                }

                String res = resBuilder.toString();
                res = res.substring(0, res.length() - 2);
                return res;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    public static void populateForm(@Nullable JSONObject jsonObject, Map<String, @Nullable List<VisitDetail>> details) {
        if (details == null || jsonObject == null) return;
        try {
            // x steps
            String count_str = jsonObject.getString(JsonFormConstants.COUNT);

            int step_count = StringUtils.isNotBlank(count_str) ? Integer.valueOf(count_str) : 1;
            while (step_count > 0) {
                JSONArray jsonArray = jsonObject.getJSONObject(MessageFormat.format("step{0}", step_count)).getJSONArray(JsonFormConstants.FIELDS);

                int field_count = jsonArray.length() - 1;
                while (field_count >= 0) {

                    JSONObject jo = jsonArray.getJSONObject(field_count);
                    String key = jo.getString(JsonFormConstants.KEY);
                    List<VisitDetail> detailList = details.get(key);

                    if (detailList != null) {
                        if (jo.getString(JsonFormConstants.TYPE).equalsIgnoreCase(JsonFormConstants.CHECK_BOX)) {
                            jo.put(JsonFormConstants.VALUE, getValue(jo, detailList));
                        } else {
                            String value = getValue(detailList.get(0));
                            if (key.contains("date")) {
                                value = NCUtils.getFormattedDate(NCUtils.getSaveDateFormat(), NCUtils.getSourceDateFormat(), value);
                            }
                            jo.put(JsonFormConstants.VALUE, value);
                        }
                    }

                    field_count--;
                }

                step_count--;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static String getValue(VisitDetail visitDetail) {
        String humanReadable = visitDetail.getHumanReadable();
        if (StringUtils.isNotBlank(humanReadable))
            return humanReadable;

        return visitDetail.getDetails();
    }

    public static JSONArray getValue(JSONObject jo, List<VisitDetail> visitDetails) throws JSONException {
        JSONArray values = new JSONArray();
        if (jo.getString(JsonFormConstants.TYPE).equalsIgnoreCase(JsonFormConstants.CHECK_BOX)) {
            JSONArray options = jo.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            HashMap<String, NameID> valueMap = new HashMap<>();

            int x = options.length() - 1;
            while (x >= 0) {
                JSONObject object = options.getJSONObject(x);
                valueMap.put(object.getString(JsonFormConstants.KEY), new NameID(object.getString(JsonFormConstants.KEY), x));
                x--;
            }

            for (VisitDetail d : visitDetails) {
                String val = getValue(d);
                List<String> checkedList = new ArrayList<>(Arrays.asList(val.split(", ")));
                if (checkedList.size() > 1) {
                    for (String item : checkedList) {
                        NameID nid = valueMap.get(item);
                        if (nid != null) {
                            values.put(nid.name);
                            options.getJSONObject(nid.position).put(JsonFormConstants.VALUE, true);
                        }
                    }
                } else {
                    NameID nid = valueMap.get(val);
                    if (nid != null) {
                        values.put(nid.name);
                        options.getJSONObject(nid.position).put(JsonFormConstants.VALUE, true);
                    }
                }
            }
        } else {
            for (VisitDetail d : visitDetails) {
                String val = getValue(d);
                if (StringUtils.isNotBlank(val)) {
                    values.put(val);
                }
            }
        }
        return values;
    }

    public static String cleanString(String dirtyString) {
        if (StringUtils.isBlank(dirtyString))
            return "";

        return dirtyString.substring(1, dirtyString.length() - 1);
    }


    public static void updateFormField(JSONArray formFieldArrays, String formFieldKey, String updateValue) {
        if (updateValue != null) {
            JSONObject formObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(formFieldArrays, formFieldKey);
            if (formObject != null) {
                try {
                    formObject.put(org.smartregister.util.JsonFormUtils.VALUE, updateValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class NameID {
        private String name;
        private int position;

        public NameID(String name, int position) {
            this.name = name;
            this.position = position;
        }
    }

}