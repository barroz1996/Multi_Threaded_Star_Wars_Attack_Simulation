
package bgu.spl.mics;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.someBroadcast;
import bgu.spl.mics.application.messages.someEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//we added a small change, unregister to all the functions in the end!!
class MessageBusImplTest {
    private MessageBusImpl msgBus;
    private HanSoloMicroservice HanSolo;
    private someEvent aEvent;
    private someBroadcast broad;

    @BeforeEach
    void setUp() {
        msgBus = (MessageBusImpl) MessageBusImpl.getInstance();
        HanSolo = new HanSoloMicroservice();
        aEvent =new someEvent();
        broad = new someBroadcast();
        msgBus.register(HanSolo);//checks register- if its not working we will get a fail
    }


    @Test
    void testComplete() {
        msgBus.subscribeEvent(aEvent.getClass(), HanSolo);
        Future f = msgBus.sendEvent(aEvent); //catch the future outcome
        try {
            msgBus.awaitMessage(HanSolo);
        } catch (InterruptedException e) {
        }

        assertFalse(f.isDone());//checks if the future hasn't changed yet
        msgBus.complete(aEvent, 5);
        assertTrue(f.isDone()); // now we need to check if complete resolved it
        assertEquals(5, f.get());  // and checks if the result is right
        msgBus.unregister(HanSolo);
    }

    @Test
    void testSendBroadcast() {
        msgBus.subscribeBroadcast(broad.getClass(), HanSolo);
        msgBus.sendBroadcast(broad);
        try {
            assertEquals(broad, (someBroadcast)msgBus.awaitMessage(HanSolo)); // checks if we get the same broadcast
        } catch (InterruptedException e) {}
        msgBus.unregister(HanSolo);

    }

    @Test
    void testSendEvent() {
        msgBus.subscribeEvent(aEvent.getClass(), HanSolo);
        Future<Integer> f = msgBus.sendEvent(aEvent);
        try {
            assertEquals(aEvent,(someEvent) msgBus.awaitMessage(HanSolo)); //checks if we get the same event
        }

        catch (InterruptedException e) {}
        msgBus.unregister(HanSolo);
    }
    @Test
    void testAwaitMessage() {
        msgBus.register(HanSolo);
        msgBus.subscribeEvent(aEvent.getClass(), HanSolo);
        msgBus.sendEvent(aEvent);
        try {
            assertEquals(aEvent, (someEvent)msgBus.awaitMessage(HanSolo));
        } catch (InterruptedException e) {}
        msgBus.unregister(HanSolo); //checks unregister
    }
}

