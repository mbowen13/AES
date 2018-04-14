import java.io.*;

import static java.lang.System.*;

public class AES {

	private static int keySize; // 128 or 256
	private static FileInputStream keyFile;
    private static FileInputStream inputFile;
    private static FileInputStream outputFile;
    private static String mode; //encrypt or decrypt
    
    private int numberOfRounds;
    private int numberofKeys;
    
    // for testing
    private static int testKey[] = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,1,3,14,15,16};
    private static String testInput = "This is test."; // 16 bytes
	
	
	public static void readArgs(String[] args) {
		keySize = Integer.parseInt(args[0]);
		mode = args[4];
        try {
            keyFile = new FileInputStream(args[1]);
            inputFile = new FileInputStream(args[2]);
        } catch (FileNotFoundException e) {
            err.println(e.getMessage());
            exit(1);
        }
	}
	
	public AES(int[] key) {
		// key expansion
		// add round key
		numberOfRounds = 1;	
	}
	
	public static void subBytes() {}
	public static void shiftRows() {}
	public static void mixColumns() {}
	public static void addRoundKey() {}
	public static void keyExpansion() {}
	
	
	public static void main(String[] args) throws IOException {
        out.print("start");
       
    }
    
}
