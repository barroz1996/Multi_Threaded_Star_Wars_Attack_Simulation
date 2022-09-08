package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    private Diary diary=Diary.getInstance();
    private CountDownInit count=CountDownInit.getInstance();

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;
    }
    private Callback<BombDestroyerEvent> BombaEventCallback=new Callback<BombDestroyerEvent>() {
        @Override
        public void call(BombDestroyerEvent c) {
           try {
               Thread.sleep(duration);
           }
           catch (InterruptedException e){}
           complete(c,true);
           sendBroadcast(new terminateBroadcast());

        }
    };

    @Override
    protected void initialize() {
        subscribeBroadcast(terminateBroadcast.class, terminateBroadcast->{diary.setLandoTerminate(System.currentTimeMillis());terminate();});
        subscribeEvent(BombDestroyerEvent.class,BombaEventCallback);
        count.Down();


    }
}
