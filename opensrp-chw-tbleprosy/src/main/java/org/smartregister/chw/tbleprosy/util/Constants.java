package org.smartregister.chw.tbleprosy.util;

public interface Constants {

    int REQUEST_CODE_GET_JSON = 2244;
    String ENCOUNTER_TYPE = "encounter_type";
    String STEP_ONE = "step1";
    String STEP_TWO = "step2";
    String TBLEPROSY_VISIT_GROUP = "tbleprosy_visit_group";


    interface JSON_FORM_EXTRA {
        String JSON = "json";
        String ENCOUNTER_TYPE = "encounter_type";
        String EVENT_TYPE = "eventType";
    }

    interface EVENT_TYPE {
        String TB_LEPROSY_ENROLLMENT = "TbLeprosy Enrollment";
        String TB_LEPROSY_SERVICES = "TbLeprosy Services";
        String TB_LEPROSY_FOLLOW_UP_VISIT = "TbLeprosy Follow-up Visit";
        String VOID_EVENT = "Void Event";
        String CLOSE_TB_LEPROSY_SERVICE = "Close TbLeprosy Service";
        String TBLEPROSY_CONTACT_VISIT = "TBLeprosy Contact Visit";
        String TB_LEPROSY_OBSERVATIONS_RESULT = "TBLEPROSY Matokeo ya Uchunguzi Contact";

    }

    interface FORMS {
        String TBLEPROSY_ENROLLMENT = "tbleprosy_enrollment";
        String TBLEPROSY_FOLLOWUP_VISIT = "tbleprosy_followup_visit";
    }

    interface TbLeprosy_FOLLOWUP_FORMS {

        String TBLEPROSY_INVESTIGATION = "tb_leprosy_investigation";
        String TBLEPROSY_SOURCE = "tb_leprosy_source";
        String TBLEPROSY_SAMPLE = "tb_leprosy_sample";
        String MEDICAL_HISTORY = "tbleprosy_service_medical_history";
        String PHYSICAL_EXAMINATION = "tbleprosy_service_physical_examination";
        String HTS = "tbleprosy_service_hts";
    }

    interface TABLES {
        String TBLEPROSY_ENROLLMENT = "ec_tbleprosy_enrollment";
        String TBLEPROSY_SERVICES = "ec_tbleprosy_services";
    }

    interface ACTIVITY_PAYLOAD {
        String BASE_ENTITY_ID = "BASE_ENTITY_ID";
        String FAMILY_BASE_ENTITY_ID = "FAMILY_BASE_ENTITY_ID";
        String TbLeprosy_FORM_NAME = "TbLeprosy_FORM_NAME";
        String MEMBER_PROFILE_OBJECT = "MemberObject";
        String EDIT_MODE = "editMode";
        String PROFILE_TYPE = "profile_type";

    }

    interface ACTIVITY_PAYLOAD_TYPE {
        String REGISTRATION = "REGISTRATION";
        String FOLLOW_UP_VISIT = "FOLLOW_UP_VISIT";
    }

    interface CONFIGURATION {
        String TBLEPROSY_ENROLLMENT = "tbleprosy_enrollment";
    }

    interface TBLEPROSY_MEMBER_OBJECT {
        String MEMBER_OBJECT = "memberObject";
    }

    interface PROFILE_TYPES {
        String TBLEPROSY_PROFILE = "tbleprosy_profile";
    }

    interface VALUES {
        String NONE = "none";
        String CHORDAE = "chordae";
        String HIV = "hiv";
        String RBG = "random_blood_glucose_test";
        String FBG = "fast_blood_glucose_test";
        String HYPERTENSION = "hypertension";
        String SILICON_OR_LEXAN = "silicon_or_lexan";
        String NEGATIVE = "negative";
        String SATISFACTORY = "satisfactory";
        String NEEDS_FOLLOWUP = "needs_followup";
        String YES = "yes";
    }

    interface TABLE_COLUMN {
        String GENITAL_EXAMINATION = "genital_examination";
        String SYSTOLIC = "systolic";
        String DIASTOLIC = "diastolic";
        String ANY_COMPLAINTS = "any_complaints";
        String CLIENT_DIAGNONISED_WITH = "is_client_diagnosed_with_any";
        String COMPLICATION_TYPE = "type_complication";
        String HEMATOLOGICAL_DISEASE_SYMPTOMS = "any_hematological_disease_symptoms";
        String KNOWN_ALLEGIES = "known_allergies";
        String HIV_RESULTS = "hiv_result";
        String HIV_VIRAL_LOAD = "hiv_viral_load_text";
        String TYPE_OF_BLOOD_FOR_GLUCOSE_TEST = "type_of_blood_for_glucose_test";
        String BLOOD_FOR_GLUCOSE = "blood_for_glucose";
        String DISCHARGE_CONDITION = "discharge_condition";
        String IS_MALE_PROCEDURE_CIRCUMCISION_CONDUCTED = "is_male_procedure_circumcision_conducted";
    }

}
