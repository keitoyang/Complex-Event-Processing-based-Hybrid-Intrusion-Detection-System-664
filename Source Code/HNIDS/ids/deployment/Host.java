/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids.deployment;
import ids.Alert;

import java.io.Serializable;
import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.hyperic.sigar.*;

import Jama.Matrix;
import ids.Alert.*;
/**
 *
 * Used to provide a network abstraction in terms of its attributes
 * @author Tanmay Bhagwat
 * @version 1.7
 */
public class Host extends Device implements Serializable{
	private int FeaturesCount;

	public double mem_usage;											//RAM Usage in percentage (E.g: 65.23 = 65%)
	public List<String> external_ip = new LinkedList<>();				//Returns a list of all external ip addresses
	public List<String> internal_ip = new LinkedList<>();				//Future Scope											
	public double cpu_usage;											//CPU Usage in percentage (E.g: 5.3 = 5%)
	public long swap_space;												//Returns swap space in bytes E.g: 5089173504 = 5.05 GB 
	public List<Boolean> firewall = new LinkedList<>();					//Firewall Status (E.g.: [true, true]) - i.e. USER, DOMAIN both are true
	public List<String[]> network_info = new LinkedList<String[]>();	//Return a dynamic list of [NW Adapter,MAC ADDR,IP ADDR,DOWNSPEED,UPSPEED] E.g: eth14,74:E5:43:59:5A:22,192.168.0.11,4089,1269	
	public List<String[]> process_info = new LinkedList<String[]>();	//Return process info dynamic List Eg: [9292,USM@rine] - i.e. PID, username who started 
	public List<String[]> session_info = new LinkedList<String[]>();	//Return session info dynamic List E.g. [USM@rine, Administrator] - i.e. username - privilage of the user 
	public HashMap<String, HashMap<String, String>> conn_info = new HashMap<>(); //Returns Hashmap of Hashmap E.G: 192.168.0.11={Packets_Received=17, Packets_Sent=6, Connection_Initiator=209.18.47.62, Start_Time=1461042335603, Type_Of_Connection=UDP + Other, End_Time=NA}
																				/* Hashmap Structure:
																				 Key for level one hash-map:	
																				 The source IP addresses are keys here (e.g. server sending data to client: here server is the source IP and client is the destination IP OR the connection originator)
																					Keys for second level hash-map:
																					1. Packets_Received
																					2. Packets_Sent
																					3. Connection_Initiator
																					3. Start_Time
																					4. End_Time
																					5. Type_Of_Connection																				  
																				  */

	public List<String> getIPlist(){
		List<String> IPs = new ArrayList<String>();
		for (String adapter[]:network_info)
			IPs.add(adapter[2]);
		return IPs;
	}
	
	public boolean hasIP(List<String> IPs){
		for (String adapter[]:network_info){
			for(String IP:IPs){
				if(adapter[2].equals(IP)){
					return true;
				}
			}		
		}
		return false;
	}
	
	public Host(String DeviceID) {
		super(1,DeviceID);

	}

	/**
	 * Returns the number of host features stored in this Host object
	 * @return Number of host features stored in this Host object
	 */
	public int getFeaturesCount() {
		return FeaturesCount;
	}

	/**
	 * Returns the features stored in this Host object
	 * @return Features stored in this Host object
	 */
	public Matrix getFeatureVector() {
		return null;
	}
	
	/**
	 * Returns RAM Usage in percentage
	 * @return RAM Usage in percentage
	 */
	public double getmem_usage() {
		return mem_usage;
	}
	
	/**
	 * Returns CPU Usage in percentage
	 * @return CPU Usage in percentage
	 */
	public double getcpu_usage(){
		return cpu_usage;
	}
	
	/**
	 * Returns System swap space in bytes
	 * @return System swap space in bytes
	 */
	public long getswap_space(){
		return swap_space;
	}

	/**
	 * Returns a list of external IP addresses
	 * @return a list of external IP addresses
	 */
	public List<String> getexternal_ip(){
		return external_ip;
	}

	/**
	 * Returns the status of Firewall
	 * @return the boolean status of Firewall for user and domain
	 */
	public List<Boolean> getfirewall(){
		return firewall; 
	}

	/**
	 * Returns a dynamic list of [NW Adapter,MAC ADDR,IP ADDR,DOWNSPEED,UPSPEED] E.g: eth14,74:E5:43:59:5A:22,192.168.0.11,4089,1269
	 * @return a dynamic list of all network adapters with their MAC addresses, IP addresses, UPLink and DownLink speed
	 */
	public List<String[]> getnetwork_info(){
		return network_info;
	}
	
	/**
	 * Returns a dynamic list of session information E.g. [USM@rine, Administrator] - i.e. username - privilage of the user
	 * @return a dynamic list of session information E.g. [USM@rine, Administrator] - i.e. username - privilage of the user
	 */
	public List<String[]> getsession_info(){
		return session_info;
	}
	
	/**
	 * Returns a dynamic list of process info dynamic List Eg: [9292,USM@rine] - i.e. PID, username who started
	 * @return a dynamic list of process info dynamic List Eg: [9292,USM@rine] - i.e. PID, username who started
	 */
	public List<String[]> getprocess_info(){
		return process_info;
	}
		
	/**
	 * Returns a Hashmap of Hashmap E.G: 192.168.0.11={Packets_Received=17, Packets_Sent=6, Connection_Initiator=209.18.47.62, Start_Time=1461042335603, Type_Of_Connection=UDP + Other, End_Time=NA}
	 * @return a Hashmap of Hashmap E.G: 192.168.0.11={Packets_Received=17, Packets_Sent=6, Connection_Initiator=209.18.47.62, Start_Time=1461042335603, Type_Of_Connection=UDP + Other, End_Time=NA}
	 */
	public HashMap<String, HashMap<String, String>> getconn_info(){
		return conn_info;
	}
}
