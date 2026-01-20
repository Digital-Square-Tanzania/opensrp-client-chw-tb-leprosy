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
        memberObject.setAge(getCursorValue(cursor, "dob"));
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
        String sql = "SELECT status FROM " + tableName + " WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "status");
        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }
        return null;
    }

    public static String getTbScreeningStatus(String baseEntityId) {
        String sql = "SELECT screening_status FROM ec_tbleprosy_screening WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

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
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

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
        String sql = "SELECT tb_sample_test_results, clinical_decision, leprosy_investigation_results FROM ec_tbleprosy_observation_results p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<ObservationResults> dataMap = cursor -> {
            String tbSampleTestResults = getCursorValue(cursor, "tb_sample_test_results", "");
            String clinicalDecision = getCursorValue(cursor, "clinical_decision", "");
            String leprosyInvestigationResults = getCursorValue(cursor, "leprosy_investigation_results", "");
            boolean poorQualitySample = StringUtils.containsIgnoreCase(tbSampleTestResults, "poor_quality_sample");
            return new ObservationResults(tbSampleTestResults, clinicalDecision, leprosyInvestigationResults, poorQualitySample);
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
                "WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

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
     * only when TB results are negative and leprosy has not been confirmed positive.
     */
    public static void closeTbNegativeClients() {
        String sql = "UPDATE ec_tbleprosy_screening SET is_closed = 1 " +
                "WHERE is_closed = 0 AND base_entity_id IN (" +
                "  SELECT scr.base_entity_id FROM ec_tbleprosy_screening scr " +
                "  INNER JOIN (" +
                "    SELECT latest.base_entity_id, latest.last_interacted_with, " +
                "           lower(ifnull(latest.tb_sample_test_results, '')) AS tb_result, " +
                "           lower(ifnull(latest.clinical_decision, '')) AS clinical_result, " +
                "           lower(ifnull(latest.leprosy_investigation_results, '')) AS leprosy_result " +
                "    FROM ec_tbleprosy_observation_results latest " +
                "    WHERE latest.last_interacted_with = (" +
                "      SELECT MAX(inner_tbl.last_interacted_with) FROM ec_tbleprosy_observation_results inner_tbl " +
                "      WHERE inner_tbl.base_entity_id = latest.base_entity_id" +
                "    )" +
                "  ) latest ON scr.base_entity_id = latest.base_entity_id " +
                "  WHERE scr.is_closed = 0 " +
                "    AND scr.last_interacted_with < latest.last_interacted_with " +
                "    AND (latest.tb_result = 'tb_dr_tb_undetected' OR latest.clinical_result = 'non_suggestive') " +
                "    AND latest.leprosy_result != 'leprosy_confirmed'" +
                ")";

        updateDB(sql);
    }

    public static String getTBleprosyFollowUpVisit(String baseEntityId) {
        String sql = "SELECT follow_up_reason FROM ec_tbleprosy_followup_visit p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> {
            return getCursorValue(cursor, "follow_up_reason");
        };

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getTBleprosyVisit(String baseEntityId) {
        String sql = "SELECT has_sample_been_collected FROM ec_tbleprosy_visit p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

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
                " WHERE p.entity_id = '" + baseEntityId + "'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1) {
            return false;
        }
        Integer count = res.get(0);
        return count != null && count > 0;
    }

    public static Date getTbLeprosyTestDate(String baseEntityID) {
        String sql = "select tbleprosy_test_date from ec_tbleprosy_screening where base_entity_id = '" + baseEntityID + "'";

        DataMap<Date> dataMap = cursor -> getCursorValueAsDate(cursor, "tbleprosy_test_date", getNativeFormsDateFormat());

        List<Date> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static String getClientTbLeprosyID(String baseEntityId) {
        String sql = "SELECT tbleprosy_client_id FROM ec_tbleprosy_screening p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY enrollment_date DESC LIMIT 1";

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

        String sql = "SELECT index_client_id FROM ec_tbleprosy_contacts WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

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

        String sql = "SELECT tb_client_number, leprosy_client_number FROM ec_tbleprosy_screening WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

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
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY enrollment_date DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "enrollment_date");

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static int getVisitNumber(String baseEntityID) {
        String sql = "SELECT visit_number  FROM ec_tbleprosy_follow_up_visit WHERE entity_id='" + baseEntityID + "' ORDER BY visit_number DESC LIMIT 1";
        DataMap<Integer> map = cursor -> getCursorIntValue(cursor, "visit_number");
        List<Integer> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0) + 1;
        } else
            return 0;
    }

    public static boolean isRegisteredForTbLeprosy(String baseEntityID) {
        String sql = "SELECT count(p.base_entity_id) count FROM ec_tbleprosy_screening p " +
                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.is_closed = 0 ";

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
                "where mr.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
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
                "where mr.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
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
                "where mr.is_closed = 0 ";

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

    public static class ObservationResults {
        private final String tbSampleTestResults;
        private final String clinicalDecision;
        private final String leprosyInvestigationResults;
        private final boolean poorQualitySample;

        public ObservationResults(String tbSampleTestResults, String clinicalDecision, String leprosyInvestigationResults, boolean poorQualitySample) {
            this.tbSampleTestResults = tbSampleTestResults;
            this.clinicalDecision = clinicalDecision;
            this.leprosyInvestigationResults = leprosyInvestigationResults;
            this.poorQualitySample = poorQualitySample;
        }

        public String getTbSampleTestResults() {
            return tbSampleTestResults;
        }

        public String getClinicalDecision() {
            return clinicalDecision;
        }

        public String getLeprosyInvestigationResults() {
            return leprosyInvestigationResults;
        }

        public boolean isPoorQualitySample() {
            return poorQualitySample;
        }
    }

}
