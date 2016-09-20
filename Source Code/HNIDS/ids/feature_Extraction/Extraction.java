
package ids.feature_Extraction;

import ids.communication.Update;
import ids.deployment.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.hyperic.sigar.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.*;
import java.net.*;

/**
 * 
 * @author Tanmay Bhagwat
 * @version 1.7
 */

public class Extraction extends Thread {

	static Device device;
	static Host host;
	static Network network;
	static int type;
	String hostip;
	int subnetmask = 0;
	InetAddress ip;
	public List<InterfaceAddress> interface_info;
	public String adapter_name;
	public long process_id;
	
	
	public static HashMap<String, HashMap<String, String>> additionalInfo = new LinkedHashMap<>();

	Sigar sigar = new Sigar();

	// static Network network;

	public Extraction(Device d) {
		device = d;
		if (device.getTypeofDevice() == 1) {
			host = (Host) device;
		} else
			network = (Network) device;
	}

	public Extraction(Host d) {
		device = d;
		host = d;
	}

	public void getFeaturesHost() {
		try {

			//host.mem_usage = sigar.getMem().getActualUsed();
			host.mem_usage = sigar.getMem().getUsedPercent();
			host.cpu_usage = sigar.getCpuPerc().getCombined()*100;
			host.disk_usage_read = sigar.getDiskUsage("C:").getReadBytes();
			host.disk_usage_write = sigar.getDiskUsage("C:").getWriteBytes();
			host.swap_space = sigar.getSwap().getUsed();
			host.os_version = System.getProperty("os.version");
			host.external_ip = Packet.unique_ip;

			if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
				host.os_name = "Windows";
			} else if (System.getProperty("os.name").toUpperCase()
					.contains("LINUX")) {
				host.os_name = "Linux";
			} else {
				host.os_name = "MAC";
			}
			
			System.out.println(host.os_name);
			//System.out.println(additionalInfo); // Debugging for Connection Information	
			
			for (String NA : sigar.getNetInterfaceList()) {
				if (sigar.getNetInterfaceStat(NA).getRxBytes() > 0) {
					adapter_name = NA;
					host.network_info.add(new String[] {
							sigar.getNetInterfaceConfig(adapter_name).getName(),
							sigar.getNetInterfaceConfig(adapter_name).getHwaddr(),
							sigar.getNetInterfaceConfig(adapter_name).getAddress(),
							Long.toString(sigar.getNetInterfaceStat(adapter_name).getRxBytes() / 1000),
							Long.toString(sigar.getNetInterfaceStat(adapter_name).getTxBytes() / 1000) });
					break;
				}
			}


			/*
			  for(String[] na:host.network_info) { for(String val:na) {
			  System.out.print(val+",\t"); } System.out.println(); }
			 */

			for (Long TA : sigar.getProcList()) {
				process_id = TA;
				host.process_info.add(new String[] { Long.toString(process_id), System.getProperty("user.name") });
			}

			/*for(String[] na:host.process_info) { for(String val:na) {
				  System.out.print(val+",\t"); } System.out.println(); }
			 */

			/* Firewall Status */
			StringBuilder output = new StringBuilder();
			Process p;
			try {
				p = Runtime.getRuntime().exec(
						"netsh advfirewall show allprofiles state");
				p.waitFor();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line = "";

				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
					// System.out.printf("%s",output.toString());
					if (output.toString().contains("ON")) {
						host.firewall.add(true);
						host.firewall.add(true);
						break;
					}
					host.firewall.clear();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StringBuilder output1 = new StringBuilder();
			Process p1;
			String temp = System.getProperty("user.name");
			String privilage = null;
			try {
				p1 = Runtime.getRuntime().exec("net user " + temp);
				p1.waitFor();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p1.getInputStream()));
				String line = "";

				while ((line = reader.readLine()) != null) {
					output1.append(line + "\n");
					//System.out.printf("%s", output1.toString());
					if (output1.toString().toUpperCase()
							.contains("*ADMINISTRATORS")) {
						privilage = "Administrator";
					} else {
						privilage = "Non-administrator user";
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			host.session_info.add(new String[] {temp,privilage});

			/*for(String[] na:host.session_info) { for(String val:na) {
				  System.out.print(val+",\t"); } System.out.println(); }
			 */

		} catch (Exception e) {
			System.out.println("Exception in feature extraction " + e);
			e.printStackTrace();
		}
		Update.fresh = true;
	}

	public void getFeaturesNetwork() {
		try{

			//host.mem_usage = sigar.getMem().getActualUsed();
			network.mem_usage = sigar.getMem().getUsedPercent();
			network.cpu_usage = sigar.getCpuPerc().getCombined()*100;
			network.disk_usage_read = sigar.getDiskUsage("C:").getReadBytes();
			network.disk_usage_write = sigar.getDiskUsage("C:").getWriteBytes();
			network.swap_space = sigar.getSwap().getUsed();
			network.os_version = System.getProperty("os.version");
			network.external_ip = Packet.unique_ip;

			if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
				network.os_name = "Windows";
			} else if (System.getProperty("os.name").toUpperCase()
					.contains("LINUX")) {
				network.os_name = "Linux";
			} else {
				network.os_name = "MAC";
			}

			for (String NA : sigar.getNetInterfaceList()) {
				if (sigar.getNetInterfaceStat(NA).getRxBytes() > 0) {
					adapter_name = NA;
					network.network_info.add(new String[] {
							sigar.getNetInterfaceConfig(adapter_name).getName(),
							sigar.getNetInterfaceConfig(adapter_name).getHwaddr(),
							sigar.getNetInterfaceConfig(adapter_name).getAddress(),
							Long.toString(sigar.getNetInterfaceStat(adapter_name).getRxBytes() / 1000),
							Long.toString(sigar.getNetInterfaceStat(adapter_name).getTxBytes() / 1000) });
					break;
				}
			}

			
			/*
		  for(String[] na:network.network_info) { for(String val:na) {
		  System.out.print(val+",\t"); } System.out.println(); }
			 */

			for (Long TA : sigar.getProcList()) {
				process_id = TA;
				network.process_info.add(new String[] { Long.toString(process_id),
						System.getProperty("user.name") });
			}

			/* Firewall Status */
			StringBuilder output = new StringBuilder();
			Process p;
			try {
				p = Runtime.getRuntime().exec(
						"netsh advfirewall show allprofiles state");
				p.waitFor();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line = "";

				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
					// System.out.printf("%s",output.toString());
					if (output.toString().contains("ON")) {
						network.firewall.add(true);
						network.firewall.add(true);
						break;
					}
					network.firewall.clear();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StringBuilder output1 = new StringBuilder();
			Process p1;
			String temp = System.getProperty("user.name");
			String privilage = null;
			try {
				p1 = Runtime.getRuntime().exec("net user " + temp);
				p1.waitFor();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p1.getInputStream()));
				String line = "";

				while ((line = reader.readLine()) != null) {
					output1.append(line + "\n");
					//System.out.printf("%s", output1.toString());
					if (output1.toString().toUpperCase()
							.contains("*ADMINISTRATORS")) {
						privilage = "Administrator";
					} else {
						privilage = "Non-administrator user";
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			network.session_info.add(new String[] {temp,privilage});

			/*for(String[] na:network.session_info) { for(String val:na) {
			  System.out.print(val+",\t"); } System.out.println(); }
			 */

		} catch (Exception e) {
			System.out.println("Exception in feature extraction " + e);
			e.printStackTrace();
		}
		Update.fresh = true;
	}

	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			if (device.getTypeofDevice() == 1) {

				this.getFeaturesHost();
				System.out.println("Host Mode ON");
			} else {
				System.out.println("Network Mode ON");
				this.getFeaturesNetwork();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Extraction object = new Extraction(host);
	}
}
