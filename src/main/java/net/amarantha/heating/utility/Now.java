package net.amarantha.heating.utility;

import javax.inject.Singleton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

@Singleton
public class Now {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public Date now() {
        return new Date(currentTimeMillis() + offset);
    }

    public Date date() {
        return dateOnly(now());
    }

    public Date time() {
        return timeOnly(now());
    }


    // Time/Date Override

    public void pushNow(int milliseconds) {
        offset += milliseconds;
    }

    public void pushNowMinutes(int minutes) {
        pushNow(minutes * 60000);
    }

    public void setDate(String date) {
        try {
            String currentTime = TIME_FORMAT.format(timeOnly(new Date()));
            Date d = DATE_TIME_FORMAT.parse(date + " " + currentTime);
            offset = d.getTime() - currentTimeMillis();
        } catch (ParseException e) {}
    }

    public void setTime(String time) {
        try {
            Date d = TIME_FORMAT.parse(time);
            offset = d.getTime() - currentTimeMillis();
        } catch (ParseException e) {}
    }

    private long offset = 0;


    // Static Utilities

    public static Date dateOnly(Date d) {
        try {
            return DATE_FORMAT.parse(DATE_FORMAT.format(d));
        } catch (ParseException e) {}
        return null;
    }

    public static Date timeOnly(Date d) {
        try {
            return TIME_FORMAT.parse(TIME_FORMAT.format(d));
        } catch (ParseException e) {}
        return null;
    }

    public static long minutesAsMilliseconds(long minutes) {
        return minutes * 60 * 1000;
    }

    public static long hoursAsMilliseconds(long hours) {
        return hours * 60 * 60 * 1000;
    }

}
