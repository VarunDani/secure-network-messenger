package model;

import java.io.Serializable;

public class MessegeSendBean implements Serializable{

	/**
	 * Serial version UID for serialize Object
	 */
	private static final long serialVersionUID = 1L;

	
	private byte[] key;
	private byte[] AESData;
	private byte[] IV;
	private byte[] mac;
	
	private byte[] ticket;
	private byte[] ticketIV;
	private String fromUser ;
	
	
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

	public byte[] getTicket() {
		return ticket;
	}

	public void setTicket(byte[] ticket) {
		this.ticket = ticket;
	}

	public byte[] getTicketIV() {
		return ticketIV;
	}

	public void setTicketIV(byte[] ticketIV) {
		this.ticketIV = ticketIV;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public byte[] getMac() {
		return mac;
	}

	public void setMac(byte[] mac) {
		this.mac = mac;
	}
	
	
	
}
