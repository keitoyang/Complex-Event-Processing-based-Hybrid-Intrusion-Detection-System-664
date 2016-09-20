/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids.sbm;
import ids.Alert;
import ids.deployment.Device;
import ids.deployment.Host;
import ids.deployment.Network;
import ids.feature_Extraction.Extraction;
import ids.feature_Extraction.Packet;
import ids.feature_Extraction.Packetqueue;

import java.lang.Thread;
/* To calculate Average Detection Time */
import java.util.*;
import java.util.Map.Entry;
import java.text.*;

/* References:
http://beginnersbook.com/2013/12/how-to-loop-arraylist-in-java/
http://www.tutorialspoint.com/java/java_arraylist_class.htm
http://stackoverflow.com/questions/10279553/accesing-static-variable-from-another-class-in-java
https://en.wikipedia.org/wiki/LAND
https://en.wikipedia.org/wiki/Smurf_attack
*/

/**
 *
 * @author ahuber
 * @version 1.4
 * 
 */
public class Sbm extends Thread {//implements Runnable or extends Thread
	Device device;
	Host host;
	Network net;
    Packetqueue packet;
    long initial_time;                               					//save the initial time of the start of the packet capture
    int syn_count = 0;                                                      //counter for the number of syn flags
    int hanging_syns;
    int ack_count;														//number of ack flags
    int syn_thresh;                                                     //holds threshold value for number of hanging syn flags per extraction
    ArrayList<Integer> syn_ave_thresh;
    int syn_ave_threshold;
    double ave_thresh_deviation = 1.25;
    boolean syn_training = true;
    int training_rounds = 5;
    HashMap<String, Integer> synIPPort = new LinkedHashMap<>();
    HashMap<Integer, Integer> port_scan = new LinkedHashMap<>();
    int unique_ports = 0;
    int current_dest_port;                                              //holds current packet destination port number
    int port_thresh;                                                    //threshold value per extraction
    long time_thresh;         											//time for each extraction cycle
    int packets_read;
    
    /* For average time calculations */
    //long difference []= new long[6];
    //int count = 0;
    
    /**
     * Initialize the thread and set threshold values
     
     */
    
	public Sbm(Device d)														///* Initialize */
	{
		super("SBM thread");
		/*Unique host/network instance is passed through 
		 * constructor from Dispatch class; if device type
		 * is 1, it's a host, otherwise, it's a network
		 */
		device = d;
		if(device.getTypeofDevice() == 1){
			host = (Host)device;
		}
		else 
			net=(Network)device;
		
		
		//System.out.println("SBM thread created for Device : " + host.getDeviceID());
	    syn_ave_thresh = new ArrayList<Integer>();
	    port_thresh = 100;
	    time_thresh = 10000;					//10 seconds
	    syn_thresh = 10000;	    
	    start();								//CALL start() to induce run() for this thread;
												//if you call run(), then main thread will run the code in run()
	    
	}
	
    /**
     * Reads network data for packet analysis
     
     * @param packet
     */
    /* detectAttack gathers specific packet information for further analysis */
    public void detectAttack(Packetqueue packet)
    {
    	/* Detect syn flood attack */
        if(packet.syn_flag == true)
        {
            hanging_syns++;
            synIPPort.put(packet.sourceIP, packet.source_port);
           
        }
        if(packet.ack_flag == true)
        {
        	ack_count++;
        	for(HashMap.Entry<String, Integer> entry : synIPPort.entrySet())
        	{
        		if(packet.sourceIP.equals(entry.getKey()))
        		{
        			if(packet.source_port == entry.getValue())
        			{
        				synIPPort.remove(entry.getKey());
        				hanging_syns--;
        			}
        				
        		}
        	}
        	
        }
        
        /* Detect port scan */
        /* We are counting syn flags */
        if(packet.syn_flag == true)
        {
        	if(port_scan.containsKey(packet.destination_port))
        	{
        		int value = port_scan.get(packet.destination_port);
        		if(value == 0)
        			unique_ports++;
        		port_scan.put(packet.destination_port, ++value);
        	}
        	else
        	{
        		port_scan.put(packet.destination_port, 1);
        		unique_ports++;
        	}
        	
        	
        }
        /* We're decrementing matching ack flags */
        if(packet.ack_flag == true)
        {
        	if(port_scan.containsKey(packet.destination_port))
        	{
        		int value = port_scan.get(packet.destination_port);
            	port_scan.put(packet.destination_port, --value);
            	if(value == 0)
            		unique_ports--;
        	}
        	
        }
           
        /* Detect Land Attack */
        if(packet.sourceIP.equals(packet.destIP))											
        {   
        	if(packet.destination_port == packet.source_port)
        	{
        		System.out.println("LAND ATTACK DETECTED!");
        		if(host!=null) 
        			updateAlert(new Alert((Device)host, 7));
        		else
        			updateAlert(new Alert((Device)net, 7));
        		
        	}
            
        }
        
    }
    /**
     * Adds a fresh alert to alert_list.  If 
     * an alert of the same code exists in alert
     * list, the old alert is replaced with the new one.
     
     * @param a
     */
    
