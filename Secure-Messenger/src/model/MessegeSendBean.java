package model;

import java.io.Serializable;

public class MessegeSendBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private byte[] key;
	private byte[] AESData;
	private byte[] IV;
	
	
	public MessegeSendBean()
	{
		
	}
	
	public MessegeSendBean(byte[] key, byte[] aESData, byte[] iV) {
		super();
		this.key = key;
		AESData = aESData;
		IV = iV;
	}
	public byte[] getAESData() {
		return AESData;
	}
	public void setAESData(byte[] aESData) {
		AESData = aESData;
	}
	public byte[] getIV() {
		return IV;
	}
	public void setIV(byte[] iV) {
		IV = iV;
	}
	public byte[] getKey() {
		return key;
	}
	public void setKey(byte[] key) {
		this.key = key;
	}
	
	
	
}
