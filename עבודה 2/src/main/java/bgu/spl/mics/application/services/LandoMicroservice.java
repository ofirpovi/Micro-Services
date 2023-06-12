package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    long duration;
    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {

        Callback<TerminateBroadcast> TerminateCallback = (TerminateBroadcast b) -> {
            Diary.getInstance().setLandoTerminate();
            this.terminate();
        };
        subscribeBroadcast(TerminateBroadcast.class, TerminateCallback);

        Callback<BombDestroyerEvent> BombDestroyerEventCallback = (BombDestroyerEvent e)-> {

                Thread.sleep(duration);
                complete(e,true);

        };
        subscribeEvent(BombDestroyerEvent.class, BombDestroyerEventCallback);
    }
}