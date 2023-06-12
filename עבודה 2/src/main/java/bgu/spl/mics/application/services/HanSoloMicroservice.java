package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        Callback<TerminateBroadcast> TerminateCallback = (TerminateBroadcast b) -> {
            Diary.getInstance().setHanSoloTerminate();
            this.terminate();
        };
        subscribeBroadcast(TerminateBroadcast.class, TerminateCallback);

        Callback<AttackEvent> attackEventCallback = (AttackEvent e)-> {

                Ewoks.getInstance().acquireEwoks(e.getAttack().getSerials());
                Thread.sleep(e.getAttack().getDuration());
                Ewoks.getInstance().realeseEwoks(e.getAttack().getSerials());


                Diary.getInstance().addAttack();
                complete(e,true);

        };
        subscribeEvent(AttackEvent.class, attackEventCallback);

        Callback<EndOfAttacksBroadcast> EndOfAttacks = (EndOfAttacksBroadcast b) -> { Diary.getInstance().setHanSoloFinish();};
        subscribeBroadcast(EndOfAttacksBroadcast.class, EndOfAttacks);

    }
}