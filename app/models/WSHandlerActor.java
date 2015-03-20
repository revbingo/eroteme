package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class WSHandlerActor extends UntypedActor {

	private final ActorRef out;

	public WSHandlerActor(ActorRef out) {
		this.out = out;
	}

	public static Props props(ActorRef out) {
		return Props.create(WSHandlerActor.class, out);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		System.out.println(out);
		if (message instanceof String) {
			out.tell("I received your message: " + message, self());
		}
	}
}
