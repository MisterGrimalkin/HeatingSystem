package net.amarantha.heating.service;

import net.amarantha.heating.entity.Status;
import net.amarantha.heating.entity.TimerEvent;

import java.util.ArrayList;
import java.util.List;

import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;

public class HeatingStatus {

    private Status currentState = null;
    private String heatingStatusString = "System not ready";

    private Status overrideStatus = OFF;

    private Status thermoStatus = OFF;
    private Status thermoTriggered = OFF;

    private Status timerStatus = OFF;
    private List<TimerEvent> timerEvents = new ArrayList<>();


    HeatingStatus() {}


    // Package Getters

    boolean isOverrideOn() {
        return ON.equals(overrideStatus);
    }

    boolean isThermoOn() {
        return ON.equals(thermoStatus);
    }

    boolean isTimerOn() {
        return ON.equals(timerStatus);
    }


    // Other Getters

    public Status getCurrentState() {
        return currentState;
    }

    public String getHeatingStatusString() {
        return heatingStatusString;
    }

    public Status getOverrideStatus() {
        return overrideStatus;
    }

    public Status getThermoStatus() {
        return thermoStatus;
    }

    public Status getThermoState() {
        return thermoTriggered;
    }

    public Status getTimerStatus() {
        return timerStatus;
    }

    public List<TimerEvent> getTimerEvents() {
        return timerEvents;
    }


    // Events

    public void addEvent(TimerEvent event) {
        timerEvents.add(event);
    }

    public void removeEvent(String id) {
        for ( TimerEvent event : timerEvents ) {
            if ( event.getId().equals(id) ) {
                timerEvents.remove(event);
                break;
            }
        }
    }


    // Setters

    void setCurrentState(Status heatingStatus) {
        this.currentState = heatingStatus;
    }

    void setHeatingStatusString(String heatingStatusString) {
        this.heatingStatusString = heatingStatusString;
    }

    void setOverrideStatus(Status overrideStatus) {
        this.overrideStatus = overrideStatus;
    }

    void setThermoStatus(Status thermoStatus) {
        this.thermoStatus = thermoStatus;
    }

    void setThermoTriggered(Status thermoTriggered) {
        this.thermoTriggered = thermoTriggered;
    }

    void setTimerStatus(Status timerStatus) {
        this.timerStatus = timerStatus;
    }


    // JSON Setters
    public void setCurrentState(String s) {
        setCurrentState(Status.valueOf(s));
    }
    public void setOverrideStatus(String s) {
        setOverrideStatus(Status.valueOf(s));
    }
    public void setThermoStatus(String s) {
        setThermoStatus(Status.valueOf(s));
    }
    public void setTimerStatus(String s) {
        setTimerStatus(Status.valueOf(s));
    }


}
