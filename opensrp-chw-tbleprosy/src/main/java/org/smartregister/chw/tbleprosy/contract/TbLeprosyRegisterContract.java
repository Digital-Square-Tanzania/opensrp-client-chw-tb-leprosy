package org.smartregister.chw.tbleprosy.contract;

import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.view.contract.BaseRegisterContract;

public interface TbLeprosyRegisterContract {

    interface View extends BaseRegisterContract.View {
        Presenter presenter();

        Form getFormConfig();
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveForm(String jsonString);

    }

    interface Model {

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception;

    }

    interface Interactor {

        void saveRegistration(String jsonString, final InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onRegistrationSaved();

    }
}
