package net.amarantha.heating.service;

import net.amarantha.heating.entity.Status;
import net.amarantha.heating.entity.TimerEvent;
import net.amarantha.heating.hardware.HeatingController;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Collections.sort;
import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;
import static net.sf.json.JSONObject.fromObject;
import static net.sf.json.JSONObject.toBean;

public class HeatingService {

    private HeatingController controller;
    private HeatingStatus status;

    private HeatingService(HeatingController controller) {
        this.controller = controller;
        status = new HeatingStatus();
        updateStatus();
    }


    // Factory

    public static HeatingService withController(HeatingController controller) {
        HeatingService service = new HeatingService(controller);
        controller.setThermoTriggerListener(service::setThermoTriggered);
        return service;
    }


    // Public API

    public HeatingService startService() {
        controller.init();
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
                            sdf.parse(time)
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
            Date time = now.nowTimeOnly();
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
            Date time = now.nowTimeOnly();
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

    private boolean overrideValid() {
        return overrideActivated!=null&&(elapsedTime()<OVERRIDE_EXPIRY);
    }

    private long elapsedTime() {
        return now.now().getTime()-overrideActivated.getTime();
    }

    private long remainingMinutes() {
        return (long)Math.round((OVERRIDE_EXPIRY-elapsedTime())/(60000));
    }

    private Date overrideActivated;
    private final long OVERRIDE_EXPIRY = 60 * 60 * 1000;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
    Now now = new Now();


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
            status.setHeatingStatusString(next.getType().name() + " at " + sdf.format(next.getTime()));
        } else {
            status.setHeatingStatusString(status.getCurrentState().name() + " until Further Notice");
        }
    }


    // Persist State

    private static final String PROPS_FILE = "heatingservice.json";

    public HeatingService loadStateFromConfig() {
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(PROPS_FILE)));
            status = (HeatingStatus)toBean(fromObject(jsonString), HeatingStatus.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public HeatingService saveStateToConfig() {
        try {
            FileWriter writer = new FileWriter(PROPS_FILE);
            writer.write(fromObject(status).toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

}
