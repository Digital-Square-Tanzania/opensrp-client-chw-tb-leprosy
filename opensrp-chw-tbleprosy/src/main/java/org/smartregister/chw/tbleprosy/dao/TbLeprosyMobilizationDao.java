package org.smartregister.chw.tbleprosy.dao;


import android.annotation.SuppressLint;

import org.smartregister.chw.tbleprosy.model.TbLeprosyMobilizationModel;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.chw.tbleprosy.util.DBConstants;
import org.smartregister.dao.AbstractDao;

import java.util.List;


public class TbLeprosyMobilizationDao extends AbstractDao {

    public static void updateData(String baseEntityID, String mobilization_date, String female_clients_reached, String male_clients_reached) {
        String sql = "INSERT INTO ec_tbleprosy_mobilization " +
                "           (id, mobilization_date, female_clients_reached, male_clients_reached) " +
                "           VALUES (" +
                "                   '" + baseEntityID + "', " +
                "                   '" + mobilization_date + "', " +
                "                   '" + female_clients_reached + "', " +
                "                   '" + male_clients_reached + "') " +
                " ON CONFLICT (id) DO UPDATE SET mobilization_date = EXCLUDED.mobilization_date, " +
                "                               female_clients_reached = EXCLUDED.female_clients_reached, " +
                "                               male_clients_reached = EXCLUDED.male_clients_reached;";

        updateDB(sql);
    }

    public static List<TbLeprosyMobilizationModel> getMobilizationSessions() {
        String sql = "SELECT *,  substr(mobilization_date, 7, 4)||'-'|| " +
                "                substr(mobilization_date, 4,2)||'-'|| " +
                "                substr(mobilization_date, 1,2) as orderDate FROM " + Constants.TABLES.TBLEPROSY_MOBILIZATION + " ORDER BY julianday(orderDate)  DESC";

        @SuppressLint("Range") DataMap<TbLeprosyMobilizationModel> dataMap = cursor -> {
            TbLeprosyMobilizationModel tbLeprosyMobilizationModel = new TbLeprosyMobilizationModel();
            tbLeprosyMobilizationModel.setSessionId(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID)));
            tbLeprosyMobilizationModel.setSessionDate(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.MOBILIZATION_DATE)));
            tbLeprosyMobilizationModel.setSessionParticipants(computeSessionParticipants(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FEMALE_CLIENTS_REACHED)), cursor.getString(cursor.getColumnIndex(DBConstants.KEY.MALE_CLIENTS_REACHED))));
            return tbLeprosyMobilizationModel;
        };

        List<TbLeprosyMobilizationModel> res = readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return null;
        return res;
    }

    private static String computeSessionParticipants(String femaleParticipants, String maleParticipants) {
        int sum = Integer.parseInt(femaleParticipants) + Integer.parseInt(maleParticipants);
        return String.valueOf(sum);
    }

    private static String computeCondomsIssued(String femaleCondoms, String maleCondoms) {
        int sum = Integer.parseInt(femaleCondoms) + Integer.parseInt(maleCondoms);
        return String.valueOf(sum);
    }


}
