package ids;

import ids.communication.Update;
import ids.deployment.Host;

public class ReceiveUpdateTest {
	public static void main(String argu[])throws Exception{
		Update u = new Update (21212, true, "127.0.0.1");
		
		u.start();
		
		
	}
	
}
