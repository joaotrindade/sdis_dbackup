package Main_System;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class MulticastConnection extends Thread {

	public void run() {
		MulticastSocket mcast_socket = null;
		int mcast_port = 6790;
		InetAddress ip_address = null;
		String sendText = new String("multicast: potatoes 11118: 192.168.32.194 36319");
		
		
		try {
				ip_address = InetAddress.getByName("239.255.254.252");
				
				mcast_socket = new MulticastSocket(mcast_port);
				mcast_socket.setTimeToLive(1);
				
				DatagramPacket mcast_packet = new DatagramPacket(sendText.getBytes(),sendText.length(),ip_address,mcast_port);
				
				System.out.println("\n"+"#CREATED MULTICAST BRODCAST");
				
				while(true)
				{
						mcast_socket.send(mcast_packet);
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							System.out.println("Sleep Error");
						}
				}
		
			} catch (UnknownHostException e1) {
				System.out.println("IP ERROR");
				
			} catch (IOException e) {
				System.out.println("IO EXCEPTION");
		}
			
			
			
		}
		
	
	public static void main(String[] args) {
		
		(new MulticastConnection()).start();
	}
}
