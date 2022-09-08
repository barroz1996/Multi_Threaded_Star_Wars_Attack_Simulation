package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	 private ConcurrentHashMap<MicroService,LinkedBlockingQueue  <Message>> microQueueMap; //mapping each microservice to the relevant queue
	 private ConcurrentHashMap<Class<?extends Message>,ConcurrentLinkedQueue<MicroService>>  messageHandle; //which kind of message can be used by each micro service
	private ConcurrentHashMap<Event<?>,Future> futureMap; // to save for each event his future result

	private MessageBusImpl(){
		microQueueMap=new ConcurrentHashMap<> ();
		messageHandle=new ConcurrentHashMap<>();
		futureMap=new ConcurrentHashMap<>();
	}
	public static class singleton {//to make only one appearance of the message bus
		private static MessageBusImpl instance=new MessageBusImpl();
	}
public static MessageBus getInstance(){
		return MessageBusImpl.singleton.instance;
}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		messageHandle.putIfAbsent(type,new ConcurrentLinkedQueue<MicroService>());//if msg type is already in queue' we dont need a new queue
		messageHandle.get(type).add(m);

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		messageHandle.putIfAbsent(type,new ConcurrentLinkedQueue<MicroService>());
		messageHandle.get(type).add(m);

    }

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future f=futureMap.get(e);
		f.resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {

		ConcurrentLinkedQueue<MicroService> broadQ = messageHandle.get(b.getClass());//getting the queue of the relevant microservices

			if (broadQ != null)
				for (MicroService m : broadQ) {//for every microservice that is registered to b type
					microQueueMap.get(m).add(b);
		}
	}
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<>();
		futureMap.put(e, f);//adding the new future of e to the future map
		MicroService m;
		ConcurrentLinkedQueue<MicroService> Q = messageHandle.get(e.getClass()); //getting the MS queue to know to which MS to assign the event
		if (Q == null)
			return null;
		synchronized (e.getClass()) {
			m = Q.poll(); //deque the first MS in line
			if (m == null)
				return null;
			Q.add(m); //enqueue to the end of the line the MS to maintain Round robin manner
		}
			LinkedBlockingQueue<Message> Q2 = microQueueMap.get(m); //getting the message queue of the MS

			if (Q2 == null)
				return null;
			Q2.add(e);//adding the relevant event

			return f;

	}

	@Override
	public void register(MicroService m) {
		microQueueMap.putIfAbsent(m,new LinkedBlockingQueue<>());//if m already registered nothing happens
	}
	@Override
	public  void  unregister(MicroService m) {
		LinkedBlockingQueue<Message> q2;
			messageHandle.forEach((key, value) -> {//deleting the m from being able to take care of a message
					value.remove(m);
			});
				q2 = microQueueMap.remove(m); //deleting him and his message queue

			while (q2 != null && !q2.isEmpty()) {//deleting his futures events
				Message msg = q2.poll();
				Future<?> f = futureMap.get(msg);
				if (f != null && f.isDone()) {
					f.resolve(null);
				}
			}
		}
	@Override
	public  Message awaitMessage(MicroService m) throws InterruptedException {

		try {
			LinkedBlockingQueue <Message>msgQueue=microQueueMap.get(m);
			return msgQueue.take(); //waiting for a message to arrive
		}
		catch (InterruptedException  e){ }

		return null;
	}

}
