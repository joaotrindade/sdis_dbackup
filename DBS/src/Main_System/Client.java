package Main_System;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Client extends Thread {

	public void run() {
		String input = null;
		String message = null;
		BufferedReader Systemin=new BufferedReader(new InputStreamReader(System.in)); 
		DatagramSocket message_socket = null;
		
		
		try {
			message_socket = new DatagramSocket(Peer.MC_PORT);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true)
		{
			
			System.out.println("P/G/D/R ?");
			try {
				input = Systemin.readLine();
				if (input.equals("P"))
				{
					message = "PUTCHUNK " + "1.3 " + "RUIZINHO " + "2 " + "1 " + '\r' + '\n' + "BALELE"; 
					
					DatagramPacket message_packet = new DatagramPacket(message.getBytes(),message.length(),Peer.MC_IP,Peer.MC_PORT);
					
					message_socket.send(message_packet);
				}
				else if (input.equals("G"))
				{
					message = "GETCHUNK " + "1.3 " + "RUIZINHO " + "2 " + '\r' + '\n';
					
					DatagramPacket message_packet = new DatagramPacket(message.getBytes(),message.length(),Peer.MC_IP,Peer.MC_PORT);
					
					message_socket.send(message_packet);
				}
				System.out.println(input);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
}
