package com.rest.controller;

import java.lang.management.ManagementFactory;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.servlet.http.HttpServletRequest;
import ids.Alert;
import ids.Main;
import ids.deployment.Device;
import ids.deployment.Host;
import ids.deployment.Network;
import ids.communication.Update;

/**
 * 
 * @author anon
 *
 */
@Path("/ids")
public class RestController {
	
	int i;
	final String delimiter="|";
	static {
		try {
			Main.main(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("ranjan")
	public String sayPlainTextHello() {
		return "Hello Ranjan";
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sizeOfDevices")
	public String sizeOfDevices() {
		List<Device> devices = Update.devices;
		if (devices != null) {
			return String.valueOf(devices.size());
		}
		return "";
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("alerts")
	public String getAlerts() {
		String temp = null;// new String[Main.alerts.size()];
		int i=0;
		//System.out.println("Number of alerts currently present : "+Main.alerts.size());
		if (Update.all_alerts != null && Update.all_alerts.size() != 0) {
			//System.out.println("Total ALerts"+Update.alerts.size());
			for(Alert alert:Update.all_alerts) {
				if(temp==null)
					temp = alert.getTimeStamp().toString()+delimiter+((alert.getAssociatedDeviceType()==1)?"Host":"Network")+delimiter+alert.getAssociatedDeviceID()+delimiter+alert.getAlertDescription();
				else
					temp +="\n"+ alert.getTimeStamp().toString()+delimiter+((alert.getAssociatedDeviceType()==1)?"Host":"Network")+delimiter+alert.getAssociatedDeviceID()+delimiter+alert.getAlertDescription();
				
				i++;
			}
			return temp;
		}
		else
			return "";
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("total_alerts_count")
	public int getAlertsCount() {
		return Update.all_alerts.size();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("hids_alerts_count")
	public long getHIDSAlertsCount() {
		return Update.AlertsCount[1];
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("nids_alerts_count")
	public long getNIDSAlertsCount() {
		return Update.AlertsCount[0];
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("cep_alerts_count")
	public long getCEPAlertsCount() {
		return Update.AlertsCount[2];
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("cep_CPU_Usage")
	public int getCEP_CPU_Usage() {
		return (int)Update.cep_device.getcpu_usage();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("cep_Mem_Usage")
	public int getCEP_Mem_Usage() {
		return (int)Update.cep_device.getmem_usage();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("cep_disk_read")
	public long getCEP_disk_read() {
		return Update.cep_device.getdisk_usage_read();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("cep_disk_write")
	public long getCEP_disk_write() {
		return Update.cep_device.getdisk_usage_write();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("uptime")
	public String upTime(@Context HttpServletRequest requestContext) {
		
		long uptime = ManagementFactory.getRuntimeMXBean().getUptime()/1000l;
		String result="";
		//System.out.println("Request from : "+requestContext.getRemoteAddr());
		if(requestContext.getRemoteAddr().equals("127.0.0.1")||requestContext.getRemoteAddr().equals("0:0:0:0:0:0:0:1")) {
			if( uptime<86400) { 
				if(uptime>3599l) { //1 hour or greater
					result+=uptime/3600l+"h ";
					uptime = uptime%3600; 
				}
				if(uptime>59l) { //1 minute or greater
					result+=uptime/60l+"m ";
					uptime = uptime%60;
				}
				result+=uptime+"s ";
			}
		}
		else	result="Naughty Naughty";
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("statistics")
	public String DeploymentStat() {
		String stat = "";
		System.out.print(Update.top_five_devices.size());
		for (int i=0;i<Update.top_five_devices.size();i++)
			stat += Update.top_five_devices.get(i).getDeviceID()+'|'+Update.top_five_devices.get(i).getAlertsList().size()+'\n';
		if (stat != "")
			stat = stat.substring(0,stat.length()-1);
		return stat;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("os")
	public String OSDistribution() {
		String os;
		os=Update.os_list.toString();
		os = os.replace('=','|');
		os = os.replace(',','\n');
		os = os.substring(1,os.length()-1);
		return os;
	}
	
	/*@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("pop")
	public void pop(@RequestParam(value="name", defaultValue="World") String test) {
		
	}*/
}
