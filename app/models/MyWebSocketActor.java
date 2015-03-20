package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MyWebSocketActor extends UntypedActor {

	private final ActorRef out;

	public MyWebSocketActor(ActorRef out) {
		this.out = out;
	}

	public static Props props(ActorRef out) {
		return Props.create(MyWebSocketActor.class, out);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			out.tell("I received your message: " + message, self());
		}
	}
}
