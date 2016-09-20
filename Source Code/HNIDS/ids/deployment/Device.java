package ids.deployment;

/**
 * 
 * @author Ranjan Mohan
 * @author Kun Yang
 * @version 1.5
 */
import java.util.LinkedList;
import java.util.List;

import ids.Alert;

import java.io.Serializable;
public class Device implements Serializable{

	private int TypeofDevice; //Network 0; Host 1;
	private String DeviceID;
	public String os_name;												//Returns Operating System Name (Windows/Linux/MAC/Other)
	public String os_version;											//Returns Operating System Version (E.g.: 6.1 means Win 7)	
    public List<Alert> alert_list;
	public double disk_read_diff;
	public double disk_write_diff;
	public long disk_usage_read;										//No. of Bytes being read from the disk
	public long disk_usage_write;										//No. of Bytes being written to the disk
	
	public Device(int TypeofDevice, String DeviceID) {
		this.TypeofDevice = TypeofDevice;
		this.DeviceID = DeviceID;
		alert_list = new LinkedList<Alert>();
		
	}

        /**
         * Returns the type of Device object
         * @return Type of device object
         */
	public int getTypeofDevice (){
		return TypeofDevice;
	}
	
	/**
     * Returns Device object
     * @return Device ID
     */ 
	public String getDeviceID() {
		return DeviceID;
    }
	
	/**
     * Returns the list of alerts
     * @return Alert List
     */
	public List<Alert> getAlertsList(){
		return alert_list;
	}
	
	/**
     * Returns the Operating System
     * @return the Operating System Name
     */
	public String getos_name(){
		return os_name;
	}

	/**
     * Returns the version of the Operating System
     * @return the version of the Operating System
     */
	public String getos_version(){
		return os_version;
	}
    
	/**
     * Returns the disk read speed in bytes per second
     * @return the disk read speed in bytes per second
     */
	public long getdisk_usage_read(){
		return disk_usage_read;
	}
	
	/**
     * Returns the disk write speed in bytes per second
     * @return the disk write speed in bytes per second
     */
	public long getdisk_usage_write(){
		return disk_usage_write;
	}
	
	public double getdisk_diff_read(){
		return disk_read_diff;
	}

	public double getdisk_diff_write(){
		return disk_write_diff;
	}
}
