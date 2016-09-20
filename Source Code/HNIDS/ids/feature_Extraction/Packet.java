
package ids.feature_Extraction;  
import ids.deployment.*;

import java.util.ArrayList; 
import java.util.Date;  
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;  
import java.lang.Thread;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;  
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
    
	/**
	 * 
	 * @author Tanmay Bhagwat	
	 * @version 1.7
	 *
	 */
	
    public class Packet extends Thread {  
      
		static int i=0;
	    public static Queue<Packetqueue> queue = new ConcurrentLinkedQueue<Packetqueue>();
	    static boolean packetcaptureflag=true;
	    public static List<String> unique_ip = new ArrayList<String>();
	    public static List<String> unique_port = new ArrayList<String>();
	    public List<String[]> conn_info_temp = new LinkedList<String[]>();
	    
       public Packet()
       {
    	   super("Packet thread");
    	   System.out.println("Packet thread created" + this);
    	   start();
       }
       
       public int temp_received(int packets){
     	   int temp_packets = 0;
     	   temp_packets = packets/3 - 1;
     	   return temp_packets;
        }
              
       public void run() {  
    	   
            List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
            StringBuilder errbuf = new StringBuilder(); // For any error msgs  
      
            /*************************************************************************** 
             * First get a list of devices on this system 
             **************************************************************************/  
            int r = Pcap.findAllDevs(alldevs, errbuf);  
            if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
                System.err.printf("Can't read list of devices, error is %s", errbuf  
                    .toString());  
                return;  
            }  
      
            System.out.println("Network devices found:");  
      
            int i = 0;  
            for (PcapIf device : alldevs) {  
                String description =  
                    (device.getDescription() != null) ? device.getDescription()  
                        : "No description available";  
                System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);  
            }  
      
            PcapIf device = alldevs.get(4); // We know we have atleast 1 device  // 4 for Aaron's Testing
            System.out  
                .printf("\nChoosing '%s' on your behalf:\n",  
                    (device.getDescription() != null) ? device.getDescription()  
                        : device.getName());  
      
            /*************************************************************************** 
             * Second we open up the selected device 
             **************************************************************************/  
            int snaplen = 64 * 1024;           // Capture all packets, no truncation  
            int flags = Pcap.MODE_NON_PROMISCUOUS;//MODE_PROMISCUOUS; // capture all packets  
            int timeout = 10 * 1000;           // 10 seconds in millis  
            Pcap pcap =  
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);  
      
            if (pcap == null) {  
                System.err.printf("Error while opening device for capture: "  
                    + errbuf.toString());  
                return;  
            }  
                  
            /*************************************************************************** 
             * Third we create a packet handler which will receive packets from the 
             * libpcap loop. 
             **************************************************************************/  
            PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {  
            	/**
            	 * @param Packet and User
            	 * @return Captured IP packets will be pushed into the queue
            	 * 
            	 */
            	
                public void nextPacket(PcapPacket packet, String user) {  
                	
                	Packetqueue packetqueue = new Packetqueue();

                	Tcp tcp = new Tcp();
                	Udp udp = new Udp();
                	Ip4 ip = new Ip4();
                	Icmp icmp = new Icmp();
                	Http http = new Http();
                	byte[] sIP = new byte[4];
                	byte[] dIP = new byte[4];
                	if(packet.hasHeader(ip)&&packet.hasHeader(tcp)){
                		sIP = packet.getHeader(ip).source();
                		dIP = packet.getHeader(ip).destination();
                		packetqueue.name="TCP";                	   
                		packetqueue.destIP=org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                		packetqueue.sourceIP=org.jnetpcap.packet.format.FormatUtils.ip(sIP);                		
                		packetqueue.source_port=packet.getHeader(tcp).source();
                		packetqueue.destination_port=packet.getHeader(tcp).destination();
                		packetqueue.syn_flag=packet.getHeader(tcp).flags_SYN();
                		packetqueue.ack_flag=packet.getHeader(tcp).flags_ACK();
                		packetqueue.rst_flag=packet.getHeader(tcp).flags_RST();
                		packetqueue.timestamp=packet.getCaptureHeader().timestampInMillis();
                		packetqueue.plen=packet.getCaptureHeader().caplen();
                		if(packet.hasHeader(http)){
                			packetqueue.application_name = packet.getHeader(http).fieldValue(Http.Request.RequestVersion);                			
                			packetqueue.http_get = packet.getHeader(http).fieldValue(Http.Request.RequestMethod);
                			packetqueue.http_post = packet.getHeader(http).fieldValue(Http.Response.ResponseCodeMsg);
                  		}                		
                		if(!unique_ip.contains(org.jnetpcap.packet.format.FormatUtils.ip(sIP))){
                			unique_ip.add(org.jnetpcap.packet.format.FormatUtils.ip(sIP));
                		/*	for (int TA : unique_ip.get()) {
                		//		conn_info_temp.add(new String[] {packetqueue.sourceIP,packetqueue.sourceIP,indexincrement,starttime,endtime,pxsent,pxrec});
                		//	}

                			/*for(String[] na:host.process_info) { for(String val:na) {
                				  System.out.print(val+",\t"); } System.out.println(); }
                			 */
                		}
                		
                		String temp_orig_ip = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                		String temp_dest_ip = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                		HashMap<String,String> hashMap = Extraction.additionalInfo.get(temp_orig_ip);
                		if(hashMap == null){
                			hashMap = new LinkedHashMap<>();
                			hashMap.put("Packets_Received", 1+"");
                			hashMap.put("Packets_Sent", 0+"");
                			Extraction.additionalInfo.put(temp_orig_ip, hashMap);
                		}
                		else{
                			int temp_packets_received = Integer.parseInt(hashMap.get("Packets_Received"));
                			int temp_packets_sent = Integer.parseInt(hashMap.get("Packets_Sent"));                			
                			hashMap.put("Packets_Received",(temp_packets_received + 1)+"");
                			temp_packets_sent = temp_received(temp_packets_received);              			
                			hashMap.put("Packets_Sent",(temp_packets_sent)+"");                	
                		}
                		//Tcp header = packet.getHeader(tcp);
                		//System.out.println("Flags received for IP " + temp_ip + header);
                		if(packet.getHeader(tcp).flags_SYN() == true || packet.getHeader(tcp).flags_ACK() == true){
	                  		hashMap.put("Connection_Initiator",temp_dest_ip);
	                		hashMap.put("Start_Time", System.currentTimeMillis()+"");	                		
                		}
                		if(packet.hasHeader(http)){
                			hashMap.put("Type_Of_Connection","TCP + HTTP");
                		}
                		else{
                			hashMap.put("Type_Of_Connection","TCP + Other");
                		}
                		if(packet.getHeader(tcp).flags_FIN() == true || packet.getHeader(tcp).flags_RST() == true){
                			hashMap.put("End_Time", System.currentTimeMillis()+"");
                		}
                   		queue.add(packetqueue);
                   		
                	}
                	else if(packet.hasHeader(icmp)){
                		packetqueue.name="ICMP";
                		System.out.println(packetqueue.name);
                	}
                	
                	else if(packet.hasHeader(ip)&&packet.hasHeader(udp)){

                		sIP = packet.getHeader(ip).source();
                		dIP = packet.getHeader(ip).destination();
                		packetqueue.name="UDP";                 	   
                		packetqueue.destIP=org.jnetpcap.packet.format.FormatUtils.ip(dIP);                		
                		packetqueue.sourceIP=org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                		packetqueue.source_port=packet.getHeader(udp).source();
                		packetqueue.destination_port=packet.getHeader(udp).destination();
                		packetqueue.timestamp=packet.getCaptureHeader().timestampInMillis();
                		packetqueue.plen=packet.getCaptureHeader().caplen();
                		queue.add(packetqueue);
                		
                		if(!unique_ip.contains(org.jnetpcap.packet.format.FormatUtils.ip(sIP))){
                			unique_ip.add(org.jnetpcap.packet.format.FormatUtils.ip(sIP));
                		}
                		                		
                		String temp_orig_ip = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                		String temp_dest_ip = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                		HashMap<String,String> hashMap = Extraction.additionalInfo.get(temp_orig_ip);
                		if(hashMap == null){
                			hashMap = new LinkedHashMap<>();
                			hashMap.put("Packets_Received", 1+"");
                			hashMap.put("Packets_Sent", 0+"");
                			Extraction.additionalInfo.put(temp_orig_ip, hashMap);
                		}
                		else{
                			int temp_packets_received = Integer.parseInt(hashMap.get("Packets_Received"));
                			int temp_packets_sent = Integer.parseInt(hashMap.get("Packets_Sent"));                			
                			hashMap.put("Packets_Received",(temp_packets_received + 1)+"");
                			temp_packets_sent = temp_received(temp_packets_received);
                			hashMap.put("Packets_Sent",(temp_packets_sent)+"");                	
                		}
                		hashMap.put("Connection_Initiator", temp_dest_ip);
                		hashMap.put("Start_Time", System.currentTimeMillis()+"");
                		if(packet.hasHeader(http)){
                			hashMap.put("Type_Of_Connection","UDP + HTTP");
                		}
                		else{
                			hashMap.put("Type_Of_Connection","UDP + Other");
                		}
                		hashMap.put("End_Time", "NA");
                	}
                	
                }  
            };  
      
              
            /*while(packetcaptureflag==true)
            {	
            pcap.loop(100000, jpacketHandler, "");  
            
            /*************************************************************************** 
             * Last thing to do is close the pcap handle 
             **************************************************************************  
            //pcap.close();  
            }*/
            pcap.loop(Pcap.LOOP_INFINITE,jpacketHandler,"");
            pcap.close();
         }  
    }  