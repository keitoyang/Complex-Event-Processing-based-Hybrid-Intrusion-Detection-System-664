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

import ids.Alert;
//import ids.Main;
import ids.deployment.Device;
import ids.sbm.Dispatch;

/**
 * 
 * @author Ranjan Mohan
 * @author Kun Yang
 * @version 1.1
 */
public class Update extends Thread {
	private ObjectOutput output;
	private ObjectInput input;
	private ServerSocket serversocket;
	private Socket clientsocket;
	private Device incoming;
	private byte[] file;
	// private JSONSerializer input_json;
	int PortNumber;
	boolean type;
	String IP;
	public static boolean fresh = false;

	public Update(int PortNumber, boolean type, String IP) throws Exception {
		this.PortNumber = PortNumber;
		this.type = type;
		this.IP = IP;
	}

	public void run() {
		try {
			
			while(true){
				Thread.sleep(5000);
				if(fresh) {
					startListener();
					sendUpdate(Dispatch.d);	
					clientsocket.close();
					fresh = false;
					
				}
			}
			
		} catch (Exception e) {
			System.err.println(e);
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
		for(Alert a:d.alert_list) {
			System.out.println("<"+a.getAlertDescription()+">");
		}
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
			/*serversocket = new ServerSocket(PortNumber);
			System.out.println("Server Socket Initialized..");
			Socket temp1;
			InputStream is;
			while (true) {
				temp1 = serversocket.accept();
				is = temp1.getInputStream();

				// file = new byte
				BufferedOutputStream temp = new BufferedOutputStream(new FileOutputStream(new File("Update1.ser")));
				System.out.println(is);
				while (!(is.available() > 0))
					;
				file = new byte[is.available()];
				System.out.println(is.available());
				is.read(file);
				temp.write(file);
				temp.close();
				is.close();
				System.out.println("Update Recieved");
				Main.addDevice(deserializeDevice());
			}*/
		} else {// client & sender
			clientsocket = new Socket(IP, PortNumber);
			System.out.println("Client Socket Initialized..");
		}
		return 0;
	}

}