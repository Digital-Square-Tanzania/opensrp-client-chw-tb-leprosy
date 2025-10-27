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

    public static String getTBleprosyObservationResults(String baseEntityId) {
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

    public static String getTBleprosyContactObservationResults(String baseEntityId) {
        String sql = "SELECT tb_investigation_results, matokeo_ya_uchunguzi_tb FROM ec_tbleprosy_contact_observation_results p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> {
            String results = getCursorValue(cursor, "tb_investigation_results");
            if (StringUtils.isBlank(results)) {
                results = getCursorValue(cursor, "matokeo_ya_uchunguzi_tb");
            }
            return results;
        };

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getTBleprosyFollowUpVisit(String baseEntityId) {
        String sql = "SELECT follow_up_reason, sababu_ya_ufuatiliaji FROM ec_tbleprosy_followup_visit p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> {
            String reason = getCursorValue(cursor, "follow_up_reason");
            if (StringUtils.isBlank(reason)) {
                reason = getCursorValue(cursor, "sababu_ya_ufuatiliaji");
            }
            return reason;
        };

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getTBleprosyVisit(String baseEntityId) {
        String sql = "SELECT has_sample_been_collected, kuchukuliwa_sampuli FROM ec_tbleprosy_visit p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> {
            String collected = getCursorValue(cursor, "has_sample_been_collected");
            if (StringUtils.isBlank(collected)) {
                collected = getCursorValue(cursor, "kuchukuliwa_sampuli");
            }
            return collected;
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

}
