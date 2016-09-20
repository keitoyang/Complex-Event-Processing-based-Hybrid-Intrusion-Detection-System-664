/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids.sbm;
import ids.feature_Extraction.Packetqueue;  
import ids.feature_Extraction.Packet;
import java.util.ArrayList;  
import java.util.Date;  
import java.util.List;  
      



import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;  
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;  
/*<<<<<<< HEAD

=======
>>>>>>> 4eb43bb387ae72b0f5e68143b1bf2a1835b08376 */
import org.hyperic.sigar.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 *
 * @author ahuber
 */
public class Check_Cmp {
    
   
    
   
    
    
   /*

    Check_Cmp(){
        int thresh_flag = 3;    //Extraction class will have a flag indicating the need for aggregation; this is a statically declared variable in his package and will shortly be 
                                //removed.
        
        //Extraction static variable access example: Extraction.cpu
        //Extraction class variables follow the same namescheme as below without the _loc extension
        
        //Below are the local variables for storing Real Time Data
      
        long Timestamp_loc;        
        //from Sigar library (types are derived therefrom)      
        Cpu cpu_loc;
        Mem mem_loc;
        DiskUsage disk_loc;
        CpuInfo[] cpuinfo_loc;
        FileSystem[] filesystem_loc;
        NetInfo netinfo_loc;
        NetInterfaceConfig netinterfaceconfig_loc;
        
        //This list of variables hold the threshold values for the respective system properties
        Cpu cpu_th;
        Mem mem_th;
        DiskUsage disk_th;
        CpuInfo[] cpuinfo_th;
        FileSystem[] filesystem_th;
        NetInfo netinfo_th;
        NetInterfaceConfig netinterfaceconfig_th;
       
        /* Here is where we may want to check to see if there is a configuration file.
        If there is, then we set thresh_flag to 1.  If there isn't, we set thresh_flag to 3. 
        Everytime you start the program, the static flag would have to be 're'-initialized, correct?
        Therefore, if appears that we will need to verify the existence or nonexistence of the config
        file to know how to go about matters.  If this is the case, may the check should be done in the
        extraction class, and the thesh_flag set accordingly.
        */
     /*   while(true){
            if(thresh_flag == 3){
               /* If thresh_flag is set to 3, then the extraction class is aggregating threshold data
                and we need to wait for that to finish.
                */
        /*        System.out.println("Thresh_flag = 3.");     //for test purposes only
            }
            else if(thresh_flag == 0)
            {
               while(thresh_flag == 0){
                    //here is where we set the threshold values using aggregated data
                    //next, we create a file and save the values therein
                    thresh_flag = 1;
                    System.out.println("thresh_flag = 1");
                }
              
            }
            else{
                //this is where we have a config file; we thus open it and read in the data to the threshold variables
                while(alert == 0){
                    //this is where we read real time data
                    //then we want to compare that data to our threshold values
                    
                    /* We need to know which data we want to compare and how to parse that
                    data to get the values we want, as the data types we want
                    */
                    
              /*  }
            }
            
        }
        
}
    public void compare(int real, int thresh){
        if(real > thresh){
            alert = 1;
            System.out.println(alert);
        }
    }*/
}
