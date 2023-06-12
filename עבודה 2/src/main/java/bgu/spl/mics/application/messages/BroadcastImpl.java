package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class BroadcastImpl implements Broadcast {
    String a;

    public BroadcastImpl(){
        a="";
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
