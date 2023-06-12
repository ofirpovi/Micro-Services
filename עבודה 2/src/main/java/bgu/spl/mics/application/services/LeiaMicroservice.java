package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.*;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link bgu.spl.mics.application.messages.AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private List<Future<Boolean>> futures;
    private MessageBusImpl messageBus;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        futures = new LinkedList<Future<Boolean>>();
        messageBus = MessageBusImpl.getInstance();

    }

    @Override
    protected void initialize() {
        Callback<TerminateBroadcast> TerminateCallback = (TerminateBroadcast b) -> {
            Diary.getInstance().setLeiaTerminate();
            this.terminate();
        };
        subscribeBroadcast(TerminateBroadcast.class, TerminateCallback);
        for (int i = 0; i < attacks.length; i++) {
            AttackEvent attackEvent = new AttackEvent();
            attackEvent.setAttack(attacks[i]);
            futures.add(sendEvent(attackEvent));
        }
        sendBroadcast(new EndOfAttacksBroadcast());
        for(Future<Boolean> future: futures){
            future.get();
        }
        Future<DeactivationEvent> futureR2D2 = sendEvent(new DeactivationEvent());
        futureR2D2.get();
        Future<BombDestroyerEvent> futureLando = sendEvent(new BombDestroyerEvent());
        futureLando.get();
        sendBroadcast(new TerminateBroadcast());
    }
}