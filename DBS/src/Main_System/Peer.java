package Main_System;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;

import API.DELETE;
import API.GETCHUNK;
import API.PUTCHUNK;


public class Peer {
	
	// VARIAVEIS GLOBAIS
	public static ArrayList<Chunk> database = new ArrayList<Chunk>();
	public static DatagramSocket socket = null;
	public static Hashtable<String,FileBackup> file_table= new Hashtable<String,FileBackup>(); 
	
	public static void main(String[] args) {
		
		// VARIAVEIS CORRESPONDENTES AO ARMAZENAMENTO
		String receivedString = null;
		String operation = null;
		byte[] buffer = new byte[64001];
		
		// VARIAVEIS CORRESPONDENTES À CONEXÃO
		DatagramPacket receivingPacket = null;
		InetAddress ipSender = null;
		int portSender;
		
		// VARIAVEIS DA API
		MulticastConnection MCAST = new MulticastConnection();
		PUTCHUNK PUTC = new PUTCHUNK();
		GETCHUNK GETC = new GETCHUNK();
		DELETE DELF = new DELETE();
		
		
		// INICIAR CONEXÃO MULTICAST 
		MCAST.start();
		
		System.out.println("### DISTRIBUTED BACKUP SERVICE ###");
		
		try {
			socket = new DatagramSocket(36319);
			
			while(true)
			{
					receivingPacket = new DatagramPacket(buffer, buffer.length);
					
				try {
						socket.receive(receivingPacket);
						
						receivedString = new String(receivingPacket.getData(),0,receivingPacket.getLength());
						System.out.println("\n"+"#REQUEST: "+ receivedString);
						
						Chunk newChunk = new Chunk();
						operation = parseRequest(receivedString,newChunk);
						
						if(operation.equalsIgnoreCase("PUTCHUNK"))
						{
							ipSender = receivingPacket.getAddress();
							portSender = receivingPacket.getPort();
							
							PUTC.run(newChunk,ipSender,portSender);
						}
						else if(operation.equalsIgnoreCase("GETCHUNK"))
						{
							ipSender = receivingPacket.getAddress();
							portSender = receivingPacket.getPort();
							
							GETC.run(newChunk,ipSender,portSender);
						}
						else if(operation.equalsIgnoreCase("DELETE"))
						{
							DELF.run(newChunk);
						}
						
						
						operation = null;
						newChunk = null;
						
						

				} catch (IOException e) {
					//e.printStackTrace();
					socket.close();
					System.out.println("#ERROR: Receiving Packet From Socket");
					
				}
				
			}
			
		} catch (SocketException e) {
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

}
