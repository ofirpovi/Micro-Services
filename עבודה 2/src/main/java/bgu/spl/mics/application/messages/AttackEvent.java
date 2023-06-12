package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {
    Future<Boolean> future;
    Attack attack;

public AttackEvent(){
    future = new Future<Boolean>();
}
    public Boolean getFutureResult() {
        return future.get();
    }

    public void setFuture(Boolean res) {
        future.resolve(res);
    }

    public Attack getAttack(){
     return attack;
    }

    public void setFuture(Future<Boolean> future) {
        this.future = future;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }
}
