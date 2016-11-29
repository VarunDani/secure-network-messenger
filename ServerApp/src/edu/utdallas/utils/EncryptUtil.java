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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

public class EncryptUtil {


	
	private static final String PUBLIC_KEY_FILE = "Public.pem";
	private static final String PRIVATE_KEY_FILE = "Private.pem";
	
	public static void main(String args[]) throws NoSuchAlgorithmException, IOException {
		String passString="password";
		System.out.println("plaintext password: "+passString);
		String passSha = makeSHA512Hash(passString);
		System.out.println("SHA512 password: " +passSha);
		String username="jay";
		BigInteger nonce = new BigInteger(512, new SecureRandom());
		System.out.println("nonce: "+nonce);
		String appended = appendedd(username, nonce, passSha);
		String hmacr=HMAC(username, nonce, passSha);
		//	System.out.println(Arrays.toString(appended.split("(?<=\\G.{245})")));
		String sub1 = appended.substring(0, 245);
		String sub2 = appended.substring(245,appended.length());
		System.out.println("substring 1: "+sub1);
		System.out.println("substring 2: "+sub2);
		int len= appended.length();
		System.out.println("length: "+ len);
		System.out.println("hmacr: "+hmacr);
		System.out.println();
		
		try {
			System.out.println("-------GENRATE PUBLIC and PRIVATE KEY-------------");
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048); //1024 used for normal securities
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			System.out.println("Public Key - " + publicKey);
			System.out.println("Private Key - " + privateKey);
			
			//Pullingout parameters which makes up Key		 
			System.out.println("\n------- PULLING OUT PARAMETERS WHICH MAKES KEYPAIR----------\n");
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			System.out.println("PubKey Modulus : " + rsaPubKeySpec.getModulus());
			System.out.println("PubKey Exponent : " + rsaPubKeySpec.getPublicExponent());
			System.out.println("PrivKey Modulus : " + rsaPrivKeySpec.getModulus());
			System.out.println("PrivKey Exponent : " + rsaPrivKeySpec.getPrivateExponent());
			
			//Share public key with other so they can encrypt data and decrypt thoses using private key(Don't share with Other)
			System.out.println("\n--------SAVING PUBLIC KEY AND PRIVATE KEY TO FILES-------\n");
			EncryptUtil rsaObj = new EncryptUtil();
			rsaObj.saveKeys(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(), rsaPubKeySpec.getPublicExponent());
			rsaObj.saveKeys(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(), rsaPrivKeySpec.getPrivateExponent());
			
			//Encrypt Data using Public Key
			System.out.println("\n----------------ENCRYPTION STARTED------------");
			byte[] encryptedData1 = rsaObj.encryptData(sub1);
			byte[] encryptedData2 = rsaObj.encryptData(sub2);
			byte[] destination = new byte[encryptedData1.length + encryptedData2.length];
			// copy encryptedData1 into start of destination (from pos 0, copy ciphertext.length bytes)
			System.arraycopy(encryptedData1, 0, destination, 0, encryptedData1.length);
			// copy encryptedData2 into end of destination
			System.arraycopy(encryptedData2, 0, destination, encryptedData1.length, encryptedData2.length);
			System.out.println("encrypted message: "+destination);
			System.out.println("----------------ENCRYPTION COMPLETED------------");
			
			//Decrypt Data using Private Key
			System.out.println("\n----------------DECRYPTION STARTED------------");
			String xyz= rsaObj.decryptData(encryptedData1);
			String abc= rsaObj.decryptData(encryptedData2);
			StringBuilder sb1 = new StringBuilder(14);
			sb1.append(xyz).append(abc);
			String app = sb1.toString();
			System.out.println("decrypted message: "+app);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				}catch (InvalidKeySpecException e) {
					e.printStackTrace();
					}
		System.out.println("----------------DECRYPTION COMPLETED------------");
		
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
	 public  static byte[] encryptData(String data) throws IOException {
		 System.out.println("Data Before Encryption :" + data);
		 byte[] dataToEncrypt = data.getBytes();
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
		 return generateNonce(512);
	 }
	 
	 public static String mergeUserDetails(String userName,String password , String nonce , String timestamp)
	 {
		 return userName+"~"+password+"~"+nonce+"~"+timestamp;
	 }
	 
}
