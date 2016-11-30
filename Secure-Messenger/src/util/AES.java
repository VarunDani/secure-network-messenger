package util;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This program generates a AES key, retrieves its raw bytes, and then
 * reinstantiates a AES key from the key bytes. The reinstantiated key is used
 * to initialize a AES cipher for encryption and decryption.
 */
public class AES {

    /**
     * Encrypt a sample message using AES in CBC mode with a random IV genrated
     * using SecureRandom.
     *
     */
    public static void main(String[] args) {
        try {
            String message = "This string contains a secret message.";
            System.out.println("Plaintext: " + message + "\n");

           /* // generate a key
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);  // To use 256 bit keys, you need the "unlimited strength" encryption policy files from Sun.
            byte[] key = keygen.generateKey().getEncoded();*/
            
    		BigInteger nonce = new BigInteger(256, new SecureRandom());
    		
    		byte[] array = nonce.toByteArray();
    		if (array[0] == 0) {
    		    byte[] tmp = new byte[array.length - 1];
    		    System.arraycopy(array, 1, tmp, 0, tmp.length);
    		    array = tmp;
    		}
    		System.out.println("array: "+array);
    		SecretKeySpec skeySpec = new SecretKeySpec(array, "AES");
   //       System.out.println("key: "+key);
            System.out.println("skeyspec: "+skeySpec);
	 
            // build the initialization vector (randomly).
            SecureRandom random = new SecureRandom();
            byte iv[] = new byte[16];//generate random 16 byte IV AES is always 16bytes
            random.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            System.out.println("ivpec: "+ivspec);

            // initialize the cipher for encrypt mode
            Cipher cipher = Cipher.getInstance("AES/PCBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

            System.out.println("Key: " + new String(array, "utf-8") + " This is important when decrypting");
            System.out.println("IV: " + new String(iv, "utf-8") + " This is important when decrypting");
            System.out.println();

            // encrypt the message
            byte[] encrypted = cipher.doFinal(message.getBytes());
            System.out.println("Ciphertext: " + asHex(encrypted) + "\n");

	    //generate sha512 message digest
	      MessageDigest md = MessageDigest.getInstance("SHA-512");
	      md.update(encrypted);
	      byte[] digest = md.digest();
	      System.out.println("digest is " + asHex(digest));

            // reinitialize the cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

            // decrypt the message
            byte[] decrypted = cipher.doFinal(encrypted);
            System.out.println("Plaintext: " + new String(decrypted) + "\n");
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Turns array of bytes into string
     *
     * @param buf   Array of bytes to convert to hex string
     * @return  Generated hex string
     */
    public static String asHex(byte buf[]) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }
    
    
    
    public static byte[] encrypyUseingAES(BigInteger key, byte[] IV, String message )
    {
    	 byte[] encrypted = null;
    	try
    	{
    		byte[] array = key.toByteArray();
    		if (array[0] == 0) {
    		    byte[] tmp = new byte[array.length - 1];
    		    System.arraycopy(array, 1, tmp, 0, tmp.length);
    		    array = tmp;
    		}
    		
    		
    		SecretKeySpec skeySpec = new SecretKeySpec(array, "AES");
    		IvParameterSpec ivspec = new IvParameterSpec(IV);
    		
    		
			Cipher cipher = Cipher.getInstance("AES/PCBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
    		
             encrypted = cipher.doFinal(message.getBytes());
    	}
    	catch(Exception e )
    	{
    		System.out.println("Some Problem in AES Encryption");
    	}
    	return encrypted;
    }
    
    
    public static String decryptUseingAES(BigInteger key, byte[] IV, byte[] encryptedMsg )
    {
    	 byte[] decryptedMsg = null;
    	try
    	{
    		byte[] array = key.toByteArray();
    		if (array[0] == 0) {
    		    byte[] tmp = new byte[array.length - 1];
    		    System.arraycopy(array, 1, tmp, 0, tmp.length);
    		    array = tmp;
    		}
    		
    		
    		SecretKeySpec skeySpec = new SecretKeySpec(array, "AES");
    		IvParameterSpec ivspec = new IvParameterSpec(IV);
    		
    		
			Cipher cipher = Cipher.getInstance("AES/PCBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
    		
	        decryptedMsg = cipher.doFinal(encryptedMsg);
    	}
    	catch(Exception e )
    	{
    		System.out.println("Some Problem in AES Encryption");
    	}
    	return new String(decryptedMsg);
    }
    
    
    
    public static byte[] getIVSpecs()
    {
    	byte iv[] = new byte[16];;
    	try
    	{
    		
    		 SecureRandom random = new SecureRandom();
             //generate random 16 byte IV AES is always 16bytes
             random.nextBytes(iv);
    	}
    	catch(Exception e )
    	{
    		System.out.println("Problem in Generating New IV Spec");
    	}
    	return iv;
    }
}
