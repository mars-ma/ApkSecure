package dev.mars.apksecure;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils {

	/** 加密工具 */
	private Cipher encryptCipher = null;

	/** 解密工具 */
	private Cipher decryptCipher = null;

	public void initialize_encryptKey(String keyValue) throws Exception {
		Key key = getKey(keyValue.getBytes());
		encryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
	}

	public void initalize_dencryptkey(String keyValue) throws Exception {
		Key key = getKey(keyValue.getBytes());
		decryptCipher = Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 * 
	 * @param arrBTmp
	 *            构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws java.lang.Exception
	 */
	private Key getKey(byte[] arrBTmp) throws Exception {
		DESKeySpec desKeySpec = new DESKeySpec(arrBTmp);
		SecretKeyFactory secretKeyFactory=SecretKeyFactory.getInstance("DES");
		Key key =secretKeyFactory.generateSecret(desKeySpec);
		return key;
	}

	/**
	 * 加密字节数组
	 * 
	 * @param arrB
	 *            需加密的字节数组
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * 解密字节数组
	 * 
	 * @param arrB
	 *            需解密的字节数组
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * 文件file进行加密并保存目标文件destFile中
	 * 
	 * @param file
	 *            要加密的文件 如c:/test/srcFile.txt
	 * @param destFile
	 *            加密后存放的文件名 如c:/加密后文件.txt
	 */
	public void encrypt(String sourceFileName, String diminationFileName)
			throws Exception {
		InputStream is = new FileInputStream(sourceFileName);
		OutputStream out = new FileOutputStream(diminationFileName);
		CipherInputStream cis = new CipherInputStream(is, encryptCipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = cis.read(buffer)) > 0) {
			out.write(buffer, 0, r);
		}
		cis.close();
		is.close();
		out.close();
	}

	/**
	 * 文件采用DES算法解密文件
	 * 
	 * @param file
	 *            已加密的文件 如c:/加密后文件.txt * @param destFile 解密后存放的文件名 如c:/
	 *            test/解密后文件.txt
	 */
	public void decrypt(String sourceFileName, String diminationFileName)
			throws Exception {
		InputStream is = new FileInputStream(sourceFileName);
		OutputStream out = new FileOutputStream(diminationFileName);
		CipherOutputStream cos = new CipherOutputStream(out, decryptCipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = is.read(buffer)) >= 0) {
			cos.write(buffer, 0, r);
		}
		cos.close();
		out.close();
		is.close();
	}

}