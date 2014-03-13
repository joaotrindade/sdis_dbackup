package Main_System;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import API.DELETE;
import API.GETCHUNK;
import API.PUTCHUNK;


public class Peer {
	
	// VARIAVEIS GLOBAIS
	public static ArrayList<Chunk> database = new ArrayList<Chunk>();
	public static ArrayList<String> interfaces_ips = new ArrayList<String>();
	public static MulticastSocket MC_socket = null, MDB_socket = null, MDR_socket = null;
	public static Hashtable<String,FileBackup> file_table= new Hashtable<String,FileBackup>(); 
	public static InetAddress MC_IP, MDB_IP, MDR_IP;
	public static int MC_PORT, MDB_PORT, MDR_PORT;
	public static void main(String[] args) {
		

		// VARIAVEIS CORRESPONDENTES AO ARMAZENAMENTO
		String receivedString = null;
		String operation = null;
		byte[] buffer = new byte[64001];
		
		// VARIAVEIS CORRESPONDENTES � CONEX�O
		DatagramPacket receivingPacket = null;
		//InetAddress ipSender = null;
		//int portSender;
		
		// VARIAVEIS DA API
		Client CLIENT = new Client();
		PUTCHUNK PUTC = new PUTCHUNK();
		GETCHUNK GETC = new GETCHUNK();
		DELETE DELF = new DELETE();
		
		
		// OBTER ENDEREÇOS INTERFACES
		try {
			getNetworkInterfacesAdresses();
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		
		// INICIAR CONEX�O MULTICAST 
		CLIENT.start();
	
	
	
			//ESTRUTURA ARG MC_IP MC_PORT MDB_IP MDB_PORT MDR_IP MDR PORT
		try {
			MC_IP = InetAddress.getByName(args[0]);
			MC_PORT = Integer.valueOf(args[1]);
			 
			MDB_IP = InetAddress.getByName(args[2]);
			MDB_PORT = Integer.valueOf(args[3]);
			
			MDR_IP = InetAddress.getByName(args[4]);
			MDR_PORT = Integer.valueOf(args[5]);
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		System.out.println("### DISTRIBUTED BACKUP SERVICE ###");
		
		try {
			
			MC_socket = new MulticastSocket(MC_PORT);
			MC_socket.joinGroup(MC_IP);
			MDB_socket = new MulticastSocket(MDB_PORT);
			MDB_socket.joinGroup(MDB_IP);
			MDR_socket = new MulticastSocket(MDR_PORT);
			MDR_socket.joinGroup(MDR_IP);
			
			
			while(true)
			{
				receivingPacket = new DatagramPacket(buffer, buffer.length);
					
				try {
						MC_socket.receive(receivingPacket);
						
						if(!interfaces_ips.contains(receivingPacket.getAddress().getHostAddress()))
						{
							receivedString = new String(receivingPacket.getData(),0,receivingPacket.getLength());
							System.out.println("\n"+"#REQUEST: "+ receivedString);
							
							Chunk newChunk = new Chunk();
							operation = parseRequest(receivedString,newChunk);
							
							if(operation.equalsIgnoreCase("PUTCHUNK"))
							{	
								PUTC.setNewChunk(newChunk);
								PUTC.start();
							}
							else if(operation.equalsIgnoreCase("GETCHUNK"))
							{
								//ipSender = receivingPacket.getAddress();
								//portSender = receivingPacket.getPort();
								GETC.setNewChunk(newChunk);
								GETC.start();
							}
							else if(operation.equalsIgnoreCase("DELETE"))
							{
								DELF.setNewChunk(newChunk);
								DELF.start();
							}
							
							
							operation = null;
							newChunk = null;
						}
						
						

				} catch (IOException e) {
					//e.printStackTrace();
					MC_socket.close();
					System.out.println("#ERROR: Receiving Packet From Socket");
					
				}
				
			}
			
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("#ERROR: Creating Socket");
		}

	}
	
	
	
	private static String parseRequest(String receivedString,Chunk newChunk) {
		String delims = "[ \r\n]+";
		String[] tokens = receivedString.split(delims);
		String operation = null;
		
		/*for (int i = 0; i < tokens.length; i++)
		    System.out.println(tokens[i]);*/
		
		operation = tokens[0];
		
		if (operation.equalsIgnoreCase("PUTCHUNK"))
		{
			//PUTCHUNK <Version> <FileId> <ChunkNo> <ReplicationDeg> <CRLF> <CRLF> <Body>
			//Chunk(float _version,String _fileID, long _chunkNumber, int _repDeg, byte[] _content)
			
			newChunk.setVersion(Float.parseFloat(tokens[1]));
			newChunk.setFileID(tokens[2]);
			newChunk.setChunkNumber(Long.parseLong(tokens[3]));
			newChunk.setRepDeg(Integer.parseInt(tokens[4]));
			newChunk.setContent(tokens[5].getBytes());
		}
		else if(operation.equalsIgnoreCase("GETCHUNK"))
		{
			//GETCHUNK <Version> <FileId> <ChunkNo> <CRLF> <CRLF>
			newChunk.setVersion(Float.parseFloat(tokens[1]));
			newChunk.setFileID(tokens[2]);
			newChunk.setChunkNumber(Long.parseLong(tokens[3]));
		}
		else if(operation.equalsIgnoreCase("DELETE"))
		{
			//DELETE <FileId> <CRLF>
			newChunk.setFileID(tokens[1]);
		}
				
		return operation;
	}
	
	private static void getNetworkInterfacesAdresses() throws SocketException{
		Enumeration<NetworkInterface> e=NetworkInterface.getNetworkInterfaces();
	    while(e.hasMoreElements())
	    {
	        NetworkInterface n=(NetworkInterface) e.nextElement();
	        Enumeration<InetAddress> ee = n.getInetAddresses();
	        while(ee.hasMoreElements())
	        {
	            InetAddress i= (InetAddress) ee.nextElement();
	            interfaces_ips.add(i.getHostAddress());
	        }
	    }
	}

}
