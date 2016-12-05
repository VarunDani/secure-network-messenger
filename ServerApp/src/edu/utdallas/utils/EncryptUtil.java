package edu.utdallas.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;

import com.google.common.base.Splitter;


public class EncryptUtil {


	
	private static final String PUBLIC_KEY_FILE = "Public.pem";
	private static final String PRIVATE_KEY_FILE = "Private.pem";


	public static void main(String[] args) {
//		EncryptUtil rsaObj = new EncryptUtil();
			String passString="password";
			System.out.println("plaintext password: "+passString);
			try
			{
				String passSha = makeSHA512Hash(passString);
				System.out.println("SHA512 password: " +passSha);
				String username="jay";
				BigInteger nonce = new BigInteger(512, new SecureRandom());
				System.out.println("nonce: "+nonce);
				String appended = appendedd(username, nonce, passSha);
			//	String hmacr=HMAC(username, nonce, passSha);
				encryptPrivate(appended);
				
					/*
					//Decrypt Data using Private Key
					System.out.println("\n----------------DECRYPTION STARTED------------");
					String xyz= rsaObj.decryptData(encryptedData1);
					String abc= rsaObj.decryptData(encryptedData2);
					StringBuilder sb1 = new StringBuilder(14);
					sb1.append(xyz).append(abc);
					String app = sb1.toString();
					System.out.println("decrypted message: "+app);
					
				System.out.println("----------------DECRYPTION COMPLETED------------");
				*/
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
	
	
	public static String makeSHA512Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.reset();
		byte[] buffer = input.getBytes("UTF-8");
		md.update(buffer);
		byte[] digest = md.digest();
		String hexStr = "";
		for (int i = 0; i < digest.length; i++) {
			hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
			}
		return hexStr;
		}
	
	private static String appendedd(String username, BigInteger nonce, String ss1) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(1024);
		sb.append(username).append(" ").append(nonce).append(" ").append(ss1);
		//System.out.println(sb.toString());
		String appended = sb.toString();
		return appended;
		}
	
