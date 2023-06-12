package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadcastImpl;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    MicroService microService1;
    MicroService microService2;
    MessageBus messageBus;

    @BeforeAll
    void setUp() {
        messageBus = new MessageBusImpl();
        microService1 = new HanSoloMicroservice();
        microService2 = new HanSoloMicroservice();
    }
// This test include the test for the methods- register, subscribeBroadCast, awaitMessage and complete. Those methods have all to work well for this test to successes.
    @Test
    void sendBroadcast() {
        BroadcastImpl broadCastTosend = new BroadcastImpl();
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.subscribeBroadcast(broadCastTosend.getClass(), microService1);
        messageBus.subscribeBroadcast(broadCastTosend.getClass(), microService2);
        messageBus.sendBroadcast(broadCastTosend);
        try{
            BroadcastImpl broadCastToReceive1 =(BroadcastImpl) messageBus.awaitMessage(microService1);
            BroadcastImpl broadCastToReceive2 = (BroadcastImpl) messageBus.awaitMessage(microService2);
            broadCastTosend.setA("test");
            assertTrue(broadCastToReceive1.getA().equals(broadCastTosend.getA()) & broadCastToReceive2.getA().equals(broadCastTosend.getA()));
        }

         catch (Exception e){
             fail();
          }
    }

    // This test include the test for the methods- register, subscribeEvent, awaitMessage and complete. Those methods have all to work well for this test to successes.
    @Test
    void sendEvent() {
        AttackEvent eventToResolve = new AttackEvent();
        messageBus.register(microService2);
        messageBus.subscribeEvent(eventToResolve.getClass() , microService2);
        messageBus.sendEvent(eventToResolve);
        try {
            AttackEvent eventToHandle = (AttackEvent) messageBus.awaitMessage(microService2);
            eventToResolve.setFuture(true);
            assertTrue(eventToResolve.getFutureResult().equals(eventToHandle.getFutureResult())); // if the complete() happened the result of "eventToresolve" should update and be equal to the result that resolved by microService2
        }
        catch (Exception e){
            fail();
        }
    }
}