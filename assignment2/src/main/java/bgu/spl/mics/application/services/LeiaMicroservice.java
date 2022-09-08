package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private int numOfAttacks;
    private Diary diary=Diary.getInstance();

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.numOfAttacks=attacks.length;
    }
    public int getNumOfAttacks(){return numOfAttacks;}
    public void setNumOfAttacks(int setNum){
        numOfAttacks=setNum;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(terminateBroadcast.class,terminateBroadcast->{diary.setLeiaTerminate(System.currentTimeMillis());
        this.terminate();});
        subscribeBroadcast(finishedAttack.class,finishedAttack->{//event that tells us that we finished an attack
            setNumOfAttacks(getNumOfAttacks()-1);
        if(numOfAttacks==0){//if we are over all the attacks, then send to R2D2 a message
        sendEvent(new DeactivationEvent());
        }});
        for (Attack attack : attacks) {
            AttackEvent a = new AttackEvent(attack.getSerials(), attack.getDuration());
            this.sendEvent(a);
        }
    }
}
