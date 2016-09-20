package ids;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import ids.cep.CEP;
import ids.communication.Update;
import ids.deployment.Device;
import ids.deployment.Host;

public class Main {
	static String RuleFiles[] = new String[] { "ids/cep/rules/SystemUsage.drl" };
	static String AdminProcess[] = new String[] { "wireshark","pcap","sniffer" };
	static String AuthorizedUsers[] = new String[] {"root","admin"};
	
	public static Update update_server;
	public static void main(String arg[]) throws Exception {
		//((Host)devices.get(0)).mem.getRam()
		/*Host temp = new Host("A");
		temp.setRAMUsage(99.9);
		devices.add(temp);

		Host temp1 = new Host("B");
		temp1.setRAMUsage(40);
		devices.add(temp1);
		
		Host temp2 = new Host("C");
		temp2.setRAMUsage(70);
		devices.add(temp2);*/
		//Extraction extract = new Extraction(cep_device);					//thread 3
		//extract.start();
		/*Exception in thread "Thread-0" java.lang.UnsatisfiedLinkError: org.hyperic.sigar.Mem.gather(Lorg/hyperic/sigar/Sigar;)V
		at org.hyperic.sigar.Mem.gather(Native Method)
		at org.hyperic.sigar.Mem.fetch(Mem.java:30)
		at org.hyperic.sigar.Sigar.getMem(Sigar.java:304)
		at ids.feature_Extraction.Extraction.getFeaturesHost(Extraction.java:62)
		at ids.feature_Extraction.Extraction.run(Extraction.java:285)*/
		
		AlertCode.initializeAlertsList();
		ClassLoader loader = Main.class.getClassLoader();
		System.out.println(loader.getResource("cep/SystemUsage.drl"));
		System.out.println("Update Server started as a new thread...");
		update_server = new Update(5000, true, "127.0.0.1");
		update_server.start();
		
		System.out.println("CEP Engine started as a new thread...");
		Thread cep = new Thread(new CEP(RuleFiles, Update.devices, true));
		cep.start();


	}
	
	boolean isAuthReqd(String p_name) {
		for(String proc:AdminProcess) {
			if(proc.contains(p_name)) {
				return true;
			}
			
		}
		return false;
	}

	boolean isAuthUser(String user) {
		for(String a_user:AuthorizedUsers) {
			if(user.equals(a_user)) {
				return true;
			}
			
		}
		return false;
	}
}
