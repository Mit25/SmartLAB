package com.example.mit25.smartlab;

/**
 * Created by mit25 on 6/6/18.
 */

public class Device implements java.io.Serializable {
    public String ID,name;

    public Device(String ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
