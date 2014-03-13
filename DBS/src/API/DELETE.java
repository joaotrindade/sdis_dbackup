package API;

import java.security.NoSuchAlgorithmException;

import Main_System.Chunk;
import Main_System.Peer;

public class DELETE extends Thread{
	private Chunk newChunk = null;
	public void run(){
		String sha256_string = null;
		
		//DELETE <FileId> <CRLF>
		System.out.println("\n"+"#DELETE#######################");
		System.out.println("##CHUNK_FILEID: " + getNewChunk().getFileID());
		System.out.println("##############################");
		
		sha256_string = getNewChunk().getFileID();
		try {
			sha256_string = SHA256.hash256(sha256_string);
		} catch (NoSuchAlgorithmException e1) {
			//e1.printStackTrace();
			System.out.println("#ERROR: SHA256_STRING Problem in GETCHUNK");
		}
		
		if(Peer.file_table.containsKey(sha256_string))
		{
			Peer.file_table.remove(sha256_string);
		}
	}
	public Chunk getNewChunk() {
		return newChunk;
	}
	public void setNewChunk(Chunk newChunk) {
		this.newChunk = newChunk;
	}

}
