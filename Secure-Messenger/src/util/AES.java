package util;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This program generates a AES key, retrieves its raw bytes, and then
 * reinstantiates a AES key from the key bytes. The reinstantiated key is used
 * to initialize a AES cipher for encryption and decryption.
 */
public class AES {

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
