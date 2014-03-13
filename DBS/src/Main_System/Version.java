package Main_System;

import java.util.Hashtable;

public class Version {
	private float version;
	private Hashtable<Long, Chunk> chunks = new Hashtable<Long, Chunk>();

	public float getVersion() {
		return version;
	}

	public void setVersion(float version) {
		this.version = version;
	}

	public Hashtable<Long, Chunk> getChunks() {
		return chunks;
	}

	public void setChunks(Hashtable<Long, Chunk> chunks) {
		this.chunks = chunks;
	}
	
}
