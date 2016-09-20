package ids;
import ids.deployment.Device;
import ids.deployment.Host;
import ids.deployment.Network;
import java.util.Date;
import java.io.Serializable;
public class Alert implements Serializable {
	final int alert_code;
	final String alert_description;
	final Device location;
	final Date timestamp;
	public Alert(Device location,int alert_code,Date timestamp) {
		AlertCode.initializeAlertsList();
		this.alert_code = alert_code;
		alert_description = AlertCode.getAlertDescription(alert_code);
		this.location = location;
		this.timestamp = timestamp;
	}
	public Alert(Device location,int alert_code) {
		AlertCode.initializeAlertsList();
		this.alert_code = alert_code;
		alert_description = AlertCode.getAlertDescription(alert_code);
		this.location = location;
		this.timestamp = new Date();
	}
	public int getAlertCode() {
		return alert_code; 
	}
	
	public String getAlertDescription() {
		return alert_description;
	}
	
	public Date getTimeStamp() {
		return timestamp;
	}
	
	public String getAssociatedDeviceID() {
		return location.getDeviceID();
	}
	
	public int getAssociatedDeviceType() { // 0 - NW, 1 - Host
		return location.getTypeofDevice();
	}
	
}
