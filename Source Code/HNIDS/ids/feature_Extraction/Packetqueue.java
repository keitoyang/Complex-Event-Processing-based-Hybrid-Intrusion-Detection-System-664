package ids.feature_Extraction;

import java.util.Date;

import ids.feature_Extraction.*;

import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
/**
 * 
 * @author Tanmay Bhagwat
 * @version 1.0
 *
 */

public class Packetqueue {
	//Tcp tcp = new Tcp();
	//Udp udp = new Udp();
	//Ip4 ip = new Ip4();
	Http http_header = new Http();
	
	public long timestamp;
	public byte[] sIP = new byte[4];
	public byte[] dIP = new byte[4];
	public String sourceIP="";
	public String destIP="";
	public boolean ack_flag;
	public boolean syn_flag;
	public boolean rst_flag;
	public int destination_port;
	public int source_port;
	public int plen;
	public String name;							// TCP or UDP or ICMP
	public String application_name;				// HTTP
	public String http_post;					
	public String http_get;

	
	public Packetqueue(){
		
	}
	
}
//prpocess level