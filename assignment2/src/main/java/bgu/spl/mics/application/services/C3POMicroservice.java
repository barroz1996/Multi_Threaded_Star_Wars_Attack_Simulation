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
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private Ewoks team=Ewoks.getInstance();
    private Diary diary=Diary.getInstance();
    private CountDownInit count=CountDownInit.getInstance();
	
    public C3POMicroservice() {
        super("C3PO");
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
        diary.inc();//for knowing how much attacks is done
        diary.setC3POFinish(System.currentTimeMillis()); // only the last time will matter
        complete(c,true);
        sendBroadcast(new finishedAttack());//for leia to know when to send Deactivation

    };

    @Override
    protected void initialize() {
        subscribeBroadcast(terminateBroadcast.class, terminateBroadcast->{diary.setC3POTerminate(System.currentTimeMillis());terminate();});
        subscribeEvent(AttackEvent.class,attackEventCallback);
        count.Down();
    }
}
