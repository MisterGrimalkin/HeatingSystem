package net.amarantha.heating.service;

import com.google.inject.Inject;
import com.googlecode.guicebehave.Modules;
import com.googlecode.guicebehave.Story;
import com.googlecode.guicebehave.StoryRunner;
import net.amarantha.heating.TestModule;
import net.amarantha.heating.entity.Status;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.utility.Now;
import org.junit.runner.RunWith;

import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;
import static org.junit.Assert.assertEquals;

@RunWith(StoryRunner.class) @Modules(TestModule.class)
public class TestHeatingService {

    @Inject private HeatingService service;

    @Story
    public void testSimpleOnOff() {

        given_the_heating_system();

        when_switch_heating_$1(ON);
        then_heating_is_$1(ON);
        when_switch_heating_$1(OFF);
        then_heating_is_$1(OFF);

    }

    @Story
    public void testThermostat() {

        given_the_heating_system();

        when_thermo_is_$1(OFF);
        then_heating_is_$1(OFF);

        when_thermo_is_triggered_$1(ON);         then_heating_is_$1(OFF);
        when_thermo_is_triggered_$1(OFF);        then_heating_is_$1(OFF);

        when_thermo_is_$1(ON);
        then_heating_is_$1(OFF);

        when_thermo_is_triggered_$1(ON);         then_heating_is_$1(ON);
        when_thermo_is_triggered_$1(OFF);        then_heating_is_$1(OFF);
        when_thermo_is_triggered_$1(ON);         then_heating_is_$1(ON);

        when_switch_heating_$1(OFF);
        then_heating_is_$1(ON);

        when_thermo_is_$1(OFF);
        then_heating_is_$1(ON);

        when_switch_heating_$1(OFF);
        then_heating_is_$1(OFF);

    }

    @Story
    public void testOverride() {

        given_the_heating_system();

        when_switch_heating_$1(ON);
        then_heating_is_$1(ON);
        then_override_is_$1(OFF);

        when_thermo_is_$1(ON);

        when_thermo_is_triggered_$1(ON);
        then_heating_is_$1(ON);
        when_thermo_is_triggered_$1(OFF);
        then_heating_is_$1(OFF);

        when_switch_heating_$1(ON);
        then_heating_is_$1(OFF);

        when_override_is_$1(ON);
        then_heating_is_$1(ON);
        then_override_is_$1(ON);

        when_$1_minutes_pass(45);
        then_heating_is_$1(ON);
        then_override_is_$1(ON);

        when_$1_minutes_pass(45);
        then_heating_is_$1(OFF);
        then_override_is_$1(OFF);

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

    @Story
    public void testTimer() {

        given_the_heating_system();

        when_add_timer_event_$1_at_$2(ON, ON_1);
        when_add_timer_event_$1_at_$2(OFF, OFF_1);
        String timerEvent3 =
        when_add_timer_event_$1_at_$2(ON, ON_2);
        when_add_timer_event_$1_at_$2(OFF, OFF_2);

        when_timer_is_$1(OFF);

        when_switch_heating_$1(OFF);
        when_time_is_$1(TEST_TIME_1);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_2);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_3);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_4);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_5);        then_heating_is_$1(OFF);

        when_switch_heating_$1(ON);
        when_time_is_$1(TEST_TIME_1);        then_heating_is_$1(ON);
        when_time_is_$1(TEST_TIME_2);        then_heating_is_$1(ON);
        when_time_is_$1(TEST_TIME_3);        then_heating_is_$1(ON);
        when_time_is_$1(TEST_TIME_4);        then_heating_is_$1(ON);
        when_time_is_$1(TEST_TIME_5);        then_heating_is_$1(ON);

        when_timer_is_$1(ON);

        when_time_is_$1(TEST_TIME_1);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_2);        then_heating_is_$1(ON);
        when_time_is_$1(TEST_TIME_3);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_4);        then_heating_is_$1(ON);
        when_time_is_$1(TEST_TIME_5);        then_heating_is_$1(OFF);

        when_remove_timer_event_$1(timerEvent3);

        when_time_is_$1(TEST_TIME_1);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_2);        then_heating_is_$1(ON);
        when_time_is_$1(TEST_TIME_3);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_4);        then_heating_is_$1(OFF);
        when_time_is_$1(TEST_TIME_5);        then_heating_is_$1(OFF);

    }

    @Story
    public void testStatePersistence() {

        given_the_heating_system();

        when_timer_is_$1(OFF);
        when_thermo_is_$1(ON);

        service.saveStateToConfig();

        given_the_heating_system();

        // TODO: Fix JSON decoding

    }


    @Inject private HeatingController controller;

    @Inject private Now now;


    // Given

    protected void given_the_heating_system() {
        service.startHeatingService();
    }


    // When

    protected void when_$1_minutes_pass(int minutes) {
        now.pushNowMinutes(minutes);
        service.updateStatus();
    }

    protected void when_time_is_$1(String time) {
        now.setTime(time);
        service.updateStatus();
    }

    protected void when_thermo_is_triggered_$1(Status status) {
        controller.triggerThermo(status);
    }

    protected void when_switch_heating_$1(Status status) {
        service.switchHeating(status);
    }

    protected void when_override_is_$1(Status status) {
        service.setOverride(status);
    }

    protected void when_thermo_is_$1(Status status) {
        service.setThermo(status);
    }

    protected void when_timer_is_$1(Status status) {
        service.setTimer(status);
    }

    protected String when_add_timer_event_$1_at_$2(Status type, String time) {
        return service.addTimerEvent(type, time);
    }

    protected void when_remove_timer_event_$1(String id) {
        service.removeTimerEvent(id);
    }


    // Then

    protected void then_heating_is_$1(Status status) {
        assertEquals(status, controller.getHeatingStatus());
    }

    protected void then_override_is_$1(Status status) {
        assertEquals(status, service.getStatus().getOverrideStatus());
    }

    protected void then_thermo_is_$1(Status status) {
        assertEquals(status, service.getStatus().getThermoStatus());
    }

    protected void then_timer_is_$1(Status status) {
        assertEquals(status, service.getStatus().getTimerStatus());
    }

    protected void then_total_events_$1(int count) {
        assertEquals(count, service.getStatus().getTimerEvents().size());
    }

}
