import java.io.*;

import static java.lang.System.*;

public class AES {

	private static int keySize; // 128 or 256
	private static String mode; //encrypt or decrypt
	private static FileInputStream keyFile;
    private static FileInputStream inputFile;
    private static FileInputStream outputFile;
	
	
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
	
	public static void main(String[] args) throws IOException {
        out.print("start");
       
    }
    
}
