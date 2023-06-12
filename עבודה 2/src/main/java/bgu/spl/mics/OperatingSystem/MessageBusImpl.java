package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static class MessageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private volatile ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventsQueue;
	private volatile ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastList;
	private volatile ConcurrentHashMap<MicroService, BlockingQueue<Message>> microQueues;
	private volatile ConcurrentHashMap<Event, Future> eventFutures;


	public MessageBusImpl() {
		eventsQueue = new ConcurrentHashMap<>();
		broadcastList = new ConcurrentHashMap<>();
		microQueues = new ConcurrentHashMap<>();
		eventFutures = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventsQueue.containsKey(type)) {
			synchronized (eventsQueue.get(type)) {
				ConcurrentLinkedQueue q = eventsQueue.get(type);
				q.add(m);// adding m to the queue from type Type
			}
		}
		else {
			synchronized (eventsQueue) {
				ConcurrentLinkedQueue q = new ConcurrentLinkedQueue();
				q.add(m);
				eventsQueue.putIfAbsent(type, q);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (broadcastList.containsKey(type)) {
			synchronized (broadcastList.get(type)) {
				ConcurrentLinkedQueue q = broadcastList.get(type);
				q.add(m); // adding m to the queue from type Type
			}
		}
		else {
			synchronized (broadcastList) {
				ConcurrentLinkedQueue q = new ConcurrentLinkedQueue<>();
				q.add(m);
				broadcastList.putIfAbsent(type, q);
			}
		}
	}


	@Override //unchecked//
	public <T> void complete(Event<T> e, T result) {
		eventFutures.get(e).resolve(result);
	}

	public void sendBroadcast(Broadcast b) {
		if (broadcastList.get(b.getClass()) == null) {
			throw new IllegalArgumentException("tried to send a broadcast b no one subscribed to");
		}
		if (broadcastList.get(b.getClass()).isEmpty()) {
			throw new IllegalArgumentException("tried to send a broadcast b but broadcast queue is empty");
		}
		synchronized (broadcastList.get(b.getClass())) {
			synchronized (microQueues) {
				for (MicroService mName : broadcastList.get(b.getClass())) {
					if (microQueues.get(mName) == null) {
						throw new IllegalArgumentException("tried to add broadcast b to the queue of a micro that is not registered");
					}
					synchronized (microQueues.get(mName)) {
						microQueues.get(mName).add(b); // add the event to the queue of the ms
						microQueues.get(mName).notifyAll();
					}
				}
			}
		}
	}


	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService m;
		if (eventsQueue.get(e.getClass()) == null) {
			return null;
		}
		if (eventsQueue.get(e.getClass()).isEmpty()) {
			return null;
		}
		Future<T> future = new Future<>();
		eventFutures.put(e, future);
		synchronized (eventsQueue.get(e.getClass())) {
			m = eventsQueue.get(e.getClass()).poll();
			synchronized (microQueues.get(m)) {
					microQueues.get(m).add(e); // add the event to the queue of the ms
					microQueues.get(m).notifyAll();
			}
			eventsQueue.get(e.getClass()).add(m);
		}
		return future;

	}

	@Override
	public void register(MicroService m) {
		synchronized (microQueues) {
			microQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
		}
	}

	public void unregister(MicroService m) {
		for (ConcurrentLinkedQueue<MicroService> q : eventsQueue.values()) { //removing all this MS queues from subscribe event map
			synchronized (q) {
				if (q.contains(m)) {
					q.remove(m);
				}
			}
		}

		for (ConcurrentLinkedQueue<MicroService> q : broadcastList.values()) { // removing all this MS queues from subscribe broadcast map
			synchronized (q) {
				if (q.contains(m)) {
					q.remove(m);
				}
			}
		}

		synchronized (microQueues) {
			if (microQueues.containsKey(m)) {
				microQueues.get(m).clear();
				microQueues.remove(m);
			}
		}
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!microQueues.containsKey(m)) {
			return null;
		}

		synchronized (microQueues.get(m)) {
			while (microQueues.get(m).isEmpty()) {
				try {
					microQueues.get(m).wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return microQueues.get(m).poll();
		}
	}
}
