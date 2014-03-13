package API;
import java.io.IOException;
import java.net.DatagramPacket;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Random;

import Main_System.Chunk;
import Main_System.FileBackup;
import Main_System.Peer;
import Main_System.Version;


public class PUTCHUNK extends Thread {
	private Chunk newChunk = null;
	public void run() {
		// VARIAVEIS DE ARMAZENAMENTO
		String sendBuffer = null;
		String sha256_string = null;
		int milisecond_wait;
		Random random = new Random();
		
		// VARIAVEIS CORRESPONDENTES � CONEX�O
		DatagramPacket responsePacket = null;
		
		sha256_string = newChunk.getFileID();
		try {
			sha256_string = SHA256.hash256(sha256_string);
		} catch (NoSuchAlgorithmException e1) {
			//e1.printStackTrace();
			System.out.println("#ERROR: SHA256_STRING Problem in PUTCHUNK");
		}

		
		if(!Peer.file_table.containsKey(sha256_string))
		{
			// CRIA UM FICHEIRO
			FileBackup newFile = new FileBackup();
			newFile.setFileID(sha256_string);

			// CRIA UMA TABELA DE HASH DE VERS�ES, CRIA UMA VERS�O
			Hashtable<Float, Version> versions = new Hashtable<Float, Version>();
			Version newVersion = new Version();
			newVersion.setVersion(newChunk.getVersion());
			
			// CRIA UMA TABELA DE HASH, ADICIONA O CHUNK E COLOCA ESSA TABELA NA VERS�O.
			Hashtable<Long, Chunk> chunks = new Hashtable<Long, Chunk>();
			chunks.put(newChunk.getChunkNumber(), newChunk);
			newVersion.setChunks(chunks);
			
			// ADICIONA A VERS�O � TABELA HASH DE VERSOES E ADICIONA A TABELA AO FILE.
			versions.put(newChunk.getVersion(), newVersion);
			newFile.setVersions(versions);
			
			// ADICIONA O NOVO FICHEIRO � NOSSA BIBLIOTECA.
			Peer.file_table.put(sha256_string, newFile);
		}
		else
		{
			FileBackup ficheiro = Peer.file_table.get(sha256_string);
			if(!ficheiro.getVersions().containsKey(newChunk.getVersion()))
			{
				Version newVersion = new Version();
				newVersion.setVersion(newChunk.getVersion());
				
				// CRIA UMA TABELA DE HASH, ADICIONA O CHUNK E COLOCA ESSA TABELA NA VERS�O.
				Hashtable<Long, Chunk> chunks = new Hashtable<Long, Chunk>();
				chunks.put(newChunk.getChunkNumber(), newChunk);
				newVersion.setChunks(chunks);
				
				// ADICIONA A VERS�O � TABELA HASH DE VERSOES DO FILE.
				ficheiro.getVersions().put(newChunk.getVersion(), newVersion);
			}
			else
			{
				Version versao = ficheiro.getVersions().get(newChunk.getVersion());
				if(!versao.getChunks().containsKey(newChunk.getChunkNumber()))
				{
					versao.getChunks().put(newChunk.getChunkNumber(), newChunk);
				}
				else
				{
					versao.getChunks().remove(newChunk.getChunkNumber());
					versao.getChunks().put(newChunk.getChunkNumber(), newChunk);
				}
				
			}
		}
		
		//Peer.database.add(newChunk);
		
		System.out.println("\n"+"#STORED#######################");
		System.out.println("##CHUNK_VERSION: " + newChunk.getVersion());
		System.out.println("##CHUNK_FILEID: " + newChunk.getFileID());
		System.out.println("##CHUNK_NUMBER: " + newChunk.getChunkNumber());
		System.out.println("##CHUNK_REPDEG: " + newChunk.getRepDeg());
		System.out.println("##CHUNK_CONTENT: " + newChunk.getContentString());
		System.out.println("##############################");
		
		// ENVIAR RESPOSTA
		// STORED <Version> <FileId> <ChunkNo> <CRLF> <CRLF>
		
		sendBuffer = "STORED " + newChunk.getVersion() + " " + newChunk.getFileID() + " " + newChunk.getChunkNumber() +'\r' + '\n';
		
		responsePacket = new DatagramPacket(sendBuffer.getBytes(),sendBuffer.getBytes().length,Peer.MC_IP,Peer.MC_PORT);
		
		//DELAY DE UM NUMERO ALEATORIO DE 0 A 400 MS 
		milisecond_wait = random.nextInt(401);
		
		try {
			Thread.sleep(milisecond_wait);
		} catch (InterruptedException e) {
			//e.printStackTrace();
			System.out.println("#ERROR: Problems waiting milisecond 0~400 before send");
		}
		
		try {
			Peer.MC_socket.send(responsePacket);
			System.out.println("#SENT: "+sendBuffer);
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("#ERROR: Problems sending packet in PUTCHUNK");
		}
		
		
	}
	public Chunk getNewChunk() {
		return newChunk;
	}
	public void setNewChunk(Chunk newChunk) {
		this.newChunk = newChunk;
	}
}
