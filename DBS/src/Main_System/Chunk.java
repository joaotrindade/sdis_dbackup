package Main_System;
import java.io.UnsupportedEncodingException;


public class Chunk {
	private String fileID;
	private float version;
	private long chunkNumber;
	private byte[] content;
	private int replicationDeg;
	
	public Chunk()
	{
		this.fileID = "NULL";
		this.chunkNumber = -1;
		this.content = "NULL".getBytes();
		this.version = -1;
		this.replicationDeg = -1;
	}

	public Chunk(float _version,String _fileID, long _chunkNumber, int _repDeg, byte[] _content)
	{
		this.fileID = _fileID;
		this.chunkNumber = _chunkNumber;
		this.content = _content;
		this.version = _version;
		this.replicationDeg = _repDeg;
	}
	
	public String getFileID() {
		return this.fileID;
	}
	
	public long getChunkNumber() {
		return this.chunkNumber;
	}
	
	public byte[] getContent() {
		return this.content;
	}
	
	public String getContentString() {
		
		try {
			return new String(this.content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			System.out.println("#ERROR: Problems converting content to String");
			return "";
		}
	}
	
	public float getVersion() {
		return this.version;
	}
	
	public int getRepDeg() {
		return this.replicationDeg;
	}
	
	public void setChunkNumber(long _chunkNumber) {
		this.chunkNumber = _chunkNumber;
	}
	
	public void setFileID(String _fileID) {
		this.fileID = _fileID;
	}
	
	public void setContent(byte[] _content) {
		this.content = _content;
	}
	
	public void setVersion(float _version) {
		this.version = _version;
	}
	
	public void setRepDeg(int _repDeg) {
		this.replicationDeg = _repDeg;
	}
}
