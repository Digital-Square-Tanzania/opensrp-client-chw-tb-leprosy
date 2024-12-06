package org.smartregister.chw.skeleton.contract;

import android.content.Context;

import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.skeleton.domain.MemberObject;
import org.smartregister.chw.skeleton.model.BaseSkeletonVisitAction;

import java.util.LinkedHashMap;
import java.util.Map;

public interface BaseSkeletonVisitContract {
    interface View extends VisitView {

        Presenter presenter();

        Form getFormConfig();

        void startForm(BaseSkeletonVisitAction skeletonHomeVisitAction);

        void startFormActivity(JSONObject jsonForm);

        void startFragment(BaseSkeletonVisitAction skeletonHomeVisitAction);

        void redrawHeader(MemberObject memberObject);

        void redrawVisitUI();

        void displayProgressBar(boolean state);

        Map<String, BaseSkeletonVisitAction> getSkeletonVisitActions();

        void close();

        void submittedAndClose(String results);

        Presenter getPresenter();

        /**
         * Save the received data into the events table
         * Start aggregation of all events and persist results into the events table
         */
        void submitVisit();

        void initializeActions(LinkedHashMap<String, BaseSkeletonVisitAction> map);

        Context getContext();

        void displayToast(String message);

        Boolean getEditMode();

        void onMemberDetailsReloaded(MemberObject memberObject);
    }

    interface VisitView {

        /**
         * Results action when a dialog is opened and returns a payload
         *
         * @param jsonString
         */
        void onDialogOptionUpdated(String jsonString);

        Context getMyContext();
    }

    interface Presenter {

        void startForm(String formName, String memberID, String currentLocationId);

        /**
         * Recall this method to redraw ui after every submission
         *
         * @return
         */
        boolean validateStatus();

        /**
         * Preload header and visit
         */
        void initialize();

        void submitVisit();

        void reloadMemberDetails(String memberID, String profileType);
    }

    interface Model {

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception;

    }

    interface Interactor {

        void reloadMemberDetails(String memberID, String profileType, InteractorCallBack callBack);

        MemberObject getMemberClient(String memberID, String profileType);

        void saveRegistration(String jsonString, boolean isEditMode, final InteractorCallBack callBack);

        void calculateActions(View view, MemberObject memberObject, InteractorCallBack callBack);

        void submitVisit(boolean editMode, String memberID, Map<String, BaseSkeletonVisitAction> map, InteractorCallBack callBack);
    }

    interface InteractorCallBack {

        void onMemberDetailsReloaded(MemberObject memberObject);

        void onRegistrationSaved(boolean isEdit);

        void preloadActions(LinkedHashMap<String, BaseSkeletonVisitAction> map);

        void onSubmitted(String results);
    }
}
