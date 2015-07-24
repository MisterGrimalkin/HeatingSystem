package net.amarantha.heating.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class Now {

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private long offset = 0;

    public Date now() {
        return new Date(currentTimeMillis() + offset);
    }

    public Date nowTimeOnly() {
        return timeOnly(now());
    }

    public static Date timeOnly(Date d) {
        try {
            return sdf.parse(sdf.format(d));
        } catch (ParseException e) {}
        return null;
    }

    public void pushNow(int minutes) {
        offset += (minutes*60000);
    }

    public void setNowTime(String time) {
        try {
            Date d = sdf.parse(time);
            offset = d.getTime() - currentTimeMillis();
        } catch (ParseException e) {}
    }

}
