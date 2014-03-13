package Main_System;

import java.util.Hashtable;

public class FileBackup {
	private String fileID;
	private Hashtable<Float, Version> versions = new Hashtable<Float, Version>();
	
	public FileBackup()
	{
		
	}
	
	public String getFileID() {
		return fileID;
	}
	public void setFileID(String _fileID) {
		this.fileID = _fileID;
	}
	
	public Hashtable<Float, Version> getVersions() {
		return this.versions;
	}
	
	public void setVersions(Hashtable<Float, Version> _versions) {
		this.versions = _versions;
	}

}
