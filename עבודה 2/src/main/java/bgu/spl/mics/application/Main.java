package bgu.spl.mics.application;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LandoMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson gson = new Gson();
		Reader reader = null;
		try {
			reader = new FileReader(args[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Input input = gson.fromJson(reader, Input.class);

		Ewoks ewoks = Ewoks.getInstance();
		ewoks.setNumOfEwoks(input.getEwoks());

		Thread Leia = new Thread(new LeiaMicroservice(input.getAttacks()));
		Thread C3PO = new Thread(new C3POMicroservice());
		Thread HanSolo = new Thread(new HanSoloMicroservice());
		Thread R2D2 = new Thread(new R2D2Microservice(input.getR2D2()));
		Thread Lando = new Thread(new LandoMicroservice(input.getLando()));

		C3PO.start();
		HanSolo.start();
		R2D2.start();
		Lando.start();

		try {
			Leia.sleep(1000);
			Leia.start();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			C3PO.join();
			HanSolo.join();
			R2D2.join();
			Lando.join();
			Leia.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}


		Gson gb = new GsonBuilder().setPrettyPrinting().create();
		try {
			FileWriter fw = new FileWriter(args[1]);
			gb.toJson(Diary.getInstance(), fw);
			fw.flush();
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}
}
