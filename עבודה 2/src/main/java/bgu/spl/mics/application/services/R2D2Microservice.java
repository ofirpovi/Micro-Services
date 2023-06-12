package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

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

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration= duration;
    }

    @Override
    protected void initialize() {
        Callback<TerminateBroadcast> TerminateCallback = (TerminateBroadcast b) -> {
            Diary.getInstance().setR2D2Terminate();
            this.terminate();
        };

        subscribeBroadcast(TerminateBroadcast.class, TerminateCallback);

        Callback<DeactivationEvent> deactivationEventCallback = (DeactivationEvent e)-> {
                Thread.sleep(duration);
                complete(e,true);
                Diary.getInstance().setR2D2Deactivate();

        };
        subscribeEvent(DeactivationEvent.class, deactivationEventCallback);
    }
}