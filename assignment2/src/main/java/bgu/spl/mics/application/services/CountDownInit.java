package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

public class CountDownInit {
    private CountDownLatch counter;
    private CountDownInit(){
        counter=new CountDownLatch(4);

    }
    public void Down(){
        counter.countDown();
    }
    public void aWaitCount(){
        try {
            counter.await();
        }
       catch (InterruptedException e){}
    }
    public static class singleton {//to make only one appearance
        private static CountDownInit instance=new CountDownInit();
    }
    public static CountDownInit getInstance(){
        return CountDownInit.singleton.instance;
    }
}
