package org.smartregister.chw.tbleprosy.util;

import android.content.Context;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.tbleprosy.TbLeprosyLibrary;
import org.smartregister.chw.tbleprosy.domain.Visit;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.chw.tbleprosy.repository.VisitDetailsRepository;
import org.smartregister.chw.tbleprosy.repository.VisitRepository;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.repository.AllSharedPreferences;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VisitUtils {

    public static String Complete = "complete";
    public static String Pending = "pending";
    public static String Ongoing = "ongoing";



    public static List<Visit> getVisits(String memberID, String... eventTypes) {


        List<Visit> visits = (eventTypes != null && eventTypes.length > 0) ? getVisitsOnly(memberID, eventTypes[0]) : getVisitsOnly(memberID, Constants.EVENT_TYPE.TB_LEPROSY_SCREENING);

        return visits;
    }


    public static List<Visit> getVisitsOnly(String memberID, String visitName) {
        return new ArrayList<>(TbLeprosyLibrary.getInstance().visitRepository().getVisits(memberID, visitName));
    }

    public static List<VisitDetail> getVisitDetailsOnly(String visitID) {
        return TbLeprosyLibrary.getInstance().visitDetailsRepository().getVisits(visitID);
    }


    public static Map<String, List<VisitDetail>> getVisitGroups(List<VisitDetail> detailList) {
        Map<String, List<VisitDetail>> visitMap = new HashMap<>();

        for (VisitDetail visitDetail : detailList) {

            List<VisitDetail> visitDetailList = visitMap.get(visitDetail.getVisitKey());
            if (visitDetailList == null)
                visitDetailList = new ArrayList<>();

            visitDetailList.add(visitDetail);

            visitMap.put(visitDetail.getVisitKey(), visitDetailList);
        }
        return visitMap;
    }

    /**
     * To be invoked for manual processing
     *
     * @param baseEntityID
     * @throws Exception
     */
    public static void processVisits(String baseEntityID) throws Exception {
        processVisits(TbLeprosyLibrary.getInstance().visitRepository(), TbLeprosyLibrary.getInstance().visitDetailsRepository(), baseEntityID);
    }

    public static void processVisits(VisitRepository visitRepository, VisitDetailsRepository visitDetailsRepository, String baseEntityID) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        List<Visit> visits = StringUtils.isNotBlank(baseEntityID) ?
                visitRepository.getAllUnSynced(calendar.getTime().getTime(), baseEntityID) :
                visitRepository.getAllUnSynced(calendar.getTime().getTime());
        processVisits(visits, visitRepository, visitDetailsRepository);
    }

    public static void processVisits(List<Visit> visits, VisitRepository visitRepository, VisitDetailsRepository visitDetailsRepository) throws Exception {
        String visitGroupId = UUID.randomUUID().toString();
        for (Visit v : visits) {
            if (!v.getProcessed()) {

                // persist to db
                Event baseEvent = new Gson().fromJson(v.getPreProcessedJson(), Event.class);
                if (StringUtils.isBlank(baseEvent.getFormSubmissionId()))
                    baseEvent.setFormSubmissionId(UUID.randomUUID().toString());

                baseEvent.addDetails(Constants.TBLEPROSY_VISIT_GROUP, visitGroupId);

                AllSharedPreferences allSharedPreferences = TbLeprosyLibrary.getInstance().context().allSharedPreferences();
                NCUtils.addEvent(allSharedPreferences, baseEvent);

                visitRepository.completeProcessing(v.getVisitId());
            }
        }

        // process after all events are saved
        NCUtils.startClientProcessing();

        // process vaccines and services
        Context context = TbLeprosyLibrary.getInstance().context().applicationContext();

    }


    public static Date getDateFromString(String dateStr) {
        try {
            return NCUtils.getSaveDateFormat().parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }


    /**
     * Check whether a visit occurred in the last 24 hours
     *
     * @param lastVisit The Visit instance for which you wish to check
     * @return true or false based on whether the visit was between 24 hours
     */
    public static boolean isVisitWithin24Hours(Visit lastVisit) {
        if (lastVisit != null) {
            return (Days.daysBetween(new DateTime(lastVisit.getCreatedAt()), new DateTime()).getDays() < 1) &&
                    (Days.daysBetween(new DateTime(lastVisit.getDate()), new DateTime()).getDays() <= 1);
        }
        return false;
    }

    public static String getActionStatus(Map<String, Boolean> checkObject) {
        for (Map.Entry<String, Boolean> entry : checkObject.entrySet()) {
            if (entry.getValue()) {
                if (checkObject.containsValue(false)) {
                    return Ongoing;
                }
                return Complete;
            }
        }
        return Pending;
    }
}
