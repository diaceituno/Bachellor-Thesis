package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class IOControl {

	private String resPath;
	private String confPath;
	private SecretKeySpec key;
	private Cipher cipher;
	
	public IOControl(String resPath, String confPath) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
		this.resPath = resPath;
		this.confPath = confPath;
		
		byte[] bKey = "miqr.nokela.gmbh".getBytes("UTF-8");
		key = new SecretKeySpec(bKey,"AES");
		cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	}
	
	public Node loadPane(String paneName, Object paneController) {
		
		String panePath = resPath + paneName;
		URL paneURL = null;
		try {
			paneURL = new File(panePath).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		if(paneURL != null) {
			
			FXMLLoader fxmlLoader = new FXMLLoader(paneURL);
			fxmlLoader.setController(paneController);
			try {
				Node paneNode = fxmlLoader.load();
				return paneNode;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return null;
	}
	
	public boolean saveEncrypted(String fileName, String plainText) {
		
		String filePath = confPath + fileName;
		File cryptFile = new File(filePath);
		String encrypted = null;
				
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return false;
		}
		
		try {
			encrypted = Base64.getEncoder().withoutPadding().encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")));
		} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	
		if(encrypted != null) {
			OutputStreamWriter osWriter;
			try {
				osWriter = new OutputStreamWriter(new FileOutputStream(cryptFile), StandardCharsets.UTF_8);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			
			if(osWriter != null) {
				try {
					osWriter.write(encrypted);
					osWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}      
		}
		return true;
	}
	
	public String loadEncrypted(String fileName) {
		
		String filePath = confPath + fileName;
		StringBuilder sBuilder = new StringBuilder();
		String decoded;
		BufferedReader bReader = null;
			
		try {
			bReader = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		if(bReader != null) {
			try {
				String line = bReader.readLine();
				while(line != null) {
					
					sBuilder.append(line);
					line = bReader.readLine();
				}
				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			decoded = new String(cipher.doFinal(Base64.getDecoder().
					decode(sBuilder.toString())));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
		return decoded;
	}
}
