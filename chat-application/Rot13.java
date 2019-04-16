import java.util.Scanner;

public class Rot13 {
	
	public static void main(String[] args) {
		Rot13 jamesRot = new Rot13(); //object declaration
		Scanner jamesScanner = new Scanner(System.in); //get scanner
		//main Functions//
		System.out.print("Input string to encrypt/decrypt then press enter: ");
		String inputString = jamesScanner.nextLine(); //Input String to encrypt/decrpyt
		String rotifiedString = jamesRot.rotify(inputString); //Decrypted/Encrypted String Output
		String derotifiedString = jamesRot.derotify(rotifiedString);
		//Printing//
		System.out.println("---"); //divider line
		System.out.println("String Before:\n" + inputString); //printing the input string
		System.out.println("---"); //divider line
		System.out.println("String After:\n" + rotifiedString); //printing output string
		System.out.println("---"); //divider line
		System.out.println("Original String:\n" + derotifiedString);
	}

	private String rotify( String inputString) { //Method to encrypt/decrypt string
		String outputString = ""; //make empty string
		for (int i = 0; i < inputString.length(); i++) {
			char convertedChar = charRot(inputString.charAt(i)); //go through the inputString and then encode each letter
			outputString += convertedChar; //add the character to the above string
		}
		return outputString;
	}

	private String derotify( String inputString) { //Method to encrypt/decrypt string
		String outputString = ""; //make empty string
		for (int i = 0; i < inputString.length(); i++) {
			char convertedChar = charDeRot(inputString.charAt(i)); //go through the inputString and then encode each letter
			outputString += convertedChar; //add the character to the above string
		}
		return outputString;
	}
	public char charRot(char charInput) { //Method to encrypt/decrypt a single character
		char[] alphabets = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z' }; //alphabets array to check if the string contains a character
		char convertedChar = charInput;
		boolean isUppercase = !(convertedChar == (Character.toLowerCase(convertedChar))); //check if the current letter is uppercase

		for (int i = 0; i < 26; i++) {
			if (Character.toLowerCase(charInput) == alphabets[i]) { //convert charInput into lower case (to match with the array above)
				int convertedPositionInArray = i + 13; //ROT13 (add 13)

				if (convertedPositionInArray >= 26) { //if its over 'z' in the array, it resets it to 'a'
					convertedPositionInArray -= 26;
				}

				if (isUppercase) { //return character to uppercase
					convertedChar = Character.toUpperCase(alphabets[convertedPositionInArray]); //get character (uppercase)
				} else
					convertedChar = alphabets[convertedPositionInArray]; //get character (lowercase)
			}
		}
		return convertedChar;
	}
	public char charDeRot(char charInput) { //Method to encrypt/decrypt a single character
		char[] alphabets = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z' }; //alphabets array to check if the string contains a character
		char convertedChar = charInput;
		boolean isUppercase = !(convertedChar == (Character.toLowerCase(convertedChar))); //check if the current letter is uppercase

		for (int i = 0; i < 26; i++) {
			if (Character.toLowerCase(charInput) == alphabets[i]) { //convert charInput into lower case (to match with the array above)
				int convertedPositionInArray = i - 13; //ROT13 (add 13)
				String s = Integer.toBinaryString(convertedPositionInArray);
				
				if (s.length()==32 & s.startsWith("1")) { //if its over 'z' in the array, it resets it to 'a'
					convertedPositionInArray += 26;
				}

				if (isUppercase) { //return character to uppercase
					convertedChar = Character.toUpperCase(alphabets[convertedPositionInArray]); //get character (uppercase)
				} else
					convertedChar = alphabets[convertedPositionInArray]; //get character (lowercase)
			}
		}
		return convertedChar;
	}
}