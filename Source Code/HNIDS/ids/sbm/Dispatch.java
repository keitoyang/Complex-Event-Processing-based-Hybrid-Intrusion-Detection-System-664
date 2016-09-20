package ids.sbm;
import ids.communication.Update;
import ids.deployment.Device;
import ids.deployment.Host;
import ids.deployment.Network;
import ids.feature_Extraction.Extraction;
import ids.feature_Extraction.Packet;

/* Dispatch runs Packet and Sbm threads */
/**
 * 
 * @author ahuber
 * @version 1.0
 *
 */


public class Dispatch {
	
	public static Network d = new Network("Aaron"); //asd123
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Packet pkt = new Packet();								//thread 1
		Sbm detect = new Sbm(d);									//thread 2
		Extraction extract = new Extraction(d);					//thread 3
		extract.start();
		/*try {
			Update update = new Update(5000,false,"10.84.8.233");//192.168.43.57");
			update.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			*/										//thread 4
	
	}

}
