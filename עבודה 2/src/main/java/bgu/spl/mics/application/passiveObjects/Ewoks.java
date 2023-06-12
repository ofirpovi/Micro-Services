
package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.*;
import java.util.*;
/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static class EwoksHolder {
        private static Ewoks instance = new Ewoks();
    }
    private List<Ewok> ewoks;

    private Ewoks(){
        ewoks = new LinkedList<>();
    }

    public static Ewoks getInstance(){
        return EwoksHolder.instance;
    }

    public void setNumOfEwoks(int num){
        for(int i=1; i<= num; i++){
            ewoks.add(new Ewok(i));
        }
    }

    public void acquireEwoks (List <Integer> serials){
        Collections.sort(serials);
        for (int i=0; i<serials.size() ; i++){
            synchronized (ewoks.get(serials.get(i)-1)){
                while (!ewoks.get(serials.get(i)-1).available){
                    try {
                        ewoks.get(serials.get(i) - 1).wait();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                ewoks.get(serials.get(i)-1).acquire();
            }
        }
    }

    public void realeseEwoks (List<Integer> serials){
        for (int i=0 ; i<serials.size() ; i++){
            synchronized (ewoks.get(serials.get(i)-1)) {
                ewoks.get(serials.get(i) - 1).release();
                ewoks.get(serials.get(i)-1).notifyAll();
            }
        }
    }
}