    public synchronized void updateAlert(Alert a)
    {
    	boolean flag = false;
    	int to_delete = 0;
    	
    	if(a.getAssociatedDeviceType() == 1)
    	{
    		for(Alert alert : host.alert_list)
        	{
        		
        		if(a.getAlertCode()==alert.getAlertCode())
        		{
        			to_delete = host.alert_list.indexOf(alert);
        			flag = true;
        			break;
        		}
        	}
        	if(flag == false)
        		host.alert_list.add(a);
        	else{
        		host.alert_list.remove(to_delete);
        		host.alert_list.add(a);
        	}
        	System.out.println("Alert_list size: <<"+host.alert_list.size()+">>");
    	}
    	else
    	{
    		for(Alert alert : net.alert_list)
        	{
        		
        		if(a.getAlertCode()==alert.getAlertCode())
        		{
        			to_delete = net.alert_list.indexOf(alert);
        			flag = true;
        			break;
        		}
        	}
        	if(flag == false)
        		net.alert_list.add(a);
        	else{
        		net.alert_list.remove(to_delete);
        		net.alert_list.add(a);
        	}
        	System.out.println("Alert_list size: <<"+net.alert_list.size()+">>");
    	}
    	
    	
    }
    /**
     * Pops packet queue sequentially for time time_thresh.
     * Keeps track of the number of packets read.  Also
     * monitors training for syn threshold.
     
     * 
     */
    public void extractData()
    {
    	initial_time = packet.timestamp; 										//starting time
    	detectAttack(packet);
    	packets_read++;
    	
        while((packet.timestamp - initial_time) < time_thresh)                  //for time_thresh units, retrieve values
            {
        		if(Packet.queue.isEmpty() == false)
        		{
        			packet = Packet.queue.poll();                                   //pop the queue for another packet
        			packets_read++;
                    detectAttack(packet);                                           //extract values
        		}
                
            }
        if(syn_training)
        	setAverageThreshold();
        	
    }
    /**Calculate the average threshold value 
     * for a syn flood attack.
     */
    
    public void setAverageThreshold()
    {
    	syn_ave_thresh.add(syn_count);
    	if(syn_ave_thresh.size() == training_rounds)
    	{
    		syn_training = false;
    		for(Integer val : syn_ave_thresh)
    		{
    			syn_ave_threshold += val;
    		}
    		syn_ave_threshold = syn_ave_threshold / training_rounds;
    	}
    }
    /**Aggregates the analyzed data and flags alerts 
     * when attacks are detected.
     */
    /* evaluateThreshold analyzes captured data */
    public synchronized void evaluateThreshold()
    {/* Timing is to start right here 
    	
        Date start = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Date unformatted: " + start.toString());
        System.out.println("Date formatted: " + sdf.format(start) );  */ 	  	   

    	 System.out.println("PORT SCAN hashmap size: " + port_scan.size());
         System.out.println("UNIQUE PORTS: " + unique_ports);
         for(Integer val : port_scan.keySet())
         {
        	 if(port_scan.get(val) <= 0)
        		 System.out.println(val);
         }
         if(unique_ports > port_thresh)									//if there are port_thresh unique port numbers
         {
         	System.out.println("PORT SCAN DETECTED!");
         	if(host!=null) 
    			updateAlert(new Alert((Device)host, 5));
    		else
    			updateAlert(new Alert((Device)net, 5));
         	//System.exit(1);
         }
       /*manual and average thresholds
     	test if average threshold is broken by 25% or more
     	if both thresholds are broken, it's a syn flood
     	 */   
        if(hanging_syns > syn_thresh)												//if there are syn_thresh unacked syn packets
        {//manual threshold
        	if(hanging_syns >= (syn_ave_threshold * ave_thresh_deviation))
        	{
        		System.out.println("SYN FLOOD DETECTED!");
        		if(host!=null) 
        			updateAlert(new Alert((Device)host, 6));
        		else
        			updateAlert(new Alert((Device)net, 6));
            	//System.exit(1);
        	}
        }  
        
        
        /* Time calculations concluded 
        Date end = new Date();
        difference[count] = end.getTime() - start.getTime(); 
        System.out.println("The time to evaluate from " + start.toString() + " to " + end.toString() + " is: " + difference + " milliseconds.");*/
        /* logging */
    	
    	System.out.println("syn_count = " + hanging_syns);
    	System.out.println("ack_count = " + ack_count);
    	/* END: logging */
    	
    	/* Reset variable values */
        hanging_syns = 0;
        ack_count = 0;
        port_scan.clear();
        
    	
    	
    	
        
    }
       
    public void run() {//here is the run method for threading--this method is analogous to main
        // TODO code application logic here
    	
    	packets_read = 0;
       /* Operating Loop */
    	while(true)
        {
        	/* Wait for a non-empty queue */
			while(Packet.queue.isEmpty())
			{
				try {
					//System.out.println("Packet.queue.isEmpty()");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println(this.getName() + " queue size is: " + Packet.queue.size());
			}
			packet = Packet.queue.poll();	

            this.extractData();
            this.evaluateThreshold();
            //System.out.println("Packets read: " + packets_read);
            //count++;
        }
    	
    	/* Time calculations 
    	int average = 0;
    	for (long num : difference)
		{
			average += num;
		}
    	average = average / count;
    	System.out.println("The average time on " + count + " runs is " + average + ".");
    	System.exit(1);*/
    	/* End time calculations */
        
        //System.exit(0);
    }
    
}
