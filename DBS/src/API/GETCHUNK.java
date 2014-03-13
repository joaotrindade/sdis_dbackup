package API;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import Main_System.Chunk;
import Main_System.FileBackup;
import Main_System.Peer;
import Main_System.Version;


public class GETCHUNK extends Thread{
	
	public void run(Chunk newChunk, InetAddress ipSender, int portSender){
		// VARIAVEIS DE ARMAZENAMENTO
		String sendBuffer = null;
		String sha256_string = null;
		int milisecond_wait;
		Random random = new Random();
		
		// VARIAVEIS CORRESPONDENTES À CONEXÃO
		DatagramPacket responsePacket = null;
		
		//GETCHUNK <Version> <FileId> <ChunkNo> <CRLF> <CRLF>
		
		System.out.println("\n"+"#GET##########################");
		System.out.println("##CHUNK_VERSION: " + newChunk.getVersion());
		System.out.println("##CHUNK_FILEID: " + newChunk.getFileID());
		System.out.println("##CHUNK_NUMBER: " + newChunk.getChunkNumber());
		System.out.println("##############################");
		
		// ENVIAR RESPOSTA
		// CHUNK <Version> <FileId> <ChunkNo> <CRLF> <CRLF> <Body>
		
		sha256_string = newChunk.getFileID();
		try {
			sha256_string = SHA256.hash256(sha256_string);
		} catch (NoSuchAlgorithmException e1) {
			//e1.printStackTrace();
			System.out.println("#ERROR: SHA256_STRING Problem in GETCHUNK");
		}

		
		if(Peer.file_table.containsKey(sha256_string))
		{
			FileBackup ficheiro = Peer.file_table.get(sha256_string);
			if(ficheiro.getVersions().containsKey(newChunk.getVersion()))
			{
				Version versao = ficheiro.getVersions().get(newChunk.getVersion());
				if(versao.getChunks().containsKey(newChunk.getChunkNumber()))
				{
					Chunk found_chunk = versao.getChunks().get(newChunk.getChunkNumber());
					
					System.out.println("\n"+"#FOUND#######################");
					System.out.println("##CHUNK_VERSION: " + found_chunk.getVersion());
					System.out.println("##CHUNK_FILEID: " + found_chunk.getFileID());
					System.out.println("##CHUNK_NUMBER: " + found_chunk.getChunkNumber());
					System.out.println("##CHUNK_REPDEG: " + found_chunk.getRepDeg());
					System.out.println("##CHUNK_CONTENT: " + found_chunk.getContentString());
					System.out.println("##############################");
					
					sendBuffer = "CHUNK " + found_chunk.getVersion() + " " + found_chunk.getFileID() + " " + found_chunk.getChunkNumber() +'\r' + '\n' + found_chunk.getContentString();
					
					responsePacket = new DatagramPacket(sendBuffer.getBytes(),sendBuffer.getBytes().length,ipSender,portSender);
					
					//DELAY DE UM NUMERO ALEATORIO DE 0 A 400 MS 
					milisecond_wait = random.nextInt(401);
					
					try {
						Thread.sleep(milisecond_wait);
					} catch (InterruptedException e) {
						//e.printStackTrace();
						System.out.println("#ERROR: Problems waiting milisecond 0~400 before send");
					}
					
					try {
						Peer.socket.send(responsePacket);
						System.out.println("#SENT: "+sendBuffer +"\n" + "##############################");
					} catch (IOException e) {
						//e.printStackTrace();
						System.out.println("#ERROR: Problems sending packet in GETCHUNK");
					}
				}
			}
		}
		
		
		
		
		
	}

}
