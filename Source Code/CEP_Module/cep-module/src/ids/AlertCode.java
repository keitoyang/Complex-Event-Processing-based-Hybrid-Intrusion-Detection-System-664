package ids;
import java.io.Serializable;
import java.util.*;
public final class AlertCode implements Serializable {
	static final int AlertsCount = 1000;
	static String [] AlertsList;
	public static void initializeAlertsList() {
		AlertsList = new String[AlertsCount];
		AlertsList[0] = "Host Timed Out";
		AlertsList[1] = "High RAM Usage";
		AlertsList[2] = "High CPU Usage";
		AlertsList[3] = "High Disk Usage";
		AlertsList[4] = "High Network Usage";
		AlertsList[5] = "Port Scan";
		AlertsList[6] = "SYN Flood";
		AlertsList[7] = "Land Attack";
		AlertsList[8] = "User Firewall Disabled";
		AlertsList[9] = "Domain Firewall Disabled";
								
		AlertsList[100] = "Gateway Timed Out";
		AlertsList[101] = "Excessive Traffic in Gateway";
		AlertsList[105] = "Port Scan";
		AlertsList[106] = "SYN Flood";
		AlertsList[107] = "Land Attack";
		
		AlertsList[200] = "Abnormal Behaviour - MCA";
		AlertsList[201] = "High Communication Domain";
		AlertsList[202] = "IP Spoofing";
	}
	
	public static String getAlertDescription(int AlertCode) {
		if(AlertsList!=null)
			return AlertsList[AlertCode];
		else 
			return null;
	}
}
