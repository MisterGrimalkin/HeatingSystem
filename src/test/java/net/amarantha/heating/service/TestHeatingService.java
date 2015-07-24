package net.amarantha.heating.service;

import net.amarantha.heating.entity.Status;
import org.junit.Test;

import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;
import static org.junit.Assert.assertEquals;

public class TestHeatingService {

    @Test
    public void testSimpleOnOff() {

        given_system();

        when_switch_heating(ON);
        then_heating_is(ON);
        when_switch_heating(OFF);
        then_heating_is(OFF);

    }

    @Test
    public void testThermostat() {

        given_system();

        when_thermo_is(OFF);
        then_heating_is(OFF);

        when_thermo_is_triggered(ON);         then_heating_is(OFF);
        when_thermo_is_triggered(OFF);        then_heating_is(OFF);

        when_thermo_is(ON);
        then_heating_is(OFF);

        when_thermo_is_triggered(ON);         then_heating_is(ON);
        when_thermo_is_triggered(OFF);        then_heating_is(OFF);
        when_thermo_is_triggered(ON);         then_heating_is(ON);

        when_switch_heating(OFF);
        then_heating_is(ON);

        when_thermo_is(OFF);
        then_heating_is(ON);

        when_switch_heating(OFF);
        then_heating_is(OFF);

    }

    @Test
    public void testOverride() {

        given_system();

        when_switch_heating(ON);
        then_heating_is(ON);
        then_override_is(OFF);

        when_thermo_is(ON);

        when_thermo_is_triggered(ON);
        then_heating_is(ON);
        when_thermo_is_triggered(OFF);
        then_heating_is(OFF);

        when_switch_heating(ON);
        then_heating_is(OFF);

        when_override_is(ON);
        then_heating_is(ON);
        then_override_is(ON);

        when_minutes_pass(45);
        then_heating_is(ON);
        then_override_is(ON);

        when_minutes_pass(45);
        then_heating_is(OFF);
        then_override_is(OFF);

    }

    private static final String TEST_TIME_1 = "02:00";
    private static final String ON_1 = "03:00";
    private static final String TEST_TIME_2 = "03:01";
    private static final String OFF_1 = "05:30";
    private static final String TEST_TIME_3 = "12:00";
    private static final String ON_2 = "18:30";
    private static final String TEST_TIME_4 = "22:59";
    private static final String OFF_2 = "23:00";
    private static final String TEST_TIME_5 = "23:30";

    @Test
    public void testTimer() {

        given_system();

        when_add_timer_event    (ON,    ON_1);
        when_add_timer_event    (OFF,   OFF_1);
        String timerEvent3 =
        when_add_timer_event    (ON,    ON_2);
        when_add_timer_event    (OFF,   OFF_2);

        when_timer_is(OFF);

        when_switch_heating(OFF);
        when_time_is(TEST_TIME_1);        then_heating_is(OFF);
        when_time_is(TEST_TIME_2);        then_heating_is(OFF);
        when_time_is(TEST_TIME_3);        then_heating_is(OFF);
        when_time_is(TEST_TIME_4);        then_heating_is(OFF);
        when_time_is(TEST_TIME_5);        then_heating_is(OFF);

        when_switch_heating(ON);
        when_time_is(TEST_TIME_1);        then_heating_is(ON);
        when_time_is(TEST_TIME_2);        then_heating_is(ON);
        when_time_is(TEST_TIME_3);        then_heating_is(ON);
        when_time_is(TEST_TIME_4);        then_heating_is(ON);
        when_time_is(TEST_TIME_5);        then_heating_is(ON);

        when_timer_is(ON);

        when_time_is(TEST_TIME_1);        then_heating_is(OFF);
        when_time_is(TEST_TIME_2);        then_heating_is(ON);
        when_time_is(TEST_TIME_3);        then_heating_is(OFF);
        when_time_is(TEST_TIME_4);        then_heating_is(ON);
        when_time_is(TEST_TIME_5);        then_heating_is(OFF);

        when_remove_timer_event(timerEvent3);

        when_time_is(TEST_TIME_1);        then_heating_is(OFF);
        when_time_is(TEST_TIME_2);        then_heating_is(ON);
        when_time_is(TEST_TIME_3);        then_heating_is(OFF);
        when_time_is(TEST_TIME_4);        then_heating_is(OFF);
        when_time_is(TEST_TIME_5);        then_heating_is(OFF);

    }

    @Test
    public void testStatePersistence() {

        given_system();

        when_timer_is(OFF);
        when_thermo_is(ON);

        service.saveStateToConfig();

        given_system();

        // TODO: Fix JSON decoding

    }


    // Given

    private HeatingService service;
    private MockHeatingController controller;

    private void given_system() {
        service = HeatingService.withController(controller = new MockHeatingController());
    }


    // When

    private void when_minutes_pass(int minutes) {
        service.now.pushNow(minutes);
        service.updateStatus();
    }

    private void when_time_is(String time) {
        service.now.setNowTime(time);
        service.updateStatus();
    }

    private void when_thermo_is_triggered(Status status) {
        controller.triggerThermo(status);
    }

    private void when_switch_heating(Status status) {
        service.switchHeating(status);
    }

    private void when_override_is(Status status) {
        service.setOverride(status);
    }

    private void when_thermo_is(Status status) {
        service.setThermo(status);
    }

    private void when_timer_is(Status status) {
        service.setTimer(status);
    }

    private String when_add_timer_event(Status type, String time) {
        return service.addTimerEvent(type, time);
    }

    private void when_remove_timer_event(String id) {
        service.removeTimerEvent(id);
    }


    // Then

    private void then_heating_is(Status status) {
        assertEquals(status, controller.isOn());
    }

    private void then_override_is(Status status) {
        assertEquals(status, service.getStatus().getOverrideStatus());
    }

    private void then_thermo_is(Status status) {
        assertEquals(status, service.getStatus().getThermoStatus());
    }

    private void then_timer_is(Status status) {
        assertEquals(status, service.getStatus().getTimerStatus());
    }

    private void then_total_events(int count) {
        assertEquals(count, service.getStatus().getTimerEvents().size());
    }

}
