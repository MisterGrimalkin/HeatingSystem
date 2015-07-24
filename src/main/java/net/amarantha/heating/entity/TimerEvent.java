package net.amarantha.heating.entity;

import java.util.Date;

public class TimerEvent implements Comparable<TimerEvent> {

    private String id;
    private Status type;
    private Date time;

    public TimerEvent(String id, Status type, Date time) {
        this.id = id;
        this.type = type;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getType() {
        return type;
    }

    public void setType(Status type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public int compareTo(TimerEvent o) {
        if ( o!=null && o.getTime()!=null && time!=null ) {
            return time.compareTo(o.getTime());
        }
        return 0;
    }
}
