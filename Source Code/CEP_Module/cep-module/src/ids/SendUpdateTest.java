package ids;
import ids.communication.Update;
import ids.deployment.Host;
import ids.deployment.Network;
public class SendUpdateTest {
	public static void main(String argu[])throws Exception{
		Update u = new Update (5000, false, "127.0.0.1");
		Host a=new Host("1CEP");
		a.cpu_usage = 99.0;
		a.mem_usage = 99.0;
		a.disk_usage_read = 10000;
		a.disk_usage_write = 20000;
		a.os_name = "win";

		u.startListener();
		u.sendUpdate(a);
	}
}