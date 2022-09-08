package bgu.spl.mics.application.passiveObjects;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private  ConcurrentHashMap<Integer, Ewok > Ewoks;


    private Ewoks(){
        Ewoks=new ConcurrentHashMap<>();

    }
    public static class singleton {//to make only one appearance
        private static  Ewoks instance=new Ewoks();
    }
    public static Ewoks getInstance(){
        return singleton.instance;
    }

    public void addEwoks(Ewok[] ewoks) { //adding the ewoks from  main ewoks array
        for (Ewok e:ewoks)
            Ewoks.put(e.getSerialNumber(),e);
        }

        public  synchronized void acquireEwoks(List<Integer> ewokAcquire){
            boolean checkIfAllFree=false;
            while (!checkIfAllFree) {//if we are sleeping, we may checked "free" ewoks that by now no longer free
                checkIfAllFree = true;
                for (Integer i : ewokAcquire) { //for every ewok we need to get for attack
                        try {
                            while (!Ewoks.get(i).isAvailable()) {
                                checkIfAllFree=false; //if we got here it means there is an ewok that is not available
                                wait();
                        }
                        }
                        catch (InterruptedException e) {
                        }
                }
            }
    for (Integer i : ewokAcquire) { //acquiring the ewoks after knowing they are all free
        Ewoks.get(i).acquire();
}
        }
        public synchronized void releaseEwoks(List<Integer>ewokRelease) {
            for (Integer i : ewokRelease) {
                Ewoks.get(i).release();
                notifyAll();

            }

        }

    }
