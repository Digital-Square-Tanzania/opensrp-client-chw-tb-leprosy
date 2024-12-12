package org.smartregister.chw.tbleprosy.model;

import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.contract.TbLeprosyRegisterContract;
import org.smartregister.chw.tbleprosy.util.TbLeprosyJsonFormUtils;

public class BaseTbLeprosyRegisterModel implements TbLeprosyRegisterContract.Model {

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject jsonObject = TbLeprosyJsonFormUtils.getFormAsJson(formName);
        TbLeprosyJsonFormUtils.getRegistrationForm(jsonObject, entityId, currentLocationId);

        return jsonObject;
    }

}
