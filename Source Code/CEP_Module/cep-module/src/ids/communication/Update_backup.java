package ids.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ids.Alert;
import ids.Main;
import ids.deployment.Device;
import ids.deployment.Network;
import ids.deployment.Host;
import ids.MCA;

/**
 * 
 * @author Ranjan Mohan
 * @author Kun Yang
 * @version 1.1
 */
public class Update_backup extends Thread {
	private ObjectOutput output;
	private ObjectInput input;
	private ServerSocket serversocket;
	private Socket clientsocket;
	private Device incoming;
	private byte[] file;
	
	public static List<Device> devices;
	public static List<Device> top_five_devices;
	public static long AlertsCount[];
	public static List<Alert> all_alerts;
	public static HashMap<String,Integer> os_list;
	public static HashMap<String,MCA> mca;
	public static Host cep_device;
	/*
	 *1st Level - NW, Host
	 *2nd Level - Device ID
	 *3rd Level - Alerts 
	 */
	public static List<HashMap<String,List<Alert>>> alerts;
	int PortNumber;
	boolean type;
	String IP;

	public Update_backup(int PortNumber, boolean type, String IP) throws Exception {
		this.PortNumber = PortNumber;
		this.type = type;
		this.IP = IP;
		devices = new ArrayList<Device>();
		top_five_devices = new ArrayList<Device>();
		os_list = new HashMap<String,Integer>();
		cep_device = new Host("CEP");
		AlertsCount = new long [3];// 2 - CEP, 1 - Host based, 0 - NW based
		all_alerts = new LinkedList<Alert>();
		alerts = new LinkedList<HashMap<String,List<Alert>>>();
		alerts.add(new HashMap<String,List<Alert>>()); //Network
		alerts.add(new HashMap<String,List<Alert>>()); //Host
		cep_device.cpu_usage = 0;
		cep_device.mem_usage = 0;
		cep_device.disk_usage_read = 0;
		cep_device.disk_usage_write = 0;
		mca = new HashMap<String,MCA>();
	}

	public void run() {
		try {
			startListener();
		} catch (Exception e) {
			e.printStackTrace();
			//System.err.println(e);
		}
	}

