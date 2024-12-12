package org.smartregister.chw.tbleprosy.util;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    /**
     * This method gets the elapsed days from a given date
     *
     * @param startDate starting date that would be truncated to midnight
     * @return number of days elapsed from the starting date to the current date regardless of time
     */
    public static int getElapsedDays(Date startDate) {
        Calendar startDateCal = Calendar.getInstance();
        startDateCal.setTime(startDate);

        Calendar nowCal = Calendar.getInstance();
        nowCal.set(nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), nowCal.get(Calendar.DATE), 0, 0, 0);


        Calendar startDateCalTruncated = Calendar.getInstance();
        startDateCalTruncated.set(startDateCal.get(Calendar.YEAR), startDateCal.get(Calendar.MONTH), startDateCal.get(Calendar.DATE), 0, 0, 0);

        return Days.daysBetween(new DateTime(startDateCalTruncated.getTimeInMillis()), new DateTime(nowCal.getTimeInMillis())).getDays();

    }
}
