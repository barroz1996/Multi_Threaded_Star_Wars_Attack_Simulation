package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.application.messages.AttackEvent;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.finishedAttack;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Ewoks team=Ewoks.getInstance();
    private Diary diary=Diary.getInstance();
    private CountDownInit count=CountDownInit.getInstance();

    public HanSoloMicroservice() {
        super("Han");
    }
    private Callback<AttackEvent> attackEventCallback= c -> {
        List<Integer> serial=c.getSerials();
        int duration=c.getDuration();
        team.acquireEwoks(serial);
        try{
            Thread.sleep(duration);
        }
       catch (InterruptedException e){}
        team.releaseEwoks(serial);
        diary.inc();//tells us how much attacks we have handled
        diary.setHanSoloFinish(System.currentTimeMillis());//eventually' only the last event will matter
        complete(c,true);
        sendBroadcast(new finishedAttack());//tells leia when she needs to send Deactivation
    };


    @Override
    protected void initialize() {
        subscribeBroadcast(terminateBroadcast.class, terminateBroadcast->{diary.setHanSoloTerminate(System.currentTimeMillis());terminate();});
        subscribeEvent(AttackEvent.class,attackEventCallback);
        count.Down();

    }
}
