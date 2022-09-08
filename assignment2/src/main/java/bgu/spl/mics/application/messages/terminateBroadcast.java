package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
//for termination of all micro services
public class terminateBroadcast implements Broadcast  {
    public String name;
    public terminateBroadcast(){
        name="terminate";
    }

}