	/**
	 * Save generated Keys in File (Public and Private )
	 * 
	 * @param fileName
	 * @param mod
	 * @param exp
	 * @throws IOException
	 */
	public void saveKeys(String fileName,BigInteger mod,BigInteger exp) throws IOException{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			System.out.println("Generating "+fileName + "...");
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(mod);
			oos.writeObject(exp);
			System.out.println(fileName + " generated successfully");
			} catch (Exception e) {
				e.printStackTrace();
				}
		finally{
			if(oos != null){
				oos.close();
				if(fos != null){
					fos.close();
					}
				}
			}
		}
	
	
	
	/*public static String sha512 (String pass) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(pass.getBytes());
		byte digest[] = md.digest();
		//convert the byte to hex format
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			}
		String Kc = sb.toString();
		//	System.out.println("Kc is "+ Kc);
		return Kc;
		}*/
	
	/**
	 * 
	 * This will Find HMAC and Append it to 
	 * 
	 * @param username
	 * @param nonce
	 * @param hashedPassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String HMAC(String username, BigInteger nonce, String hashedPassword) throws NoSuchAlgorithmException
	{
		String hash = null;
		StringBuilder sb = new StringBuilder(14);
		sb.append(username).append(" ").append(nonce).append(" ").append(hashedPassword);
		String appended = sb.toString();
		try {
			hash= makeSHA512Hash(appended);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				}
		return hash;
		}

	/**
	 * This method will Decrypt Data Encrypted By Public Key 
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	 public static String decryptData(byte[] data) throws IOException {
		 byte[] descryptedData = null;
		 try {
			 PrivateKey privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);
			 Cipher cipher = Cipher.getInstance("RSA");
			 cipher.init(Cipher.DECRYPT_MODE, privateKey);
			 descryptedData = cipher.doFinal(data);
			 //System.out.println("Decrypted Data: " + new String(descryptedData));
			 } catch (Exception e) {
				 e.printStackTrace();
				 } 
		 return new String(descryptedData);
		 }
	 
	 public static void encryptPrivate(String appended) throws IOException{
			
			ArrayList<byte []> encrypted_data = new ArrayList<byte []>();
			for(final String token : Splitter.fixedLength(245).split(appended)){
		//		System.out.println("string token "+token);
				encrypted_data.add(EncryptUtil.encryptDataPrivate(token));
	   			}
			System.out.println("Encrypted data using Server's Private key"+encrypted_data);

		}

	 public static void encryptPrivateAA(String appended) throws IOException{
			
			ArrayList<byte []> encrypted_data = new ArrayList<byte []>();
			for(final String token : Splitter.fixedLength(245).split(appended)){
		//		System.out.println("string token "+token);
				encrypted_data.add(EncryptUtil.encryptDataPrivate(token));
	   			}
			System.out.println("Encrypted data using Server's Private key"+encrypted_data);

		}
	 
	 
	/**
	 * This method will Encrypt Data Using Private Key 
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	 public  static byte[] encryptDataPrivate(String data) throws IOException {
		 System.out.println("Data Before Encryption :" + data);
		 byte[] dataToEncrypt = data.getBytes();
		 byte[] encryptedData = null;
		 try {
			 PrivateKey pubKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);
			 Cipher cipher = Cipher.getInstance("RSA");
			 cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			 encryptedData = cipher.doFinal(dataToEncrypt);
			 // System.out.println("Encrypted Data: " + encryptedData);
			 } catch (Exception e) {
				 e.printStackTrace();
				 }
		 return encryptedData;
		 }
	 
	 
		 
	/**
	 * This method will Read Private Key From File 
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	 public static PrivateKey readPrivateKeyFromFile(String fileName) throws IOException{
		 FileInputStream fis = null;
		 ObjectInputStream ois = null;
		 try {
			 fis = new FileInputStream(new File(fileName));
			 ois = new ObjectInputStream(fis);   
			 BigInteger modulus = (BigInteger) ois.readObject();
			 BigInteger exponent = (BigInteger) ois.readObject();
			 //Get Private Key
			 RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
			 KeyFactory fact = KeyFactory.getInstance("RSA");
			 PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);
			 return privateKey;
			 } 
		 catch (Exception e) 
		 {
			 e.printStackTrace();
			 }
		 finally
		 {
			 if(ois != null){
				 ois.close();
				 if(fis != null){
					 fis.close();
					 }
				 }
			 }
		 return null;
		 }
	 
	

	 /**
	  * 
	  * This method will Encrypt Data with server's public key 
	  * 
	  * @param data
	  * @return
	  * @throws IOException
	  */
	 public  static byte[] encryptData(byte[] dataToEncrypt) throws IOException {
		 byte[] encryptedData = null;
		 try {
			 PublicKey pubKey = readPublicKeyFromFile(PUBLIC_KEY_FILE);
			 Cipher cipher = Cipher.getInstance("RSA");
			 cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			 encryptedData = cipher.doFinal(dataToEncrypt);
			 // System.out.println("Encrypted Data: " + encryptedData);
			 } catch (Exception e) {
				 e.printStackTrace();
				 }
		 return encryptedData;
		 }
	 
	 
	 public static String decryptDataPublic(byte[] data) throws IOException {
		 byte[] descryptedData = null;
		 try {
			 PublicKey publicKey = readPublicKeyFromFile(PUBLIC_KEY_FILE);
			 Cipher cipher = Cipher.getInstance("RSA");
			 cipher.init(Cipher.DECRYPT_MODE, publicKey);
			 descryptedData = cipher.doFinal(data);
			 } catch (Exception e) {
				 e.printStackTrace();
				 } 
		 return new String(descryptedData);
		 }
	 
	 
	 /**
	  * This method will Read Public Key From File
	  * 
	  * @param fileName
	  * @return
	  * @throws IOException
	  */
	 public static PublicKey readPublicKeyFromFile(String fileName) throws IOException{
		 FileInputStream fis = null;
		 ObjectInputStream ois = null;
		 try {
			 fis = new FileInputStream(new File(fileName));
			 ois = new ObjectInputStream(fis);

			 BigInteger modulus = (BigInteger) ois.readObject();
			 BigInteger exponent = (BigInteger) ois.readObject();
			 //Get Public Key
			 RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
			 KeyFactory fact = KeyFactory.getInstance("RSA");
			 PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
			 return publicKey;
			 } catch (Exception e) {
				 e.printStackTrace();
				 }
		 finally{
			 if(ois != null){
				 ois.close();
				 if(fis != null){
					 fis.close();
					 }
				 }
			 }
		 return null;
		 }
	 
	 
	 
	 public static BigInteger generateNonce(int bytes)
	 {
		 BigInteger nonce = new BigInteger(bytes, new SecureRandom());
		 return nonce;
	 }
	 
	 public static BigInteger generateNonce()
	 {
		 return generateNonce(128);
	 }
	 
	 public static String mergeUserDetails(String userName,String password , String nonce , String timestamp)
	 {
		 return userName+"~"+password+"~"+nonce+"~"+timestamp;
	 }
	 
	 public static String mergeAESDetails(String userName,String password, String timestamp)
	 {
		 return "Authentication~"+userName+"~"+password+"~"+timestamp;
	 }
	 
	 
	 
	 public static boolean currentTimeStampChecking(String inTime)
	 {
		 System.out.println("In time "+inTime);
		 System.out.println("Sys Time "+System.currentTimeMillis()) ;
		 return (System.currentTimeMillis())<Long.valueOf(inTime)+300000;
	 }
}
