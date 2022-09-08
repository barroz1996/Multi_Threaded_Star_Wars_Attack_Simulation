package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.GsonBuilder;


import  java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {

		try {
			Input json=JsonInputReader.getFromJson(args[0]);
			System.out.println(json);
			long R2D2duration=json.getR2D2();
			long Landoduration=json.getLando();
			int numOfEwoks=json.getEwoks();
			Attack [] attacks=json.getAttacks();
			List<MicroService> Micro=new ArrayList<>();
			List<Thread> threads=new ArrayList<>();
			LeiaMicroservice leia=new LeiaMicroservice(attacks);
			HanSoloMicroservice Han=new HanSoloMicroservice();
			Micro.add(Han);
			C3POMicroservice C3P=new C3POMicroservice();
			Micro.add(C3P);
			R2D2Microservice R2D2=new R2D2Microservice(R2D2duration);
			Micro.add(R2D2);
			LandoMicroservice Lando=new LandoMicroservice(Landoduration);
			Micro.add(Lando);
			Ewok [] EwokArray=new Ewok[numOfEwoks]; //we will send it to a function called addEwoks in Ewoks
			for(int i=1;i<=numOfEwoks;i++){
				EwokArray[i-1]=new Ewok(i);
			}
			Ewoks ewoks=Ewoks.getInstance();
			ewoks.addEwoks(EwokArray);
			for(MicroService m: Micro){
				Thread thread=new Thread(m,m.getName());
				threads.add(thread);
				thread.start();
			}

			CountDownInit countInit=CountDownInit.getInstance();//so Leia wont start before everyone else init
			Thread thread1=new Thread(leia,leia.getName());
			threads.add(thread1);
			countInit.aWaitCount(); //waiting for everybody else to init before leia start
			thread1.start();
			for (Thread thread: threads){
				try {
					thread.join();
				}
				catch (Exception e){}
			}
			Diary d=Diary.getInstance();
			Gson gson=new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer=new FileWriter(args[1]);
			gson.toJson(d,writer);
			writer.flush();
			writer.close();

		}catch (IOException e){
			System.out.println("Path not exist!");
		}

	}
}
