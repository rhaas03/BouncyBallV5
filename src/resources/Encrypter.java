package resources;

import java.util.LinkedHashMap;
import java.util.Map;

public class Encrypter
{
	private  final String alpha = "abcdefghijklmnopqrstuvwxyz0123456789 _ABCDEFGHIJKLMNOPQRSTUVWXYZ.,\"':;-+=><\\/";
	private final Map<String, Integer> key = new LinkedHashMap<String, Integer>();

	public static final int LOW_ENCRYPTION = 0;
	public static final int STANDARD_ENCRYPTION = 1;
	public static final int HIGH_ENCRYPTION = 2;
	public static final int MAX_ENCRYPTION = 3;
	private int encLevel;

	private int encryptCounter = 0;
	private int decryptCounter = 0;

	public Encrypter(int encryptionLevel)
	{
		createKey();
		encLevel = encryptionLevel;
	}

	public Encrypter()
	{
		createKey();
		encLevel = STANDARD_ENCRYPTION;
	}

	private final void createKey()
	{
		for(int x = 0; x < alpha.length(); x++)
			key.put(Character.toString(alpha.charAt(x)), x + 1);
	}

	public String encryptString(String text) {
		String encString = "";
		for(int x = 0; x < text.length(); x++) {
			String toAdd = Integer.toString(key.get(Character.toString(text.charAt(x))));
			if(toAdd.length() < 2)
				toAdd = "0" + toAdd;
			encString += toAdd;
		}

		if(encryptCounter < encLevel) {
			encryptCounter++;
			return encryptString(encString);
		}
		else {
			encryptCounter = 0;
			return encString;
		}
	}

	public String decryptString(String text) {
		String decString = "";
		for(int x = 0; x < text.length(); x+=2) {
			int index = Integer.parseInt(text.substring(x, x+2));
			decString += key.keySet().toArray()[index - 1];
		}

		if(decryptCounter < encLevel) {
			decryptCounter++;
			return decryptString(decString);
		}
		else {
			decryptCounter = 0;
			return decString;
		}
	}

	public void setEncryptionLevel(int encryptionLevel)
	{
		encLevel = encryptionLevel;
	}
}
