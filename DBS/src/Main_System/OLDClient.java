package Main_System;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.*;


public class OLDClient {
	
	static int service_port;
	static String service_ip = null;
	
	public static void main(String[] args) throws IOException {
		
		//172.30.33.209
		//239.255.254.253
		
		BufferedReader Systemin=new BufferedReader(new InputStreamReader(System.in)); 
		
		int mcast_port;
		InetAddress address = null;
		InetAddress service_address = null;
		DatagramSocket socket = null;
		MulticastSocket mcast_socket = null;
		byte[] recBuf = new byte[1024];
		String sendBufString = new String("SENDING STRING");
	
		
		String type;
		String PlateNumber = null;
		String OwnerNumber = null;
		
		/*System.out.println("Register OR Lookup?");
		type = Systemin.readLine();
		
		if(type.equalsIgnoreCase("register"))
		{
			System.out.println("Plate Number?");
			PlateNumber = Systemin.readLine();
			
			System.out.println("Owner Number?");
			OwnerNumber = Systemin.readLine();
		}
		else if (type.equalsIgnoreCase("lookup"))
		{
			System.out.println("Plate Number?");
			PlateNumber = Systemin.readLine();
		}
		else
		{
			System.out.println("Invalid Command");
			System.exit(1);
		}
		*/
		
		// DEFINI��O DE ENDERE�O :::::::::::::::::::::::::::::::::::::::::::::
		
		try {
			address = InetAddress.getByName("239.255.254.253");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		// CRIA��O DE SOCKETS ::::::::::::::::::::::::::::::::::::::::::::::::
		
		mcast_port = 6790;
		try {
			mcast_socket = new MulticastSocket(mcast_port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
			
		
		while(true)
		{
			// JUNTAR AO GRUPO MULTICAST ::::::::::::::::::::::::::::::::::::::::::::::::::
			
			mcast_socket.joinGroup(address);
			mcast_socket.setTimeToLive(1); //TTL A 1,SENAO O CICA FICA NERVOSO
			
			// Receber um pacote ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			
			DatagramPacket Recieve_packet = new DatagramPacket(recBuf,recBuf.length);
			
			try {
				mcast_socket.receive(Recieve_packet);
				mcast_socket.leaveGroup(address);
				String received_string = new String (Recieve_packet.getData(),0,Recieve_packet.getLength());
			
				System.out.println(received_string);
				
				parseRequest(received_string);
				
				System.out.println("SERVICE IP: "+service_ip);
				System.out.println("SERVICE PORT: "+service_port);
			
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Problems Reading From Multicast");
			}
			
			try {
				socket = new DatagramSocket();
				socket.setSoTimeout(10000);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		
			// Enviar um pacote :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			
			/*if(type.equalsIgnoreCase("register"))
			{
				sendBufString="REGISTER "+ PlateNumber + " " + OwnerNumber;
			}
			else if (type.equalsIgnoreCase("lookup"))
			{
				sendBufString="LOOKUP "+ PlateNumber;
			}
			*/
			
			sendBufString = Systemin.readLine();
			//sendBufString = "PUTCHUNK " + "1.3 " + "RUIZINHO " + "2 " + "1 " + '\r' + '\n' + "RANHO";
			//sendBufString = "GETCHUNK " + "1.3 " + "RUIZINHO " + "2 " + '\r' + '\n';
			//sendBufString = "DELETE " + "Teste_De_Chunk " + '\r' + '\n';
			
			try {
				service_address = InetAddress.getByName(service_ip);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			DatagramPacket Sender_packet = new DatagramPacket(sendBufString.getBytes(),sendBufString.length(),service_address,service_port);
			
			try {
				socket.send(Sender_packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Sent : "+sendBufString);
			
			// Receber um pacote ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			
			DatagramPacket Recieve_packet_server = new DatagramPacket(recBuf,recBuf.length);
			
			try {
				socket.receive(Recieve_packet_server);
				
				String received_string = new String (Recieve_packet_server.getData(),0,Recieve_packet_server.getLength());
			
				System.out.println("RECIEVED: "+received_string);
	
				break;
			
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Send Again!");
			}
		}
		
	
		
		

	}
	
	private static int parseRequest(String receivedString) {
		String delims = "[ ]+";
		String[] tokens = receivedString.split(delims);
		
		/*for (int i = 0; i < tokens.length; i++)
		    System.out.println(tokens[i]);*/
		
		service_port = Integer.valueOf(tokens[4].substring(0, tokens[2].length() - 1));
		service_ip = tokens[3];
		
		return 1;
	}
	

}
