package org.smartregister.chw.tbleprosy.dao;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.dao.AbstractDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TbLeprosyDao extends AbstractDao {
    private static final SimpleDateFormat df = new SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
    );

    private static final DataMap<MemberObject> memberObjectMap = cursor -> {

        MemberObject memberObject = new MemberObject();

        memberObject.setFirstName(getCursorValue(cursor, "first_name", ""));
        memberObject.setMiddleName(getCursorValue(cursor, "middle_name", ""));
        memberObject.setLastName(getCursorValue(cursor, "last_name", ""));
        memberObject.setAddress(getCursorValue(cursor, "village_town"));
        memberObject.setGender(getCursorValue(cursor, "gender"));
        memberObject.setMartialStatus(getCursorValue(cursor, "marital_status"));
        memberObject.setUniqueId(getCursorValue(cursor, "unique_id", ""));
        memberObject.setDob(getCursorValue(cursor, "dob"));
        memberObject.setFamilyBaseEntityId(getCursorValue(cursor, "family_base_entity_id", ""));
        memberObject.setRelationalId(getCursorValue(cursor, "relational_id", ""));
        memberObject.setPrimaryCareGiver(getCursorValue(cursor, "primary_caregiver"));
        memberObject.setFamilyName(getCursorValue(cursor, "family_name", ""));
        memberObject.setPhoneNumber(getCursorValue(cursor, "phone_number", ""));
        memberObject.setTbLeprosyTestDate(getCursorValueAsDate(cursor, "tbleprosy_test_date", df));
        memberObject.setBaseEntityId(getCursorValue(cursor, "base_entity_id", ""));
        memberObject.setFamilyHead(getCursorValue(cursor, "family_head", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "pcg_phone_number", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "family_head_phone_number", ""));
        memberObject.setEnrollmentDate(getCursorValue(cursor,
                "enrollment_date",
                ""));

        String familyHeadName = getCursorValue(cursor, "family_head_first_name", "") + " "
                + getCursorValue(cursor, "family_head_middle_name", "");

        familyHeadName =
                (familyHeadName.trim() + " " + getCursorValue(cursor, "family_head_last_name", "")).trim();
        memberObject.setFamilyHeadName(familyHeadName);

        String familyPcgName = getCursorValue(cursor, "pcg_first_name", "") + " "
                + getCursorValue(cursor, "pcg_middle_name", "");

        familyPcgName =
                (familyPcgName.trim() + " " + getCursorValue(cursor, "pcg_last_name", "")).trim();
        memberObject.setPrimaryCareGiverName(familyPcgName);

        return memberObject;
    };

    public static String getTbLeprosyClientStatus(String baseEntityId) {
        String status = getClientStatusFromTable(Constants.TABLES.TBLEPROSY_SCREENING, baseEntityId);

        if (StringUtils.isBlank(status)) {
            if (getContact(baseEntityId) != null) {
                status = "contact";
            } else {
                status = "client";
            }
        }

        return StringUtils.defaultString(status);
    }

    private static String getClientStatusFromTable(String tableName, String baseEntityId) {
        String sql = "SELECT status FROM " + tableName + " WHERE is_closed = 0 AND base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "status");
        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }
        return null;
    }

    public static String getTbScreeningStatus(String baseEntityId) {
        String sql = "SELECT screening_status FROM ec_tbleprosy_screening WHERE is_closed = 0 AND base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "screening_status");
        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return null;
    }

    public static boolean isTbPresumptiveClient(String baseEntityId) {
        String screeningStatus = getTbScreeningStatus(baseEntityId);
        if (StringUtils.isBlank(screeningStatus)) {
            return false;
        }

        String normalizedStatus = screeningStatus.toLowerCase(Locale.ENGLISH);
        return normalizedStatus.contains("tb_presamptive") || normalizedStatus.contains("tb_presumptive");
    }

    public static boolean isLeprosyPresumptiveClient(String baseEntityId) {
        String screeningStatus = getTbScreeningStatus(baseEntityId);
        if (StringUtils.isBlank(screeningStatus)) {
            return false;
        }

        String normalizedStatus = screeningStatus.toLowerCase(Locale.ENGLISH);
        return normalizedStatus.contains("leprocy_presamptive")
                || normalizedStatus.contains("leprocy_presumptive")
                || normalizedStatus.contains("leprosy_presamptive")
                || normalizedStatus.contains("leprosy_presumptive");
    }

    public static String getTbLeprosyObservationResults(String baseEntityId) {
        String sql = "SELECT tb_sample_test_results, clinical_decision FROM ec_tbleprosy_observation_results p " +
                " WHERE p.is_closed = 0 AND p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> {
            String tbSampleTestResults = getCursorValue(cursor, "tb_sample_test_results");
            if (StringUtils.isNotBlank(tbSampleTestResults)) {
                return tbSampleTestResults;
            }
            return getCursorValue(cursor, "clinical_decision");
        };

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static ObservationResults getLatestObservationResults(String baseEntityId) {
        String sql = "SELECT last_interacted_with, investigation_type, tb_diagnostic_test_type, tb_sample_test_results, " +
                "clinical_decision, leprosy_diagnostic_method, tb_treatment_start_date, leprosy_treatment_start_date, leprosy_investigation_results " +
                "FROM ec_tbleprosy_observation_results p " +
                " WHERE p.is_closed = 0 AND p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<ObservationResults> dataMap = cursor -> {
            String tbSampleTestResults = getCursorValue(cursor, "tb_sample_test_results", "");
            String clinicalDecision = getCursorValue(cursor, "clinical_decision", "");
            String leprosyInvestigationResults = getCursorValue(cursor, "leprosy_investigation_results", "");
            boolean poorQualitySample = StringUtils.containsIgnoreCase(tbSampleTestResults, "poor_quality_sample");

            return new ObservationResults(
                    getCursorValue(cursor, "last_interacted_with", ""),
                    getCursorValue(cursor, "investigation_type", ""),
                    getCursorValue(cursor, "tb_diagnostic_test_type", ""),
                    tbSampleTestResults,
                    clinicalDecision,
                    getCursorValue(cursor, "leprosy_diagnostic_method", ""),
                    getCursorValue(cursor, "tb_treatment_start_date", ""),
                    getCursorValue(cursor, "leprosy_treatment_start_date", ""),
                    leprosyInvestigationResults,
                    poorQualitySample
            );
        };

        List<ObservationResults> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }
        return null;
    }

    public static boolean isClientTbOrLeprosyNegative(String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId)) {
            return false;
        }

        String sql = "SELECT tb_sample_test_results, clinical_decision, leprosy_investigation_results " +
                "FROM ec_tbleprosy_observation_results " +
                "WHERE is_closed = 0 AND entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<Boolean> dataMap = cursor -> {
            String tbSampleResult = StringUtils.defaultString(getCursorValue(cursor, "tb_sample_test_results", "")).toLowerCase(Locale.ENGLISH);
            String clinicalDecision = StringUtils.defaultString(getCursorValue(cursor, "clinical_decision", "")).toLowerCase(Locale.ENGLISH);
            String leprosyResult = StringUtils.defaultString(getCursorValue(cursor, "leprosy_investigation_results", "")).toLowerCase(Locale.ENGLISH);

            boolean tbNegative = "tb_dr_tb_undetected".equals(tbSampleResult) || "non_suggestive".equals(clinicalDecision);
            boolean leprosyNegative = "no_leprosy_detected".equals(leprosyResult);
            return tbNegative || leprosyNegative;
        };

        List<Boolean> res = readData(sql, dataMap);
        return res != null && !res.isEmpty() && Boolean.TRUE.equals(res.get(0));
    }

    /**
     * Close TB/Leprosy screening records whose latest observation shows the client is TB-negative.
     * This uses the most recent entry in ec_tbleprosy_observation_results per client and closes
     * only when TB results are negative and leprosy has not been confirmed positive. All related
     * open observation, follow-up, and visit records are closed alongside the screening row.
     */
    public static void closeTbNegativeClients() {
        String baseEntitySubQuery = "SELECT scr.base_entity_id FROM ec_tbleprosy_screening scr " +
                "  INNER JOIN (" +
                "    SELECT latest.entity_id, latest.last_interacted_with, " +
                "           lower(ifnull(latest.tb_sample_test_results, '')) AS tb_result, " +
                "           lower(ifnull(latest.clinical_decision, '')) AS clinical_result, " +
                "           lower(ifnull(latest.leprosy_investigation_results, '')) AS leprosy_result " +
                "    FROM ec_tbleprosy_observation_results latest " +
                "    WHERE latest.is_closed = 0 AND latest.last_interacted_with = (" +
                "      SELECT MAX(inner_tbl.last_interacted_with) FROM ec_tbleprosy_observation_results inner_tbl " +
                "      WHERE inner_tbl.entity_id = latest.entity_id AND inner_tbl.is_closed = 0" +
                "    )" +
                "  ) latest ON scr.base_entity_id = latest.entity_id " +
                "  WHERE scr.is_closed = 0 " +
                "    AND scr.last_interacted_with <= latest.last_interacted_with " +
                "    AND (latest.tb_result = 'tb_dr_tb_undetected' OR latest.clinical_result = 'non_suggestive' OR latest.leprosy_investigation_results = 'no_leprosy_detected') " +
                "    AND latest.leprosy_result != 'leprosy_confirmed'";

        List<String> baseEntityIds = readData(baseEntitySubQuery, cursor -> getCursorValue(cursor, "base_entity_id"));
        if (baseEntityIds == null || baseEntityIds.isEmpty()) {
            return;
        }

        String joinedIds = "'" + String.join("','", baseEntityIds) + "'";

        String closeObservationSql = "UPDATE ec_tbleprosy_observation_results SET is_closed = 1 " +
                "WHERE is_closed = 0 AND entity_id IN (" + joinedIds + ")";
        updateDB(closeObservationSql);

        String closeFollowUpSql = "UPDATE ec_tbleprosy_followup_visit SET is_closed = 1 " +
                "WHERE is_closed = 0 AND entity_id IN (" + joinedIds + ")";
        updateDB(closeFollowUpSql);

        String closeVisitSql = "UPDATE ec_tbleprosy_visit SET is_closed = 1 " +
                "WHERE is_closed = 0 AND entity_id IN (" + joinedIds + ")";
        updateDB(closeVisitSql);

        String closeScreeningSql = "UPDATE ec_tbleprosy_screening SET is_closed = 1 " +
                "WHERE is_closed = 0 AND base_entity_id IN (" + joinedIds + ")";
        updateDB(closeScreeningSql);
    }

    public static String getTBleprosyFollowUpVisit(String baseEntityId) {
        String sql = "SELECT follow_up_reason FROM ec_tbleprosy_followup_visit p " +
                " WHERE p.is_closed = 0 AND p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> {
            return getCursorValue(cursor, "follow_up_reason");
        };

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static FollowUpVisit getLatestFollowUpVisit(String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId)) {
            return null;
        }

        String sql = "SELECT last_interacted_with, follow_up_reason, follow_up_outcome, " +
                "reason_client_not_found, returned_to_treatment, tb_treatment_start_date, service_access_challenges, " +
                "reasons_for_not_returning_to_services_while_not_facing_challenges, client_challenge_types, " +
                "health_facility_challenges_detail, return_to_treatment_prompt " +
                "FROM ec_tbleprosy_followup_visit " +
                "WHERE is_closed = 0 AND entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<FollowUpVisit> dataMap = cursor -> new FollowUpVisit(
                getCursorValue(cursor, "last_interacted_with", ""),
                getCursorValue(cursor, "follow_up_reason", ""),
                getCursorValue(cursor, "follow_up_outcome", ""),
                getCursorValue(cursor, "reason_client_not_found", ""),
                getCursorValue(cursor, "returned_to_treatment", ""),
                getCursorValue(cursor, "tb_treatment_start_date", ""),
                getCursorValue(cursor, "service_access_challenges", ""),
                getCursorValue(cursor, "reasons_for_not_returning_to_services_while_not_facing_challenges", ""),
                getCursorValue(cursor, "client_challenge_types", ""),
                getCursorValue(cursor, "health_facility_challenges_detail", ""),
                getCursorValue(cursor, "return_to_treatment_prompt", "")
        );

        List<FollowUpVisit> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }
        return null;
    }

    public static boolean hasFollowUpVisitWithTreatmentStartDate(String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId)) {
            return false;
        }

        String sql = "SELECT count(entity_id) count FROM ec_tbleprosy_followup_visit " +
                "WHERE is_closed = 0 AND entity_id = '" + baseEntityId + "' AND ifnull(tb_treatment_start_date, '') <> ''";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1) {
            return false;
        }

        Integer count = res.get(0);
        return count != null && count > 0;
    }

    public static String getTBleprosyVisit(String baseEntityId) {
        String sql = "SELECT has_sample_been_collected FROM ec_tbleprosy_visit p " +
                " WHERE p.is_closed = 0 AND p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> {
            return getCursorValue(cursor, "has_sample_been_collected");
        };

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static boolean hasTbLeprosyVisit(String baseEntityId) {
        String sql = "SELECT count(p.entity_id) count FROM ec_tbleprosy_visit p " +
                " WHERE p.is_closed = 0 AND p.entity_id = '" + baseEntityId + "'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1) {
            return false;
        }
        Integer count = res.get(0);
        return count != null && count > 0;
    }

    public static Date getTbLeprosyTestDate(String baseEntityID) {
        String sql = "select tbleprosy_test_date from ec_tbleprosy_screening where is_closed = 0 AND base_entity_id = '" + baseEntityID + "'";

        DataMap<Date> dataMap = cursor -> getCursorValueAsDate(cursor, "tbleprosy_test_date", getNativeFormsDateFormat());

        List<Date> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static String getClientTbLeprosyID(String baseEntityId) {
        String sql = "SELECT tbleprosy_client_id FROM ec_tbleprosy_screening p " +
                " WHERE p.is_closed = 0 AND p.base_entity_id = '" + baseEntityId + "' ORDER BY enrollment_date DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "tbleprosy_client_id");

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getIndexClientIdForContact(String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId)) {
            return null;
        }

        String sql = "SELECT index_client_id FROM ec_tbleprosy_contacts WHERE is_closed = 0 AND base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "index_client_id");

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return null;
    }

    public static ClientNumberInfo getIndexClientNumbers(String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId)) {
            return null;
        }

        String sql = "SELECT tb_client_number, leprosy_client_number FROM ec_tbleprosy_screening WHERE is_closed = 0 AND base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<ClientNumberInfo> dataMap = cursor -> new ClientNumberInfo(
                getCursorValue(cursor, "tb_client_number"),
                getCursorValue(cursor, "leprosy_client_number")
        );

        List<ClientNumberInfo> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }
        return null;
    }

    public static String getEnrollmentDate(String baseEntityId) {
        String sql = "SELECT enrollment_date FROM ec_tbleprosy_screening p " +
                " WHERE p.is_closed = 0 AND p.base_entity_id = '" + baseEntityId + "' ORDER BY enrollment_date DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "enrollment_date");

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static int getVisitNumber(String baseEntityID) {
        String sql = "SELECT visit_number  FROM ec_tbleprosy_follow_up_visit WHERE is_closed = 0 AND entity_id='" + baseEntityID + "' ORDER BY visit_number DESC LIMIT 1";
        DataMap<Integer> map = cursor -> getCursorIntValue(cursor, "visit_number");
        List<Integer> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0) + 1;
        } else
            return 0;
    }

    public static boolean isRegisteredForTbLeprosy(String baseEntityID) {
        String sql = "SELECT count(p.base_entity_id) count FROM ec_tbleprosy_screening p " +
                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.is_closed = 0  AND screening_status != '-' ";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static Integer getTbLeprosyFamilyMembersCount(String familyBaseEntityId) {
        String sql = "SELECT count(emc.base_entity_id) count FROM ec_tbleprosy_screening emc " +
                "INNER Join ec_family_member fm on fm.base_entity_id = emc.base_entity_id " +
                "WHERE fm.relational_id = '" + familyBaseEntityId + "' AND fm.is_closed = 0 " +
                "AND emc.is_closed = 0 AND emc.tbleprosy = 1";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.isEmpty())
            return 0;
        return res.get(0);
    }

    public static MemberObject getMember(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "f.base_entity_id as family_base_entity_id, " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_tbleprosy_screening mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.is_closed = 0 AND f.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static MemberObject getContact(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "f.base_entity_id as family_base_entity_id, " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_tbleprosy_contacts mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.is_closed = 0 AND f.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static List<MemberObject> getMembers() {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "f.base_entity_id as family_base_entity_id, " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_tbleprosy_screening mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.is_closed = 0 AND f.is_closed = 0 ";

        return readData(sql, memberObjectMap);
    }

    public static class ClientNumberInfo {
        private final String tbClientNumber;
        private final String leprosyClientNumber;

        public ClientNumberInfo(String tbClientNumber, String leprosyClientNumber) {
            this.tbClientNumber = tbClientNumber;
            this.leprosyClientNumber = leprosyClientNumber;
        }

        public String getTbClientNumber() {
            return tbClientNumber;
        }

        public String getLeprosyClientNumber() {
            return leprosyClientNumber;
        }
    }

    public static class FollowUpVisit {
        private final String lastInteractedWith;
        private final String followUpReason;
        private final String followUpOutcome;
        private final String reasonClientNotFound;
        private final String returnedToTreatment;
        private final String tbTreatmentStartDate;
        private final String serviceAccessChallenges;
        private final String reasonsForNotReturningWithoutChallenges;
        private final String clientChallengeTypes;
        private final String healthFacilityChallengesDetail;
        private final String returnToTreatmentPrompt;

        public FollowUpVisit(String lastInteractedWith, String followUpReason, String followUpOutcome, String reasonClientNotFound,
                             String returnedToTreatment, String tbTreatmentStartDate, String serviceAccessChallenges,
                             String reasonsForNotReturningWithoutChallenges, String clientChallengeTypes,
                             String healthFacilityChallengesDetail, String returnToTreatmentPrompt) {
            this.lastInteractedWith = lastInteractedWith;
            this.followUpReason = followUpReason;
            this.followUpOutcome = followUpOutcome;
            this.reasonClientNotFound = reasonClientNotFound;
            this.returnedToTreatment = returnedToTreatment;
            this.tbTreatmentStartDate = tbTreatmentStartDate;
            this.serviceAccessChallenges = serviceAccessChallenges;
            this.reasonsForNotReturningWithoutChallenges = reasonsForNotReturningWithoutChallenges;
            this.clientChallengeTypes = clientChallengeTypes;
            this.healthFacilityChallengesDetail = healthFacilityChallengesDetail;
            this.returnToTreatmentPrompt = returnToTreatmentPrompt;
        }

        public String getLastInteractedWith() {
            return lastInteractedWith;
        }

        public String getFollowUpReason() {
            return followUpReason;
        }

        public String getFollowUpOutcome() {
            return followUpOutcome;
        }

        public String getReasonClientNotFound() {
            return reasonClientNotFound;
        }

        public String getReturnedToTreatment() {
            return returnedToTreatment;
        }

        public String getTbTreatmentStartDate() {
            return tbTreatmentStartDate;
        }

        public String getServiceAccessChallenges() {
            return serviceAccessChallenges;
        }

        public String getReasonsForNotReturningWithoutChallenges() {
            return reasonsForNotReturningWithoutChallenges;
        }

        public String getClientChallengeTypes() {
            return clientChallengeTypes;
        }

        public String getHealthFacilityChallengesDetail() {
            return healthFacilityChallengesDetail;
        }

        public String getReturnToTreatmentPrompt() {
            return returnToTreatmentPrompt;
        }
    }

    public static class ObservationResults {
        private final String lastInteractedWith;
        private final String investigationType;
        private final String tbDiagnosticTestType;
        private final String tbSampleTestResults;
        private final String clinicalDecision;
        private final String leprosyDiagnosticMethod;
        private final String tbTreatmentStartDate;
        private final String leprosyTreatmentStartDate;
        private final String leprosyInvestigationResults;
        private final boolean poorQualitySample;

        public ObservationResults(String lastInteractedWith, String investigationType, String tbDiagnosticTestType, String tbSampleTestResults,
                                  String clinicalDecision, String leprosyDiagnosticMethod, String tbTreatmentStartDate,
                                  String leprosyTreatmentStartDate, String leprosyInvestigationResults, boolean poorQualitySample) {
            this.lastInteractedWith = lastInteractedWith;
            this.investigationType = investigationType;
            this.tbDiagnosticTestType = tbDiagnosticTestType;
            this.tbSampleTestResults = tbSampleTestResults;
            this.clinicalDecision = clinicalDecision;
            this.leprosyDiagnosticMethod = leprosyDiagnosticMethod;
            this.tbTreatmentStartDate = tbTreatmentStartDate;
            this.leprosyTreatmentStartDate = leprosyTreatmentStartDate;
            this.leprosyInvestigationResults = leprosyInvestigationResults;
            this.poorQualitySample = poorQualitySample;
        }

        public String getLastInteractedWith() {
            return lastInteractedWith;
        }

        public String getInvestigationType() {
            return investigationType;
        }

        public String getTbDiagnosticTestType() {
            return tbDiagnosticTestType;
        }

        public String getTbSampleTestResults() {
            return tbSampleTestResults;
        }

        public String getClinicalDecision() {
            return clinicalDecision;
        }

        public String getLeprosyDiagnosticMethod() {
            return leprosyDiagnosticMethod;
        }

        public String getTbTreatmentStartDate() {
            return tbTreatmentStartDate;
        }

        public String getLeprosyTreatmentStartDate() {
            return leprosyTreatmentStartDate;
        }

        public String getLeprosyInvestigationResults() {
            return leprosyInvestigationResults;
        }

        public boolean isPoorQualitySample() {
            return poorQualitySample;
        }
    }

}
