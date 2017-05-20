package controllers;

import models.QuizMaster;
import models.forms.CreateQuizForm;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.configure;
import views.html.admin.index;

import javax.inject.Inject;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Admin extends Controller {

	@Inject private FormFactory factory;

	@Inject private QuizMaster quizMaster;

	public Result index() {
		if(quizMaster.getQuizState() == QuizMaster.QuizState.NOT_STARTED) {
			return redirect("/newQuiz");
		}
		String url = "http://" + request().host();
		if(url.contains("localhost")) {
			String ip = getHostAddress();
			url = url.replace("localhost", ip);
		}
		return ok(index.render(url));
	}

	public Result configure() {
		return ok(configure.render());
	}

	public Result create() {
		CreateQuizForm quizConfig = factory.form(CreateQuizForm.class).bindFromRequest().get();
		quizMaster.startQuiz(quizConfig);
		return redirect("/admin");
	}

	public Result qrCode(String url) {
		return ok(QRCode.from(url).to(ImageType.GIF).stream().toByteArray()).as("image/gif");
	}

	private String getHostAddress() {
		Set<String> HostAddresses = new HashSet<>();
		try {
			for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (!ni.isLoopback() && ni.isUp() && ni.getHardwareAddress() != null) {
					for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
						if (ia.getBroadcast() != null) {  //If limited to IPV4
							HostAddresses.add(ia.getAddress().getHostAddress());
						}
					}
				}
			}
		} catch (SocketException e) { }
		return HostAddresses.toArray(new String[0])[0];
	}

}
