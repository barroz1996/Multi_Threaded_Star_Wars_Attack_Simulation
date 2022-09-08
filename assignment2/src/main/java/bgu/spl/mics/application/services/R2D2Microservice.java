package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;
    private Diary diary=Diary.getInstance();
    private CountDownInit count=CountDownInit.getInstance();

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }
    private  Callback<DeactivationEvent> Devent=new Callback<DeactivationEvent>() {
        @Override
        public void call(DeactivationEvent c) {
            try {
                Thread.sleep(duration);

            } catch (InterruptedException e) {
            }
            diary.setR2D2Deactivate(System.currentTimeMillis());
            complete(c,true );
            sendEvent(new BombDestroyerEvent());
        }
    };
    @Override
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class,Devent);
        subscribeBroadcast(terminateBroadcast.class,terminateBroadcast->{diary.setR2D2Terminate(System.currentTimeMillis());terminate();});
        count.Down();//count for Leia to start sending attacks
    }
}
