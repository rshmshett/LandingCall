package com.example.rshms.v2landingcall;

/**
 * Created by rshms on 5/6/2018.
 */

public class Fall {
    int id;
    String event;
    String deviceip;

    public Fall(int id, String event, String deviceip) {
        this.id = id;
        this.event = event;
        this.deviceip = deviceip;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setDeviceip(String deviceip) {
        this.deviceip = deviceip;
    }

    public int getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }

    public String getDeviceip() {
        return deviceip;
    }
}
