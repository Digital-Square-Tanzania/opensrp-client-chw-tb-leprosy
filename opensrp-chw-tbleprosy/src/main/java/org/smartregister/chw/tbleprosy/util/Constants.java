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
        String TB_LEPROSY_SCREENING = "TBLeprosy Screening";
        String TB_LEPROSY_SERVICES = "TbLeprosy Services";
        String TB_LEPROSY_FOLLOW_UP_VISIT = "TbLeprosy Follow-up Visit";
        String VOID_EVENT = "Void Event";
        String CLOSE_TB_LEPROSY_SERVICE = "Close TbLeprosy Service";
        String TB_LEPROSY_MOBILIZATION = "TbLeprosy Mobilization Session";
        String TBLEPROSY_CONTACT_VISIT = "TBLeprosy Contact Visit";
        String TB_LEPROSY_OBSERVATIONS_RESULT = "TBLEPROSY Contact Observation Results";
        String TB_LEPROSY_RECORD_VISIT = "TBLeprosy Visit";
        String TB_LEPROSY_CLIENT_OBSERVATION = "TBLEPROSY Observation Results";
        String TBLEPROSY_CONTACTS = "TbLeprosy Contact Registration";

    }

    interface FORMS {
        String TB_LEPROSY_SCREENING = "tbleprosy_screening";
        String TBLEPROSY_MOBILIZATION_SESSION = "tbleprosy_mobilization_session";
        String TBLEPROSY_CONTACT_REGISTRATION = "tbleprosy_contact_registration";
        String RECORD_TBLEPROSY_VISIT = "tbleprosy_record_visit";
        String OBSERVATION_RESULTS = "tbleprosy_observation_results";
        String CONTACT_OBSERVATION_RESULTS = "tbleprosy_contact_observation_results";
        String TBLEPROSY_FOLLOWUP_VISIT = "tbleprosy_followup_visit";

    }

    interface TbLeprosy_FOLLOWUP_FORMS {

        String TBLEPROSY_CONTACT_TB_INVESTIGATION = "tb_leprosy_contact_tb_investigation";
        String TBLEPROSY_CONTACT_LEPROSY_INVESTIGATION = "tb_leprosy_contact_leprosy_investigation";
        String TBLEPROSY_INDEX_CLIENT_DETAILS_SOURCE = "tbleprosy_index_client_details";
        String TBLEPROSY_SAMPLE = "tb_leprosy_sample";
        String MEDICAL_HISTORY = "tbleprosy_service_medical_history";
        String PHYSICAL_EXAMINATION = "tbleprosy_service_physical_examination";
        String HTS = "tbleprosy_service_hts";
    }

    interface TABLES {
        String TBLEPROSY_SCREENING = "ec_tbleprosy_screening";
        String TBLEPROSY_CONTACTS = "ec_tbleprosy_contacts";
        String TBLEPROSY_SERVICES = "ec_tbleprosy_services";
        String TBLEPROSY_MOBILIZATION = "ec_tbleprosy_mobilization";
    }

    interface ACTIVITY_PAYLOAD {
        String BASE_ENTITY_ID = "BASE_ENTITY_ID";
        String FAMILY_BASE_ENTITY_ID = "FAMILY_BASE_ENTITY_ID";
        String TB_LEPROSY_FORM_NAME = "TBLEPROSY_FORM_NAME";
        String MEMBER_PROFILE_OBJECT = "MemberObject";
        String EDIT_MODE = "editMode";
        String PROFILE_TYPE = "profile_type";

    }

    interface ACTIVITY_PAYLOAD_TYPE {
        String REGISTRATION = "REGISTRATION";
        String FOLLOW_UP_VISIT = "FOLLOW_UP_VISIT";
    }

    interface CONFIGURATION {
        String TBLEPROSY_ENROLLMENT = "tbleprosy_screening";
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