	/**
	 * Serializes the specified subclass instance of Device class
	 * 
	 * @param Device
	 *            object
	 * @return Exit STatus
	 */
	public int serializeDevice(Device d) throws Exception {
		output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("Update.ser")));
		output.writeObject(d);
		output.close();
		System.out.println("Device Serialized successfully..");
		// input_json = new JSONSerializer().prettyPrint(true);
		// System.out.println(input_json.deepSerialize(d));
		return 0;
	}

	/**
	 * De-serializes the data received
	 * 
	 * @return Device object
	 */
	public Device deserializeDevice() throws Exception {
		input = new ObjectInputStream(new BufferedInputStream(new FileInputStream("Update1.ser")));
		incoming = (Device) input.readObject();
		input.close();
		System.out.println("Device De-serialized successfully..");
		return incoming;
	}

	/**
	 * Serializes the running Instances and sends them to the sever
	 * 
	 * @param Device
	 *            object
	 */
	public void sendUpdate(Device d) throws Exception {

		serializeDevice(d);
		File update = new File("Update.ser");

		OutputStream os = clientsocket.getOutputStream();
		file = new byte[(int) update.length()];
		BufferedInputStream temp = new BufferedInputStream(new FileInputStream(update));
		temp.read(file, 0, file.length);
		temp.close();
		System.out.println(file.length);
		os.write(file, 0, file.length);
		os.close();
		System.out.println("Update sent successfully");
	}

	/**
	 * Starts the Incoming Update Listener on the specified Port
	 * 
	 * @param Port
	 *            number, type, and IP address
	 * @return Exit STatus
	 */
	public int startListener() throws Exception {// type 1 server, type 0 client
		if (type) {// server & receiver
			serversocket = new ServerSocket(PortNumber);
			serversocket.setReceiveBufferSize(100000000);
			System.out.println("Server Socket Initialized..");
			Socket temp1;
			InputStream is;
			while (true) {
				temp1 = serversocket.accept();
				
				temp1.setReceiveBufferSize(100000000);
				System.out.println("\nServer Socket Receive Buffer Size : "+serversocket.getReceiveBufferSize());
				System.out.println("temp1 Receive Buffer Size : "+temp1.getReceiveBufferSize());
				is = temp1.getInputStream();
				
				// file = new byte
				FileOutputStream fff = new FileOutputStream(new File("Update1.ser"));
				
				BufferedOutputStream temp = new BufferedOutputStream(fff);
				System.out.println(is);
				while (!(is.available() > 0));
				
				Thread.sleep(5000);
				file = new byte[is.available()];
				System.out.println(is.available());
				is.read(file);
				temp.write(file);
				temp.close();
				fff.close();
				is.close();
				Device dev = deserializeDevice();
				System.out.println("Update Recieved");
				if(dev.getDeviceID().equals("CEP"))
				{
					cep_device.cpu_usage = ((Host)dev).getcpu_usage();
					cep_device.mem_usage = ((Host)dev).getmem_usage();
					cep_device.disk_usage_read =((Host)dev).getdisk_usage_read();
					cep_device.disk_usage_write =((Host)dev).getdisk_usage_write();
				}
				if(!alerts.get(dev.getTypeofDevice()).containsKey(dev.getDeviceID())) {
					alerts.get(dev.getTypeofDevice()).put(dev.getDeviceID(), new LinkedList<Alert>());
				}
				addDevice(dev);

				addAlertsList(dev);
				System.out.println ("fresh update");
			}
		} else {// client & sender
			clientsocket = new Socket(IP, PortNumber);
			System.out.println("Client Socket Initialized..");
		}
		return 0;
	}

	public synchronized static void addAlertsList(Device dev) {
		List<Alert> tempp = alerts.get(dev.getTypeofDevice()).get(dev.getDeviceID());
		for(Alert inc_alert:dev.getAlertsList()) {
			for(Alert alert:tempp){
				if(alert.getAlertCode()==inc_alert.getAlertCode()) {
					tempp.remove(alert);
					all_alerts.remove(alert);
					AlertsCount[dev.getTypeofDevice()]--;
					break;
				}
			}
			tempp.add(inc_alert);
			all_alerts.add(inc_alert);
			AlertsCount[dev.getTypeofDevice()]++;
		}
		alerts.get(dev.getTypeofDevice()).put(dev.getDeviceID(), tempp);
		//return tempp;
	}
	public synchronized static void addAlert(Device dev,Alert alert) {
		all_alerts.add(alert);
		System.out.println(all_alerts.size());
		(alerts.get(dev.getTypeofDevice())).get(dev.getDeviceID()).add(alert);
		AlertsCount[dev.getTypeofDevice()]++;
	}
	public synchronized static void addDevice(Device host) {
		for (Device device : devices) {
			if (host.getDeviceID().equals(device.getDeviceID())) {
				if (os_list.get(device.getos_name())>1)
					os_list.put(device.getos_name(),os_list.get(device.getos_name())-1);
				else
					os_list.remove(device.getos_name());
				devices.remove(device);
				break;
			}
		}
		devices.add(host);
		if (os_list.containsKey(host.getos_name()))
			os_list.put(host.getos_name(),os_list.get(host.getos_name())+1);
		else
			os_list.put(host.getos_name(),1);
		int max[] = {0,0,0,0,0};
		for (int i=0; i<top_five_devices.size();i++)
		{
			if (top_five_devices.get(i).getAlertsList().size()>max[i])
				max[i]=top_five_devices.get(i).getAlertsList().size();
		}
		for (Device device : devices) {
			if (device.getAlertsList().size()>=max[0]){
				max[0] = device.getAlertsList().size();
				if (top_five_devices.size()<1)
					top_five_devices.add(device);
				else
					top_five_devices.set(0,device);
			}
		}
		for (Device device : devices) {
			if (device.getAlertsList().size()>=max[1] && !top_five_devices.contains(device)){
				max[1] = device.getAlertsList().size();
				if (top_five_devices.size()<2)
					top_five_devices.add(device);
				else
					top_five_devices.set(1,device);
			}
		}
		for (Device device : devices) {
			if (device.getAlertsList().size()>=max[2] && !top_five_devices.contains(device)){
				max[2] = device.getAlertsList().size();
				if (top_five_devices.size()<3)
					top_five_devices.add(device);
				else
					top_five_devices.set(2,device);
			}
		}
		for (Device device : devices) {
			if (device.getAlertsList().size()>=max[3] && !top_five_devices.contains(device)){
				max[3] = device.getAlertsList().size();
				if (top_five_devices.size()<4)
					top_five_devices.add(device);
				else
					top_five_devices.set(3,device);
			}
		}
		for (Device device : devices) {
			if (device.getAlertsList().size()>=max[4] && !top_five_devices.contains(device)){
				max[4] = device.getAlertsList().size();
				if (top_five_devices.size()<5)
					top_five_devices.add(device);
				else
					top_five_devices.set(4,device);
			}
		}
	}
}