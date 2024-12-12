package org.smartregister.chw.tbleprosy.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.smartregister.chw.tbleprosy.domain.Visit;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.chw.tbleprosy.util.DBConstants;
import org.smartregister.repository.BaseRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class VisitRepository extends BaseRepository {

    public static final String VISIT_TABLE = "visits";
    private static final String VISIT_ID = "visit_id";
    private static final String VISIT_TYPE = "visit_type";
    private static final String VISIT_GROUP = "visit_group";
    private static final String PARENT_VISIT_ID = "parent_visit_id";
    public static final String PARENT_VISIT_ID_INDEX = "CREATE INDEX " + VISIT_TABLE + "_" + PARENT_VISIT_ID + "_index ON " + VISIT_TABLE
            + "(" + PARENT_VISIT_ID + " COLLATE NOCASE );";
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String VISIT_DATE = "visit_date";
    private static final String VISIT_JSON = "visit_json";
    private static final String PRE_PROCESSED = "pre_processed";
    private static final String FORM_SUBMISSION_ID = "form_submission_id";
    private static final String PROCESSED = "processed";
    private static final String UPDATED_AT = "updated_at";
    private static final String CREATED_AT = "created_at";
    private static final String CREATE_VISIT_TABLE =
            "CREATE TABLE " + VISIT_TABLE + "("
                    + VISIT_ID + " VARCHAR NULL, "
                    + VISIT_TYPE + " VARCHAR NULL, "
                    + PARENT_VISIT_ID + " VARCHAR NULL, "
                    + BASE_ENTITY_ID + " VARCHAR NULL, "
                    + VISIT_DATE + " VARCHAR NULL, "
                    + VISIT_JSON + " VARCHAR NULL, "
                    + PRE_PROCESSED + " VARCHAR NULL, "
                    + FORM_SUBMISSION_ID + " VARCHAR NULL, "
                    + PROCESSED + " Integer NULL, "
                    + UPDATED_AT + " DATETIME NULL, "
                    + CREATED_AT + " DATETIME NULL)";
    private static final String BASE_ENTITY_ID_INDEX = "CREATE INDEX " + VISIT_TABLE + "_" + BASE_ENTITY_ID + "_index ON " + VISIT_TABLE
            + "("
            + BASE_ENTITY_ID + " COLLATE NOCASE , "
            + VISIT_TYPE + " COLLATE NOCASE , "
            + VISIT_DATE + " COLLATE NOCASE"
            + ");";
    private String[] VISIT_COLUMNS = {VISIT_ID, VISIT_TYPE, VISIT_GROUP, PARENT_VISIT_ID, BASE_ENTITY_ID, VISIT_DATE, VISIT_JSON, PRE_PROCESSED, FORM_SUBMISSION_ID, PROCESSED, UPDATED_AT, CREATED_AT};

    public static String ADD_VISIT_GROUP_COLUMN = "ALTER TABLE " + VISIT_TABLE + " ADD COLUMN " + VISIT_GROUP + " VARCHAR;";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_VISIT_TABLE);
        database.execSQL(BASE_ENTITY_ID_INDEX);
        database.execSQL(PARENT_VISIT_ID_INDEX);
    }

    private ContentValues createValues(Visit visit) {
        ContentValues values = new ContentValues();
        values.put(VISIT_ID, visit.getVisitId());
        values.put(VISIT_TYPE, visit.getVisitType());
        values.put(VISIT_GROUP, visit.getVisitGroup());
        values.put(PARENT_VISIT_ID, visit.getParentVisitID());
        values.put(BASE_ENTITY_ID, visit.getBaseEntityId());
        values.put(VISIT_DATE, visit.getDate() != null ? visit.getDate().getTime() : null);
        values.put(VISIT_JSON, visit.getJson());
        values.put(PRE_PROCESSED, visit.getPreProcessedJson());
        values.put(FORM_SUBMISSION_ID, visit.getFormSubmissionId());
        values.put(PROCESSED, visit.getProcessed());
        values.put(UPDATED_AT, visit.getUpdatedAt().getTime());
        values.put(CREATED_AT, visit.getCreatedAt().getTime());
        return values;
    }

    public void addVisit(Visit visit) {
        addVisit(visit, getWritableDatabase());
    }

    public void addVisit(Visit visit, SQLiteDatabase database) {
        if (visit == null) {
            return;
        }
        // Handle updated home visit details
        database.insert(VISIT_TABLE, null, createValues(visit));
        EventBus.getDefault().post(visit);
    }

    public String getParentVisitEventID(String baseEntityID, String parentEventType, Date eventDate) {
        if (StringUtils.isBlank(baseEntityID) || StringUtils.isBlank(parentEventType) || eventDate == null)
            return null;

        String visitID = null;
        Cursor cursor = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String sql = "select " + VISIT_ID + " from visits where base_entity_id = ? COLLATE NOCASE and visit_type = ? COLLATE NOCASE and strftime('%Y-%m-%d',visit_date / 1000, 'unixepoch') = ? ";
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{baseEntityID, parentEventType, sdf.format(eventDate)});
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    visitID = cursor.getString(cursor.getColumnIndex(VISIT_ID));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return visitID;
    }

    public void deleteVisit(String visitID) {
        try {
            getWritableDatabase().delete(VISIT_TABLE, VISIT_ID + "= ?", new String[]{visitID});
            getWritableDatabase().delete(VisitDetailsRepository.VISIT_DETAILS_TABLE, VISIT_ID + "= ?", new String[]{visitID});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void completeProcessing(String visitID) {
        try {
            ContentValues values = new ContentValues();
            values.put(PROCESSED, 1);
            values.put(PRE_PROCESSED, "");
            getWritableDatabase().update(VISIT_TABLE, values, VISIT_ID + " = ?", new String[]{visitID});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public List<Visit> readVisits(Cursor cursor) {
        List<Visit> visits = new ArrayList<>();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Visit visit = new Visit();
                    visit.setVisitId(cursor.getString(cursor.getColumnIndex(VISIT_ID)));
                    visit.setVisitType(cursor.getString(cursor.getColumnIndex(VISIT_TYPE)));
                    visit.setVisitGroup(cursor.getString(cursor.getColumnIndex(VISIT_GROUP)));
                    visit.setParentVisitID(cursor.getString(cursor.getColumnIndex(PARENT_VISIT_ID)));
                    visit.setPreProcessedJson(cursor.getString(cursor.getColumnIndex(PRE_PROCESSED)));
                    visit.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
                    visit.setDate(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(VISIT_DATE)))));
                    visit.setJson(cursor.getString(cursor.getColumnIndex(VISIT_JSON)));
                    visit.setFormSubmissionId(cursor.getString(cursor.getColumnIndex(FORM_SUBMISSION_ID)));
                    visit.setProcessed(cursor.getInt(cursor.getColumnIndex(PROCESSED)) == 1);
                    visit.setCreatedAt(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CREATED_AT)))));
                    visit.setUpdatedAt(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))));

                    visits.add(visit);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return visits;
    }

    public List<Visit> getAllUnSynced(Long last_edit_time) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, PROCESSED + " = ? AND UPDATED_AT <= ? ", new String[]{"0", last_edit_time.toString()}, null, null, CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getAllUnSynced() {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, PROCESSED + " = ? ", new String[]{"0"}, null, null, CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getAllVisitsProcessed(String visitType, String baseEntityId) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, PROCESSED + " = ? AND visit_type = ?  AND base_entity_id = ?", new String[]{"1", visitType, baseEntityId}, null, null, CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }



    public List<Visit> getAllUnSynced(Long last_edit_time, String baseEntityID) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(
                    VISIT_TABLE, VISIT_COLUMNS, PROCESSED + " = ? AND UPDATED_AT <= ? AND " + BASE_ENTITY_ID + " = ? ",
                    new String[]{"0", last_edit_time.toString(), baseEntityID}, null, null,
                    CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getVisits(String baseEntityID, String visitType) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, BASE_ENTITY_ID + " = ? AND " + VISIT_TYPE + " = ? ", new String[]{baseEntityID, visitType}, null, null, CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getVisitsByGroup(String visitGroup) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, VISIT_GROUP + " = ? ", new String[]{visitGroup}, null, null, CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getUniqueDayLatestThreeVisits(String baseEntityID, String visitType) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "select STRFTIME('%Y%m%d', datetime((" + VISIT_DATE + ")/1000,'unixepoch')) as d,* from " + VISIT_TABLE + " where " + VISIT_TYPE + " = '" + visitType + "' AND " +
                    "" + BASE_ENTITY_ID + " = '" + baseEntityID + "'  group by d order by " + VISIT_DATE + " desc limit 3";
            cursor = getReadableDatabase().rawQuery(query, null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getVisitsByVisitId(String visitID) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, VISIT_ID + " = ? ", new String[]{visitID}, null, null, VISIT_DATE + " DESC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public Visit getVisitByVisitId(String visitID) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, VISIT_ID + " = ? ", new String[]{visitID}, null, null, VISIT_DATE + " DESC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits.size() == 1 ? visits.get(0) : null;
    }

    public List<Visit> getChildEvents(String visitID) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, PARENT_VISIT_ID + " = ? ", new String[]{visitID}, null, null, VISIT_DATE + " DESC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public Visit getVisitByFormSubmissionID(String formSubmissionID) {
        if (StringUtils.isBlank(formSubmissionID))
            return null;

        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, FORM_SUBMISSION_ID + " = ? ", new String[]{formSubmissionID}, null, null, VISIT_DATE + " DESC ", "1");
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return (visits.size() > 0) ? visits.get(0) : null;
    }

    public Visit getLatestVisit(String baseEntityID, String visitType) {
        return getLatestVisit(baseEntityID, visitType, null);
    }

    public Visit getLatestVisit(String baseEntityID, String visitType, SQLiteDatabase sqLiteDatabase) {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = getReadableDatabase();
        }
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(VISIT_TABLE, VISIT_COLUMNS, BASE_ENTITY_ID + " = ? AND " + VISIT_TYPE + " = ? ", new String[]{baseEntityID, visitType}, null, null, VISIT_DATE + " DESC ", "1");
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return (visits.size() > 0) ? visits.get(0) : null;
    }

    public void setNotVisitingDate(String date, String baseID) {
        try {
            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.VISIT_NOT_DONE, date);
            getWritableDatabase().update(Constants.TABLES.TBLEPROSY_ENROLLMENT, values, DBConstants.KEY.BASE_ENTITY_ID + " = ?", new String[]{baseID});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public String getLastInteractedWithAndVisitNotDone(String baseEntityID, String dateColumn) {
        SQLiteDatabase database = getReadableDatabase();
        net.sqlcipher.Cursor cursor = null;
        try {
            if (database == null) {
                return null;
            }

            cursor = database.query(Constants.TABLES.TBLEPROSY_ENROLLMENT, new String[]{dateColumn}, BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE, new String[]{baseEntityID}, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                String date = cursor.getString(cursor.getColumnIndex(dateColumn));
                return date;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;

    }
}
