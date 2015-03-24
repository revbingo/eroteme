package models;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class WSHandlerActor extends UntypedActor {

	private final ActorRef out;
	private final Map<String, Handler> handlers;
	
	public WSHandlerActor(ActorRef out) {
		this.out = out;
		handlers = new HashMap<String, Handler>();
		handlers.put("REGISTER", new Handler() {

			@Override
			public void handle(ActorRef out) {
				out.tell("REGISTERED", self());
			}
		});
	}

	public static Props props(ActorRef out) {
		return Props.create(WSHandlerActor.class, out);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (!(message instanceof String)) return;
		
		System.out.println(message);
		if(((String) message).startsWith("REGISTER")) {
			out.tell("REGISTERED", self());
			System.out.println("registered");
		} else {
			out.tell("ERROR", self());
		}
	}
}
