package net.amarantha.heating.service;

import com.google.inject.Singleton;
import net.amarantha.heating.entity.Status;
import net.amarantha.heating.entity.TimerEvent;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.utility.Now;
import net.amarantha.heating.utility.PropertyManager;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static java.util.Collections.sort;
import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;
import static net.amarantha.heating.utility.Now.*;
import static net.sf.json.JSONObject.fromObject;
import static net.sf.json.JSONObject.toBean;

@Singleton
public class HeatingService {

    @Inject protected HeatingController controller;

    @Inject protected Now now;
    @Inject protected PropertyManager props;

    private HeatingStatus status;


    // Public API

    public HeatingService startHeatingService() {
        status = new HeatingStatus();
        overrideTimeout = minutesAsMilliseconds(props.getLong("overrideMinutes", 60L));
        controller.init();
        controller.setThermoTriggerListener(this::setThermoTriggered);
        heating(OFF);
        new Timer().schedule(new TimerTask() { public void run() {
            updateStatus();
        }}, 0, 1000);
        return this;
    }

    public HeatingStatus getStatus() {
        return status;
    }

    public void switchHeating(Status s) {
        status.setCurrentState(s);
        updateStatus();
    }

    public void setOverride(Status s) {
        status.setOverrideStatus(s);
        if ( ON.equals(s) ) {
            overrideActivated = now.now();
        } else {
            overrideActivated = null;
        }
        updateStatus();
    }

    public void setThermo(Status s) {
        status.setThermoStatus(s);
        updateStatus();
    }

    public void setThermoTriggered(Status s) {
        status.setThermoTriggered(s);
        updateStatus();
    }

    public void setTimer(Status s) {
        status.setTimerStatus(s);
        updateStatus();
    }

    public String addTimerEvent(Status type, String time) {
        try {
            TimerEvent event =
                    new TimerEvent(
                            String.valueOf(UUID.randomUUID()),
                            type,
                            TIME_FORMAT.parse(time)
                    );
            status.addEvent(event);
            return event.getId();
        } catch (ParseException ignored) {}
        return null;
    }

    public void removeTimerEvent(String id) {
        status.removeEvent(id);
    }


    // Status Algorithm

    void updateStatus() {

        if ( status.isOverrideOn() ) {
            if ( overrideValid() ) {
                heating(ON);
                overrideStatusString();
            } else {
                setOverride(OFF);
            }
        } else {
            if ( status.isTimerOn() ) {
                switch ( getStatusByTimer() ) {
                    case ON:
                        if ( status.isThermoOn() ) {
                            heating(status.getThermoState());
                        } else {
                            heating(ON);
                        }
                        break;
                    case OFF:
                        heating(OFF);
                        break;
                }
                timerActiveStatusString();
            } else {
                if ( status.isThermoOn() ) {
                    heating(status.getThermoState());
                } else {
                    heating(status.getCurrentState());
                }
                timerDisabledStatusString();
            }
        }

    }


    // Hardware Interface

    private void heating(Status s) {
        if ( s==null ) {
            s = OFF;
        }
        status.setCurrentState(s);
        controller.switchHeating(s);
    }


    // Timer

    private Status getStatusByTimer() {
        Status result = OFF;
        List<TimerEvent> events = status.getTimerEvents();
        if ( !events.isEmpty() ) {
            Date time = now.time();
            sort(events);
            for (TimerEvent event : events) {
                if (event.getTime().before(time)) {
                    result = event.getType();
                } else {
                    return result;
                }
            }
        }
        return result;
    }

    private TimerEvent getNextTimerEvent() {
        List<TimerEvent> events = status.getTimerEvents();
        if ( !events.isEmpty() ) {
            Date time = now.time();
            sort(events);
            for (TimerEvent event : events) {
                if (event.getTime().after(time)) {
                    return event;
                }
            }
            return events.get(0);
        }
        return null;
    }


    // Override

    private long overrideTimeout = hoursAsMilliseconds(1);
    private Date overrideActivated;

    private boolean overrideValid() {
        return overrideActivated!=null&&(elapsedTime()<overrideTimeout);
    }

    private long elapsedTime() {
        return now.now().getTime()-overrideActivated.getTime();
    }

    private long remainingMinutes() {
        return (long)Math.round((overrideTimeout-elapsedTime())/(60000));
    }


    // Status Strings

    private void overrideStatusString() {
        status.setHeatingStatusString("ON for next " + remainingMinutes() + " minutes");
    }

    private void timerDisabledStatusString() {
        status.setHeatingStatusString("Timer Disabled");
    }

    private void timerActiveStatusString() {
        TimerEvent next = getNextTimerEvent();
        if ( next!=null ) {
            status.setHeatingStatusString(next.getType().name() + " at " + TIME_FORMAT.format(next.getTime()));
        } else {
            status.setHeatingStatusString(status.getCurrentState().name() + " until Further Notice");
        }
    }


    // Persist State

    private static final String JSON_FILE = "heatingservice.json";

    public HeatingService loadStateFromConfig() {
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            status = (HeatingStatus)toBean(fromObject(jsonString), HeatingStatus.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public HeatingService saveStateToConfig() {
        try {
            FileWriter writer = new FileWriter(JSON_FILE);
            writer.write(fromObject(status).toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

}
