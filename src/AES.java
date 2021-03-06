import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import static java.lang.System.*;
import javax.xml.bind.DatatypeConverter;

public class AES {
    private static int keySize; // 128 or 256
      private static FileInputStream keyFile;
      private static FileInputStream inputFile;
    private static FileInputStream outputFile;
    private static String mode; //encrypt or decrypt

    private final static byte[] sbox = {
            0x63, 0x7c, 0x77, 0x7b, (byte) 0xf2, 0x6b, 0x6f, (byte) 0xc5, 0x30, 0x01, 0x67, 0x2b, (byte) 0xfe, (byte) 0xd7, (byte) 0xab, 0x76,
            (byte) 0xca, (byte) 0x82, (byte) 0xc9, 0x7d, (byte) 0xfa, 0x59, 0x47, (byte) 0xf0, (byte) 0xad, (byte) 0xd4, (byte) 0xa2, (byte) 0xaf, (byte) 0x9c, (byte) 0xa4, 0x72, (byte) 0xc0,
            (byte) 0xb7, (byte) 0xfd, (byte) 0x93, 0x26, 0x36, 0x3f, (byte) 0xf7, (byte) 0xcc, 0x34, (byte) 0xa5, (byte) 0xe5, (byte) 0xf1, 0x71, (byte) 0xd8, 0x31, 0x15,
            0x04, (byte) 0xc7, 0x23, (byte) 0xc3, 0x18, (byte) 0x96, 0x05, (byte) 0x9a, 0x07, 0x12, (byte) 0x80, (byte) 0xe2, (byte) 0xeb, 0x27, (byte) 0xb2, 0x75,
            0x09, (byte) 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, (byte) 0xa0, 0x52, 0x3b, (byte) 0xd6, (byte) 0xb3, 0x29, (byte) 0xe3, 0x2f, (byte) 0x84,
            0x53, (byte) 0xd1, 0x00, (byte) 0xed, 0x20, (byte) 0xfc, (byte) 0xb1, 0x5b, 0x6a, (byte) 0xcb, (byte) 0xbe, 0x39, 0x4a, 0x4c, 0x58, (byte) 0xcf,
            (byte) 0xd0, (byte) 0xef, (byte) 0xaa, (byte) 0xfb, 0x43, 0x4d, 0x33, (byte) 0x85, 0x45, (byte) 0xf9, 0x02, 0x7f, 0x50, 0x3c, (byte) 0x9f, (byte) 0xa8,
            0x51, (byte) 0xa3, 0x40, (byte) 0x8f, (byte) 0x92, (byte) 0x9d, 0x38, (byte) 0xf5, (byte) 0xbc, (byte) 0xb6, (byte) 0xda, 0x21, 0x10, (byte) 0xff, (byte) 0xf3, (byte) 0xd2,
            (byte) 0xcd, 0x0c, 0x13, (byte) 0xec, 0x5f, (byte) 0x97, 0x44, 0x17, (byte) 0xc4, (byte) 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
            0x60, (byte) 0x81, 0x4f, (byte) 0xdc, 0x22, 0x2a, (byte) 0x90, (byte) 0x88, 0x46, (byte) 0xee, (byte) 0xb8, 0x14, (byte) 0xde, 0x5e, 0x0b, (byte) 0xdb,
            (byte) 0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, (byte) 0xc2, (byte) 0xd3, (byte) 0xac, 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xe4, 0x79,
            (byte) 0xe7, (byte) 0xc8, 0x37, 0x6d, (byte) 0x8d, (byte) 0xd5, 0x4e, (byte) 0xa9, 0x6c, 0x56, (byte) 0xf4, (byte) 0xea, 0x65, 0x7a, (byte) 0xae, 0x08,
            (byte) 0xba, 0x78, 0x25, 0x2e, 0x1c, (byte) 0xa6, (byte) 0xb4, (byte) 0xc6, (byte) 0xe8, (byte) 0xdd, 0x74, 0x1f, 0x4b, (byte) 0xbd, (byte) 0x8b, (byte) 0x8a,
            0x70, 0x3e, (byte) 0xb5, 0x66, 0x48, 0x03, (byte) 0xf6, 0x0e, 0x61, 0x35, 0x57, (byte) 0xb9, (byte) 0x86, (byte) 0xc1, 0x1d, (byte) 0x9e,
            (byte) 0xe1, (byte) 0xf8, (byte) 0x98, 0x11, 0x69, (byte) 0xd9, (byte) 0x8e, (byte) 0x94, (byte) 0x9b, 0x1e, (byte) 0x87, (byte) 0xe9, (byte) 0xce, 0x55, 0x28, (byte) 0xdf,
            (byte) 0x8c, (byte) 0xa1, (byte) 0x89, 0x0d, (byte) 0xbf, (byte) 0xe6, 0x42, 0x68, 0x41, (byte) 0x99, 0x2d, 0x0f, (byte) 0xb0, 0x54, (byte) 0xbb, 0x16
    };
    
    private final static byte invSbox[] = 
        {
           (byte) 0x52, (byte) 0x09, (byte) 0x6A, (byte) 0xD5, (byte) 0x30, (byte) 0x36, (byte) 0xA5, (byte) 0x38, (byte) 0xBF, (byte) 0x40, (byte) 0xA3, (byte) 0x9E, (byte) 0x81, (byte) 0xF3, (byte) 0xD7, (byte) 0xFB,
           (byte) 0x7C, (byte) 0xE3, (byte) 0x39, (byte) 0x82, (byte) 0x9B, (byte) 0x2F, (byte) 0xFF, (byte) 0x87, (byte) 0x34, (byte) 0x8E, (byte) 0x43, (byte) 0x44, (byte) 0xC4, (byte) 0xDE, (byte) 0xE9, (byte) 0xCB,
           (byte) 0x54, (byte) 0x7B, (byte) 0x94, (byte) 0x32, (byte) 0xA6, (byte) 0xC2, (byte) 0x23, (byte) 0x3D, (byte) 0xEE, (byte) 0x4C, (byte) 0x95, (byte) 0x0B, (byte) 0x42, (byte) 0xFA, (byte) 0xC3, (byte) 0x4E,
           (byte) 0x08, (byte) 0x2E, (byte) 0xA1, (byte) 0x66, (byte) 0x28, (byte) 0xD9, (byte) 0x24, (byte) 0xB2, (byte) 0x76, (byte) 0x5B, (byte) 0xA2, (byte) 0x49, (byte) 0x6D, (byte) 0x8B, (byte) 0xD1, (byte) 0x25,
           (byte) 0x72, (byte) 0xF8, (byte) 0xF6, (byte) 0x64, (byte) 0x86, (byte) 0x68, (byte) 0x98, (byte) 0x16, (byte) 0xD4, (byte) 0xA4, (byte) 0x5C, (byte) 0xCC, (byte) 0x5D, (byte) 0x65, (byte) 0xB6, (byte) 0x92,
           (byte) 0x6C, (byte) 0x70, (byte) 0x48, (byte) 0x50, (byte) 0xFD, (byte) 0xED, (byte) 0xB9, (byte) 0xDA, (byte) 0x5E, (byte) 0x15, (byte) 0x46, (byte) 0x57, (byte) 0xA7, (byte) 0x8D, (byte) 0x9D, (byte) 0x84,
           (byte) 0x90, (byte) 0xD8, (byte) 0xAB, (byte) 0x00, (byte) 0x8C, (byte) 0xBC, (byte) 0xD3, (byte) 0x0A, (byte) 0xF7, (byte) 0xE4, (byte) 0x58, (byte) 0x05, (byte) 0xB8, (byte) 0xB3, (byte) 0x45, (byte) 0x06,
           (byte) 0xD0, (byte) 0x2C, (byte) 0x1E, (byte) 0x8F, (byte) 0xCA, (byte) 0x3F, (byte) 0x0F, (byte) 0x02, (byte) 0xC1, (byte) 0xAF, (byte) 0xBD, (byte) 0x03, (byte) 0x01, (byte) 0x13, (byte) 0x8A, (byte) 0x6B,
           (byte) 0x3A, (byte) 0x91, (byte) 0x11, (byte) 0x41, (byte) 0x4F, (byte) 0x67, (byte) 0xDC, (byte) 0xEA, (byte) 0x97, (byte) 0xF2, (byte) 0xCF, (byte) 0xCE, (byte) 0xF0, (byte) 0xB4, (byte) 0xE6, (byte) 0x73,
           (byte) 0x96, (byte) 0xAC, (byte) 0x74, (byte) 0x22, (byte) 0xE7, (byte) 0xAD, (byte) 0x35, (byte) 0x85, (byte) 0xE2, (byte) 0xF9, (byte) 0x37, (byte) 0xE8, (byte) 0x1C, (byte) 0x75, (byte) 0xDF, (byte) 0x6E,
           (byte) 0x47, (byte) 0xF1, (byte) 0x1A, (byte) 0x71, (byte) 0x1D, (byte) 0x29, (byte) 0xC5, (byte) 0x89, (byte) 0x6F, (byte) 0xB7, (byte) 0x62, (byte) 0x0E, (byte) 0xAA, (byte) 0x18, (byte) 0xBE, (byte) 0x1B,
           (byte) 0xFC, (byte) 0x56, (byte) 0x3E, (byte) 0x4B, (byte) 0xC6, (byte) 0xD2, (byte) 0x79, (byte) 0x20, (byte) 0x9A, (byte) 0xDB, (byte) 0xC0, (byte) 0xFE, (byte) 0x78, (byte) 0xCD, (byte) 0x5A, (byte) 0xF4,
           (byte) 0x1F, (byte) 0xDD, (byte) 0xA8, (byte) 0x33, (byte) 0x88, (byte) 0x07, (byte) 0xC7, (byte) 0x31, (byte) 0xB1, (byte) 0x12, (byte) 0x10, (byte) 0x59, (byte) 0x27, (byte) 0x80, (byte) 0xEC, (byte) 0x5F,
           (byte) 0x60, (byte) 0x51, (byte) 0x7F, (byte) 0xA9, (byte) 0x19, (byte) 0xB5, (byte) 0x4A, (byte) 0x0D, (byte) 0x2D, (byte) 0xE5, (byte) 0x7A, (byte) 0x9F, (byte) 0x93, (byte) 0xC9, (byte) 0x9C, (byte) 0xEF,
           (byte) 0xA0, (byte) 0xE0, (byte) 0x3B, (byte) 0x4D, (byte) 0xAE, (byte) 0x2A, (byte) 0xF5, (byte) 0xB0, (byte) 0xC8, (byte) 0xEB, (byte) 0xBB, (byte) 0x3C, (byte) 0x83, (byte) 0x53, (byte) 0x99, (byte) 0x61,
           (byte) 0x17, (byte) 0x2B, (byte) 0x04, (byte) 0x7E, (byte) 0xBA, (byte) 0x77, (byte) 0xD6, (byte) 0x26, (byte) 0xE1, (byte) 0x69, (byte) 0x14, (byte) 0x63, (byte) 0x55, (byte) 0x21, (byte) 0x0C, (byte) 0x7D
        };
    
    private static final byte[] mul2= {  (byte) 0x00,(byte) 0x02,(byte) 0x04,(byte) 0x06,(byte) 0x08,(byte) 0x0a,(byte) 0x0c,(byte) 0x0e,(byte) 0x10,(byte) 0x12,(byte) 0x14,(byte) 0x16,(byte) 0x18,(byte) 0x1a,(byte) 0x1c,(byte) 0x1e,
            (byte) 0x20,(byte) 0x22,(byte) 0x24,(byte) 0x26,(byte) 0x28,(byte) 0x2a,(byte) 0x2c,(byte) 0x2e,(byte) 0x30,(byte) 0x32,(byte) 0x34,(byte) 0x36,(byte) 0x38,(byte) 0x3a,(byte) 0x3c,(byte) 0x3e,
            (byte) 0x40,(byte) 0x42,(byte) 0x44,(byte) 0x46,(byte) 0x48,(byte) 0x4a,(byte) 0x4c,(byte) 0x4e,(byte) 0x50,(byte) 0x52,(byte) 0x54,(byte) 0x56,(byte) 0x58,(byte) 0x5a,(byte) 0x5c,(byte) 0x5e,
            (byte) 0x60,(byte) 0x62,(byte) 0x64,(byte) 0x66,(byte) 0x68,(byte) 0x6a,(byte) 0x6c,(byte) 0x6e,(byte) 0x70,(byte) 0x72,(byte) 0x74,(byte) 0x76,(byte) 0x78,(byte) 0x7a,(byte) 0x7c,(byte) 0x7e,
            (byte) 0x80,(byte) 0x82,(byte) 0x84,(byte) 0x86,(byte) 0x88,(byte) 0x8a,(byte) 0x8c,(byte) 0x8e,(byte) 0x90,(byte) 0x92,(byte) 0x94,(byte) 0x96,(byte) 0x98,(byte) 0x9a,(byte) 0x9c,(byte) 0x9e,
            (byte) 0xa0,(byte) 0xa2,(byte) 0xa4,(byte) 0xa6,(byte) 0xa8,(byte) 0xaa,(byte) 0xac,(byte) 0xae,(byte) 0xb0,(byte) 0xb2,(byte) 0xb4,(byte) 0xb6,(byte) 0xb8,(byte) 0xba,(byte) 0xbc,(byte) 0xbe,
            (byte) 0xc0,(byte) 0xc2,(byte) 0xc4,(byte) 0xc6,(byte) 0xc8,(byte) 0xca,(byte) 0xcc,(byte) 0xce,(byte) 0xd0,(byte) 0xd2,(byte) 0xd4,(byte) 0xd6,(byte) 0xd8,(byte) 0xda,(byte) 0xdc,(byte) 0xde,
            (byte) 0xe0,(byte) 0xe2,(byte) 0xe4,(byte) 0xe6,(byte) 0xe8,(byte) 0xea,(byte) 0xec,(byte) 0xee,(byte) 0xf0,(byte) 0xf2,(byte) 0xf4,(byte) 0xf6,(byte) 0xf8,(byte) 0xfa,(byte) 0xfc,(byte) 0xfe,
            (byte) 0x1b,(byte) 0x19,(byte) 0x1f,(byte) 0x1d,(byte) 0x13,(byte) 0x11,(byte) 0x17,(byte) 0x15,(byte) 0x0b,(byte) 0x09,(byte) 0x0f,(byte) 0x0d,(byte) 0x03,(byte) 0x01,(byte) 0x07,(byte) 0x05,
            (byte) 0x3b,(byte) 0x39,(byte) 0x3f,(byte) 0x3d,(byte) 0x33,(byte) 0x31,(byte) 0x37,(byte) 0x35,(byte) 0x2b,(byte) 0x29,(byte) 0x2f,(byte) 0x2d,(byte) 0x23,(byte) 0x21,(byte) 0x27,(byte) 0x25,
            (byte) 0x5b,(byte) 0x59,(byte) 0x5f,(byte) 0x5d,(byte) 0x53,(byte) 0x51,(byte) 0x57,(byte) 0x55,(byte) 0x4b,(byte) 0x49,(byte) 0x4f,(byte) 0x4d,(byte) 0x43,(byte) 0x41,(byte) 0x47,(byte) 0x45,
            (byte) 0x7b,(byte) 0x79,(byte) 0x7f,(byte) 0x7d,(byte) 0x73,(byte) 0x71,(byte) 0x77,(byte) 0x75,(byte) 0x6b,(byte) 0x69,(byte) 0x6f,(byte) 0x6d,(byte) 0x63,(byte) 0x61,(byte) 0x67,(byte) 0x65,
            (byte) 0x9b,(byte) 0x99,(byte) 0x9f,(byte) 0x9d,(byte) 0x93,(byte) 0x91,(byte) 0x97,(byte) 0x95,(byte) 0x8b,(byte) 0x89,(byte) 0x8f,(byte) 0x8d,(byte) 0x83,(byte) 0x81,(byte) 0x87,(byte) 0x85,
            (byte) 0xbb,(byte) 0xb9,(byte) 0xbf,(byte) 0xbd,(byte) 0xb3,(byte) 0xb1,(byte) 0xb7,(byte) 0xb5,(byte) 0xab,(byte) 0xa9,(byte) 0xaf,(byte) 0xad,(byte) 0xa3,(byte) 0xa1,(byte) 0xa7,(byte) 0xa5,
            (byte) 0xdb,(byte) 0xd9,(byte) 0xdf,(byte) 0xdd,(byte) 0xd3,(byte) 0xd1,(byte) 0xd7,(byte) 0xd5,(byte) 0xcb,(byte) 0xc9,(byte) 0xcf,(byte) 0xcd,(byte) 0xc3,(byte) 0xc1,(byte) 0xc7,(byte) 0xc5,
            (byte) 0xfb,(byte) 0xf9,(byte) 0xff,(byte) 0xfd,(byte) 0xf3,(byte) 0xf1,(byte) 0xf7,(byte) 0xf5,(byte) 0xeb,(byte) 0xe9,(byte) 0xef,(byte) 0xed,(byte) 0xe3,(byte) 0xe1,(byte) 0xe7,(byte) 0xe5 
    };
    
    private static final byte[] mul3 = {
            (byte) 0x00,(byte) 0x03,(byte) 0x06,(byte) 0x05,(byte) 0x0c,(byte) 0x0f,(byte) 0x0a,(byte) 0x09,(byte) 0x18,(byte) 0x1b,(byte) 0x1e,(byte) 0x1d,(byte) 0x14,(byte) 0x17,(byte) 0x12,(byte) 0x11,
            (byte) 0x30,(byte) 0x33,(byte) 0x36,(byte) 0x35,(byte) 0x3c,(byte) 0x3f,(byte) 0x3a,(byte) 0x39,(byte) 0x28,(byte) 0x2b,(byte) 0x2e,(byte) 0x2d,(byte) 0x24,(byte) 0x27,(byte) 0x22,(byte) 0x21,
            (byte) 0x60,(byte) 0x63,(byte) 0x66,(byte) 0x65,(byte) 0x6c,(byte) 0x6f,(byte) 0x6a,(byte) 0x69,(byte) 0x78,(byte) 0x7b,(byte) 0x7e,(byte) 0x7d,(byte) 0x74,(byte) 0x77,(byte) 0x72,(byte) 0x71,
            (byte) 0x50,(byte) 0x53,(byte) 0x56,(byte) 0x55,(byte) 0x5c,(byte) 0x5f,(byte) 0x5a,(byte) 0x59,(byte) 0x48,(byte) 0x4b,(byte) 0x4e,(byte) 0x4d,(byte) 0x44,(byte) 0x47,(byte) 0x42,(byte) 0x41,
            (byte) 0xc0,(byte) 0xc3,(byte) 0xc6,(byte) 0xc5,(byte) 0xcc,(byte) 0xcf,(byte) 0xca,(byte) 0xc9,(byte) 0xd8,(byte) 0xdb,(byte) 0xde,(byte) 0xdd,(byte) 0xd4,(byte) 0xd7,(byte) 0xd2,(byte) 0xd1,
            (byte) 0xf0,(byte) 0xf3,(byte) 0xf6,(byte) 0xf5,(byte) 0xfc,(byte) 0xff,(byte) 0xfa,(byte) 0xf9,(byte) 0xe8,(byte) 0xeb,(byte) 0xee,(byte) 0xed,(byte) 0xe4,(byte) 0xe7,(byte) 0xe2,(byte) 0xe1,
            (byte) 0xa0,(byte) 0xa3,(byte) 0xa6,(byte) 0xa5,(byte) 0xac,(byte) 0xaf,(byte) 0xaa,(byte) 0xa9,(byte) 0xb8,(byte) 0xbb,(byte) 0xbe,(byte) 0xbd,(byte) 0xb4,(byte) 0xb7,(byte) 0xb2,(byte) 0xb1,
            (byte) 0x90,(byte) 0x93,(byte) 0x96,(byte) 0x95,(byte) 0x9c,(byte) 0x9f,(byte) 0x9a,(byte) 0x99,(byte) 0x88,(byte) 0x8b,(byte) 0x8e,(byte) 0x8d,(byte) 0x84,(byte) 0x87,(byte) 0x82,(byte) 0x81,
            (byte) 0x9b,(byte) 0x98,(byte) 0x9d,(byte) 0x9e,(byte) 0x97,(byte) 0x94,(byte) 0x91,(byte) 0x92,(byte) 0x83,(byte) 0x80,(byte) 0x85,(byte) 0x86,(byte) 0x8f,(byte) 0x8c,(byte) 0x89,(byte) 0x8a,
            (byte) 0xab,(byte) 0xa8,(byte) 0xad,(byte) 0xae,(byte) 0xa7,(byte) 0xa4,(byte) 0xa1,(byte) 0xa2,(byte) 0xb3,(byte) 0xb0,(byte) 0xb5,(byte) 0xb6,(byte) 0xbf,(byte) 0xbc,(byte) 0xb9,(byte) 0xba,
            (byte) 0xfb,(byte) 0xf8,(byte) 0xfd,(byte) 0xfe,(byte) 0xf7,(byte) 0xf4,(byte) 0xf1,(byte) 0xf2,(byte) 0xe3,(byte) 0xe0,(byte) 0xe5,(byte) 0xe6,(byte) 0xef,(byte) 0xec,(byte) 0xe9,(byte) 0xea,
            (byte) 0xcb,(byte) 0xc8,(byte) 0xcd,(byte) 0xce,(byte) 0xc7,(byte) 0xc4,(byte) 0xc1,(byte) 0xc2,(byte) 0xd3,(byte) 0xd0,(byte) 0xd5,(byte) 0xd6,(byte) 0xdf,(byte) 0xdc,(byte) 0xd9,(byte) 0xda,
            (byte) 0x5b,(byte) 0x58,(byte) 0x5d,(byte) 0x5e,(byte) 0x57,(byte) 0x54,(byte) 0x51,(byte) 0x52,(byte) 0x43,(byte) 0x40,(byte) 0x45,(byte) 0x46,(byte) 0x4f,(byte) 0x4c,(byte) 0x49,(byte) 0x4a,
            (byte) 0x6b,(byte) 0x68,(byte) 0x6d,(byte) 0x6e,(byte) 0x67,(byte) 0x64,(byte) 0x61,(byte) 0x62,(byte) 0x73,(byte) 0x70,(byte) 0x75,(byte) 0x76,(byte) 0x7f,(byte) 0x7c,(byte) 0x79,(byte) 0x7a,
            (byte) 0x3b,(byte) 0x38,(byte) 0x3d,(byte) 0x3e,(byte) 0x37,(byte) 0x34,(byte) 0x31,(byte) 0x32,(byte) 0x23,(byte) 0x20,(byte) 0x25,(byte) 0x26,(byte) 0x2f,(byte) 0x2c,(byte) 0x29,(byte) 0x2a,
            (byte) 0x0b,(byte) 0x08,(byte) 0x0d,(byte) 0x0e,(byte) 0x07,(byte) 0x04,(byte) 0x01,(byte) 0x02,(byte) 0x13,(byte) 0x10,(byte) 0x15,(byte) 0x16,(byte) 0x1f,(byte) 0x1c,(byte) 0x19,(byte) 0x1a
    };
    
    private static final byte[] mul9 = {
            (byte) 0x00,(byte) 0x09,(byte) 0x12,(byte) 0x1b,(byte) 0x24,(byte) 0x2d,(byte) 0x36,(byte) 0x3f,(byte) 0x48,(byte) 0x41,(byte) 0x5a,(byte) 0x53,(byte) 0x6c,(byte) 0x65,(byte) 0x7e,(byte) 0x77,
            (byte) 0x90,(byte) 0x99,(byte) 0x82,(byte) 0x8b,(byte) 0xb4,(byte) 0xbd,(byte) 0xa6,(byte) 0xaf,(byte) 0xd8,(byte) 0xd1,(byte) 0xca,(byte) 0xc3,(byte) 0xfc,(byte) 0xf5,(byte) 0xee,(byte) 0xe7,
            (byte) 0x3b,(byte) 0x32,(byte) 0x29,(byte) 0x20,(byte) 0x1f,(byte) 0x16,(byte) 0x0d,(byte) 0x04,(byte) 0x73,(byte) 0x7a,(byte) 0x61,(byte) 0x68,(byte) 0x57,(byte) 0x5e,(byte) 0x45,(byte) 0x4c,
            (byte) 0xab,(byte) 0xa2,(byte) 0xb9,(byte) 0xb0,(byte) 0x8f,(byte) 0x86,(byte) 0x9d,(byte) 0x94,(byte) 0xe3,(byte) 0xea,(byte) 0xf1,(byte) 0xf8,(byte) 0xc7,(byte) 0xce,(byte) 0xd5,(byte) 0xdc,
            (byte) 0x76,(byte) 0x7f,(byte) 0x64,(byte) 0x6d,(byte) 0x52,(byte) 0x5b,(byte) 0x40,(byte) 0x49,(byte) 0x3e,(byte) 0x37,(byte) 0x2c,(byte) 0x25,(byte) 0x1a,(byte) 0x13,(byte) 0x08,(byte) 0x01,
            (byte) 0xe6,(byte) 0xef,(byte) 0xf4,(byte) 0xfd,(byte) 0xc2,(byte) 0xcb,(byte) 0xd0,(byte) 0xd9,(byte) 0xae,(byte) 0xa7,(byte) 0xbc,(byte) 0xb5,(byte) 0x8a,(byte) 0x83,(byte) 0x98,(byte) 0x91,
            (byte) 0x4d,(byte) 0x44,(byte) 0x5f,(byte) 0x56,(byte) 0x69,(byte) 0x60,(byte) 0x7b,(byte) 0x72,(byte) 0x05,(byte) 0x0c,(byte) 0x17,(byte) 0x1e,(byte) 0x21,(byte) 0x28,(byte) 0x33,(byte) 0x3a,
            (byte) 0xdd,(byte) 0xd4,(byte) 0xcf,(byte) 0xc6,(byte) 0xf9,(byte) 0xf0,(byte) 0xeb,(byte) 0xe2,(byte) 0x95,(byte) 0x9c,(byte) 0x87,(byte) 0x8e,(byte) 0xb1,(byte) 0xb8,(byte) 0xa3,(byte) 0xaa,
            (byte) 0xec,(byte) 0xe5,(byte) 0xfe,(byte) 0xf7,(byte) 0xc8,(byte) 0xc1,(byte) 0xda,(byte) 0xd3,(byte) 0xa4,(byte) 0xad,(byte) 0xb6,(byte) 0xbf,(byte) 0x80,(byte) 0x89,(byte) 0x92,(byte) 0x9b,
            (byte) 0x7c,(byte) 0x75,(byte) 0x6e,(byte) 0x67,(byte) 0x58,(byte) 0x51,(byte) 0x4a,(byte) 0x43,(byte) 0x34,(byte) 0x3d,(byte) 0x26,(byte) 0x2f,(byte) 0x10,(byte) 0x19,(byte) 0x02,(byte) 0x0b,
            (byte) 0xd7,(byte) 0xde,(byte) 0xc5,(byte) 0xcc,(byte) 0xf3,(byte) 0xfa,(byte) 0xe1,(byte) 0xe8,(byte) 0x9f,(byte) 0x96,(byte) 0x8d,(byte) 0x84,(byte) 0xbb,(byte) 0xb2,(byte) 0xa9,(byte) 0xa0,
            (byte) 0x47,(byte) 0x4e,(byte) 0x55,(byte) 0x5c,(byte) 0x63,(byte) 0x6a,(byte) 0x71,(byte) 0x78,(byte) 0x0f,(byte) 0x06,(byte) 0x1d,(byte) 0x14,(byte) 0x2b,(byte) 0x22,(byte) 0x39,(byte) 0x30,
            (byte) 0x9a,(byte) 0x93,(byte) 0x88,(byte) 0x81,(byte) 0xbe,(byte) 0xb7,(byte) 0xac,(byte) 0xa5,(byte) 0xd2,(byte) 0xdb,(byte) 0xc0,(byte) 0xc9,(byte) 0xf6,(byte) 0xff,(byte) 0xe4,(byte) 0xed,
            (byte) 0x0a,(byte) 0x03,(byte) 0x18,(byte) 0x11,(byte) 0x2e,(byte) 0x27,(byte) 0x3c,(byte) 0x35,(byte) 0x42,(byte) 0x4b,(byte) 0x50,(byte) 0x59,(byte) 0x66,(byte) 0x6f,(byte) 0x74,(byte) 0x7d,
            (byte) 0xa1,(byte) 0xa8,(byte) 0xb3,(byte) 0xba,(byte) 0x85,(byte) 0x8c,(byte) 0x97,(byte) 0x9e,(byte) 0xe9,(byte) 0xe0,(byte) 0xfb,(byte) 0xf2,(byte) 0xcd,(byte) 0xc4,(byte) 0xdf,(byte) 0xd6,
            (byte) 0x31,(byte) 0x38,(byte) 0x23,(byte) 0x2a,(byte) 0x15,(byte) 0x1c,(byte) 0x07,(byte) 0x0e,(byte) 0x79,(byte) 0x70,(byte) 0x6b,(byte) 0x62,(byte) 0x5d,(byte) 0x54,(byte) 0x4f,(byte) 0x46
    };
    
    private static final byte[] mul11 = {
            (byte) 0x00,(byte) 0x0b,(byte) 0x16,(byte) 0x1d,(byte) 0x2c,(byte) 0x27,(byte) 0x3a,(byte) 0x31,(byte) 0x58,(byte) 0x53,(byte) 0x4e,(byte) 0x45,(byte) 0x74,(byte) 0x7f,(byte) 0x62,(byte) 0x69,
            (byte) 0xb0,(byte) 0xbb,(byte) 0xa6,(byte) 0xad,(byte) 0x9c,(byte) 0x97,(byte) 0x8a,(byte) 0x81,(byte) 0xe8,(byte) 0xe3,(byte) 0xfe,(byte) 0xf5,(byte) 0xc4,(byte) 0xcf,(byte) 0xd2,(byte) 0xd9,
            (byte) 0x7b,(byte) 0x70,(byte) 0x6d,(byte) 0x66,(byte) 0x57,(byte) 0x5c,(byte) 0x41,(byte) 0x4a,(byte) 0x23,(byte) 0x28,(byte) 0x35,(byte) 0x3e,(byte) 0x0f,(byte) 0x04,(byte) 0x19,(byte) 0x12,
            (byte) 0xcb,(byte) 0xc0,(byte) 0xdd,(byte) 0xd6,(byte) 0xe7,(byte) 0xec,(byte) 0xf1,(byte) 0xfa,(byte) 0x93,(byte) 0x98,(byte) 0x85,(byte) 0x8e,(byte) 0xbf,(byte) 0xb4,(byte) 0xa9,(byte) 0xa2,
            (byte) 0xf6,(byte) 0xfd,(byte) 0xe0,(byte) 0xeb,(byte) 0xda,(byte) 0xd1,(byte) 0xcc,(byte) 0xc7,(byte) 0xae,(byte) 0xa5,(byte) 0xb8,(byte) 0xb3,(byte) 0x82,(byte) 0x89,(byte) 0x94,(byte) 0x9f,
            (byte) 0x46,(byte) 0x4d,(byte) 0x50,(byte) 0x5b,(byte) 0x6a,(byte) 0x61,(byte) 0x7c,(byte) 0x77,(byte) 0x1e,(byte) 0x15,(byte) 0x08,(byte) 0x03,(byte) 0x32,(byte) 0x39,(byte) 0x24,(byte) 0x2f,
            (byte) 0x8d,(byte) 0x86,(byte) 0x9b,(byte) 0x90,(byte) 0xa1,(byte) 0xaa,(byte) 0xb7,(byte) 0xbc,(byte) 0xd5,(byte) 0xde,(byte) 0xc3,(byte) 0xc8,(byte) 0xf9,(byte) 0xf2,(byte) 0xef,(byte) 0xe4,
            (byte) 0x3d,(byte) 0x36,(byte) 0x2b,(byte) 0x20,(byte) 0x11,(byte) 0x1a,(byte) 0x07,(byte) 0x0c,(byte) 0x65,(byte) 0x6e,(byte) 0x73,(byte) 0x78,(byte) 0x49,(byte) 0x42,(byte) 0x5f,(byte) 0x54,
            (byte) 0xf7,(byte) 0xfc,(byte) 0xe1,(byte) 0xea,(byte) 0xdb,(byte) 0xd0,(byte) 0xcd,(byte) 0xc6,(byte) 0xaf,(byte) 0xa4,(byte) 0xb9,(byte) 0xb2,(byte) 0x83,(byte) 0x88,(byte) 0x95,(byte) 0x9e,
            (byte) 0x47,(byte) 0x4c,(byte) 0x51,(byte) 0x5a,(byte) 0x6b,(byte) 0x60,(byte) 0x7d,(byte) 0x76,(byte) 0x1f,(byte) 0x14,(byte) 0x09,(byte) 0x02,(byte) 0x33,(byte) 0x38,(byte) 0x25,(byte) 0x2e,
            (byte) 0x8c,(byte) 0x87,(byte) 0x9a,(byte) 0x91,(byte) 0xa0,(byte) 0xab,(byte) 0xb6,(byte) 0xbd,(byte) 0xd4,(byte) 0xdf,(byte) 0xc2,(byte) 0xc9,(byte) 0xf8,(byte) 0xf3,(byte) 0xee,(byte) 0xe5,
            (byte) 0x3c,(byte) 0x37,(byte) 0x2a,(byte) 0x21,(byte) 0x10,(byte) 0x1b,(byte) 0x06,(byte) 0x0d,(byte) 0x64,(byte) 0x6f,(byte) 0x72,(byte) 0x79,(byte) 0x48,(byte) 0x43,(byte) 0x5e,(byte) 0x55,
            (byte) 0x01,(byte) 0x0a,(byte) 0x17,(byte) 0x1c,(byte) 0x2d,(byte) 0x26,(byte) 0x3b,(byte) 0x30,(byte) 0x59,(byte) 0x52,(byte) 0x4f,(byte) 0x44,(byte) 0x75,(byte) 0x7e,(byte) 0x63,(byte) 0x68,
            (byte) 0xb1,(byte) 0xba,(byte) 0xa7,(byte) 0xac,(byte) 0x9d,(byte) 0x96,(byte) 0x8b,(byte) 0x80,(byte) 0xe9,(byte) 0xe2,(byte) 0xff,(byte) 0xf4,(byte) 0xc5,(byte) 0xce,(byte) 0xd3,(byte) 0xd8,
            (byte) 0x7a,(byte) 0x71,(byte) 0x6c,(byte) 0x67,(byte) 0x56,(byte) 0x5d,(byte) 0x40,(byte) 0x4b,(byte) 0x22,(byte) 0x29,(byte) 0x34,(byte) 0x3f,(byte) 0x0e,(byte) 0x05,(byte) 0x18,(byte) 0x13,
            (byte) 0xca,(byte) 0xc1,(byte) 0xdc,(byte) 0xd7,(byte) 0xe6,(byte) 0xed,(byte) 0xf0,(byte) 0xfb,(byte) 0x92,(byte) 0x99,(byte) 0x84,(byte) 0x8f,(byte) 0xbe,(byte) 0xb5,(byte) 0xa8,(byte) 0xa3
    };
    
    private static final byte[] mul13 = {
            (byte) 0x00,(byte) 0x0d,(byte) 0x1a,(byte) 0x17,(byte) 0x34,(byte) 0x39,(byte) 0x2e,(byte) 0x23,(byte) 0x68,(byte) 0x65,(byte) 0x72,(byte) 0x7f,(byte) 0x5c,(byte) 0x51,(byte) 0x46,(byte) 0x4b,
            (byte) 0xd0,(byte) 0xdd,(byte) 0xca,(byte) 0xc7,(byte) 0xe4,(byte) 0xe9,(byte) 0xfe,(byte) 0xf3,(byte) 0xb8,(byte) 0xb5,(byte) 0xa2,(byte) 0xaf,(byte) 0x8c,(byte) 0x81,(byte) 0x96,(byte) 0x9b,
            (byte) 0xbb,(byte) 0xb6,(byte) 0xa1,(byte) 0xac,(byte) 0x8f,(byte) 0x82,(byte) 0x95,(byte) 0x98,(byte) 0xd3,(byte) 0xde,(byte) 0xc9,(byte) 0xc4,(byte) 0xe7,(byte) 0xea,(byte) 0xfd,(byte) 0xf0,
            (byte) 0x6b,(byte) 0x66,(byte) 0x71,(byte) 0x7c,(byte) 0x5f,(byte) 0x52,(byte) 0x45,(byte) 0x48,(byte) 0x03,(byte) 0x0e,(byte) 0x19,(byte) 0x14,(byte) 0x37,(byte) 0x3a,(byte) 0x2d,(byte) 0x20,
            (byte) 0x6d,(byte) 0x60,(byte) 0x77,(byte) 0x7a,(byte) 0x59,(byte) 0x54,(byte) 0x43,(byte) 0x4e,(byte) 0x05,(byte) 0x08,(byte) 0x1f,(byte) 0x12,(byte) 0x31,(byte) 0x3c,(byte) 0x2b,(byte) 0x26,
            (byte) 0xbd,(byte) 0xb0,(byte) 0xa7,(byte) 0xaa,(byte) 0x89,(byte) 0x84,(byte) 0x93,(byte) 0x9e,(byte) 0xd5,(byte) 0xd8,(byte) 0xcf,(byte) 0xc2,(byte) 0xe1,(byte) 0xec,(byte) 0xfb,(byte) 0xf6,
            (byte) 0xd6,(byte) 0xdb,(byte) 0xcc,(byte) 0xc1,(byte) 0xe2,(byte) 0xef,(byte) 0xf8,(byte) 0xf5,(byte) 0xbe,(byte) 0xb3,(byte) 0xa4,(byte) 0xa9,(byte) 0x8a,(byte) 0x87,(byte) 0x90,(byte) 0x9d,
            (byte) 0x06,(byte) 0x0b,(byte) 0x1c,(byte) 0x11,(byte) 0x32,(byte) 0x3f,(byte) 0x28,(byte) 0x25,(byte) 0x6e,(byte) 0x63,(byte) 0x74,(byte) 0x79,(byte) 0x5a,(byte) 0x57,(byte) 0x40,(byte) 0x4d,
            (byte) 0xda,(byte) 0xd7,(byte) 0xc0,(byte) 0xcd,(byte) 0xee,(byte) 0xe3,(byte) 0xf4,(byte) 0xf9,(byte) 0xb2,(byte) 0xbf,(byte) 0xa8,(byte) 0xa5,(byte) 0x86,(byte) 0x8b,(byte) 0x9c,(byte) 0x91,
            (byte) 0x0a,(byte) 0x07,(byte) 0x10,(byte) 0x1d,(byte) 0x3e,(byte) 0x33,(byte) 0x24,(byte) 0x29,(byte) 0x62,(byte) 0x6f,(byte) 0x78,(byte) 0x75,(byte) 0x56,(byte) 0x5b,(byte) 0x4c,(byte) 0x41,
            (byte) 0x61,(byte) 0x6c,(byte) 0x7b,(byte) 0x76,(byte) 0x55,(byte) 0x58,(byte) 0x4f,(byte) 0x42,(byte) 0x09,(byte) 0x04,(byte) 0x13,(byte) 0x1e,(byte) 0x3d,(byte) 0x30,(byte) 0x27,(byte) 0x2a,
            (byte) 0xb1,(byte) 0xbc,(byte) 0xab,(byte) 0xa6,(byte) 0x85,(byte) 0x88,(byte) 0x9f,(byte) 0x92,(byte) 0xd9,(byte) 0xd4,(byte) 0xc3,(byte) 0xce,(byte) 0xed,(byte) 0xe0,(byte) 0xf7,(byte) 0xfa,
            (byte) 0xb7,(byte) 0xba,(byte) 0xad,(byte) 0xa0,(byte) 0x83,(byte) 0x8e,(byte) 0x99,(byte) 0x94,(byte) 0xdf,(byte) 0xd2,(byte) 0xc5,(byte) 0xc8,(byte) 0xeb,(byte) 0xe6,(byte) 0xf1,(byte) 0xfc,
            (byte) 0x67,(byte) 0x6a,(byte) 0x7d,(byte) 0x70,(byte) 0x53,(byte) 0x5e,(byte) 0x49,(byte) 0x44,(byte) 0x0f,(byte) 0x02,(byte) 0x15,(byte) 0x18,(byte) 0x3b,(byte) 0x36,(byte) 0x21,(byte) 0x2c,
            (byte) 0x0c,(byte) 0x01,(byte) 0x16,(byte) 0x1b,(byte) 0x38,(byte) 0x35,(byte) 0x22,(byte) 0x2f,(byte) 0x64,(byte) 0x69,(byte) 0x7e,(byte) 0x73,(byte) 0x50,(byte) 0x5d,(byte) 0x4a,(byte) 0x47,
            (byte) 0xdc,(byte) 0xd1,(byte) 0xc6,(byte) 0xcb,(byte) 0xe8,(byte) 0xe5,(byte) 0xf2,(byte) 0xff,(byte) 0xb4,(byte) 0xb9,(byte) 0xae,(byte) 0xa3,(byte) 0x80,(byte) 0x8d,(byte) 0x9a,(byte) 0x97
    };
    
    private static final byte[] mul14 = {
            (byte) 0x00,(byte) 0x0e,(byte) 0x1c,(byte) 0x12,(byte) 0x38,(byte) 0x36,(byte) 0x24,(byte) 0x2a,(byte) 0x70,(byte) 0x7e,(byte) 0x6c,(byte) 0x62,(byte) 0x48,(byte) 0x46,(byte) 0x54,(byte) 0x5a,
            (byte) 0xe0,(byte) 0xee,(byte) 0xfc,(byte) 0xf2,(byte) 0xd8,(byte) 0xd6,(byte) 0xc4,(byte) 0xca,(byte) 0x90,(byte) 0x9e,(byte) 0x8c,(byte) 0x82,(byte) 0xa8,(byte) 0xa6,(byte) 0xb4,(byte) 0xba,
            (byte) 0xdb,(byte) 0xd5,(byte) 0xc7,(byte) 0xc9,(byte) 0xe3,(byte) 0xed,(byte) 0xff,(byte) 0xf1,(byte) 0xab,(byte) 0xa5,(byte) 0xb7,(byte) 0xb9,(byte) 0x93,(byte) 0x9d,(byte) 0x8f,(byte) 0x81,
            (byte) 0x3b,(byte) 0x35,(byte) 0x27,(byte) 0x29,(byte) 0x03,(byte) 0x0d,(byte) 0x1f,(byte) 0x11,(byte) 0x4b,(byte) 0x45,(byte) 0x57,(byte) 0x59,(byte) 0x73,(byte) 0x7d,(byte) 0x6f,(byte) 0x61,
            (byte) 0xad,(byte) 0xa3,(byte) 0xb1,(byte) 0xbf,(byte) 0x95,(byte) 0x9b,(byte) 0x89,(byte) 0x87,(byte) 0xdd,(byte) 0xd3,(byte) 0xc1,(byte) 0xcf,(byte) 0xe5,(byte) 0xeb,(byte) 0xf9,(byte) 0xf7,
            (byte) 0x4d,(byte) 0x43,(byte) 0x51,(byte) 0x5f,(byte) 0x75,(byte) 0x7b,(byte) 0x69,(byte) 0x67,(byte) 0x3d,(byte) 0x33,(byte) 0x21,(byte) 0x2f,(byte) 0x05,(byte) 0x0b,(byte) 0x19,(byte) 0x17,
            (byte) 0x76,(byte) 0x78,(byte) 0x6a,(byte) 0x64,(byte) 0x4e,(byte) 0x40,(byte) 0x52,(byte) 0x5c,(byte) 0x06,(byte) 0x08,(byte) 0x1a,(byte) 0x14,(byte) 0x3e,(byte) 0x30,(byte) 0x22,(byte) 0x2c,
            (byte) 0x96,(byte) 0x98,(byte) 0x8a,(byte) 0x84,(byte) 0xae,(byte) 0xa0,(byte) 0xb2,(byte) 0xbc,(byte) 0xe6,(byte) 0xe8,(byte) 0xfa,(byte) 0xf4,(byte) 0xde,(byte) 0xd0,(byte) 0xc2,(byte) 0xcc,
            (byte) 0x41,(byte) 0x4f,(byte) 0x5d,(byte) 0x53,(byte) 0x79,(byte) 0x77,(byte) 0x65,(byte) 0x6b,(byte) 0x31,(byte) 0x3f,(byte) 0x2d,(byte) 0x23,(byte) 0x09,(byte) 0x07,(byte) 0x15,(byte) 0x1b,
            (byte) 0xa1,(byte) 0xaf,(byte) 0xbd,(byte) 0xb3,(byte) 0x99,(byte) 0x97,(byte) 0x85,(byte) 0x8b,(byte) 0xd1,(byte) 0xdf,(byte) 0xcd,(byte) 0xc3,(byte) 0xe9,(byte) 0xe7,(byte) 0xf5,(byte) 0xfb,
            (byte) 0x9a,(byte) 0x94,(byte) 0x86,(byte) 0x88,(byte) 0xa2,(byte) 0xac,(byte) 0xbe,(byte) 0xb0,(byte) 0xea,(byte) 0xe4,(byte) 0xf6,(byte) 0xf8,(byte) 0xd2,(byte) 0xdc,(byte) 0xce,(byte) 0xc0,
            (byte) 0x7a,(byte) 0x74,(byte) 0x66,(byte) 0x68,(byte) 0x42,(byte) 0x4c,(byte) 0x5e,(byte) 0x50,(byte) 0x0a,(byte) 0x04,(byte) 0x16,(byte) 0x18,(byte) 0x32,(byte) 0x3c,(byte) 0x2e,(byte) 0x20,
            (byte) 0xec,(byte) 0xe2,(byte) 0xf0,(byte) 0xfe,(byte) 0xd4,(byte) 0xda,(byte) 0xc8,(byte) 0xc6,(byte) 0x9c,(byte) 0x92,(byte) 0x80,(byte) 0x8e,(byte) 0xa4,(byte) 0xaa,(byte) 0xb8,(byte) 0xb6,
            (byte) 0x0c,(byte) 0x02,(byte) 0x10,(byte) 0x1e,(byte) 0x34,(byte) 0x3a,(byte) 0x28,(byte) 0x26,(byte) 0x7c,(byte) 0x72,(byte) 0x60,(byte) 0x6e,(byte) 0x44,(byte) 0x4a,(byte) 0x58,(byte) 0x56,
            (byte) 0x37,(byte) 0x39,(byte) 0x2b,(byte) 0x25,(byte) 0x0f,(byte) 0x01,(byte) 0x13,(byte) 0x1d,(byte) 0x47,(byte) 0x49,(byte) 0x5b,(byte) 0x55,(byte) 0x7f,(byte) 0x71,(byte) 0x63,(byte) 0x6d,
            (byte) 0xd7,(byte) 0xd9,(byte) 0xcb,(byte) 0xc5,(byte) 0xef,(byte) 0xe1,(byte) 0xf3,(byte) 0xfd,(byte) 0xa7,(byte) 0xa9,(byte) 0xbb,(byte) 0xb5,(byte) 0x9f,(byte) 0x91,(byte) 0x83,(byte) 0x8d
    };
    
    // cipher key
    private static byte[] K;
    
    // int is 4 bytes in Java, matches word size of 4 bytes
    private static int[] schedule;
    
    // number of 4 byte words in state
    // same as, number of columns comprising the state
    private static final int Nb = 4;
    
    // number of  4 byte words comprising cipher key, 4 or 8
    private static int Nk; 
    
    // number of rounds, 10 or 14
    private static int Nr; 
    private static byte[][] state;
    
    public AES(byte[] key, byte[] input) {
        state = new byte[4][Nb];
        
        // copy plaintext to state
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < Nb; j++) {
                state[j][i] = input[4 * i + j];
            }
        }
        
        // copy key
        K = key;
        
        
        Nk = key.length / 4;
        Nr = Nk + 6;
        
        // key expansion
        schedule = new int[Nb * (Nr + 1)];
        keyExpansion();
        
        // debug 
        // printBinary(state);
        // printBinary(K);
    }
 
    private static void addRoundKey(int round) {
        for (int c = 0; c < Nb; c++) {
            int w = schedule[4 * round + c];
            // bit manipulation to xor with correct corresponding
            // 8 bits in the word
            state[0][c] ^= (w >>> 24) & 0xff; 
            state[1][c] ^= (w >>> 16) & 0xff; // 3rd lowest 8 bits
            state[2][c] ^= (w >>> 8) & 0xff; // 2nd lowest 8 bits
            state[3][c] ^= (w) & 0xff; // lowest 8 bits
        }
    };
    
    private static void mixColumns() {  
         byte[][] tmp = new byte[4][4];
        
         tmp[0][0] = (byte)(mul2[state[0][0] & 0xff] ^ mul3[state[1][0] & 0xff] ^ state[2][0] ^ state[3][0]);
         tmp[1][0] = (byte)(state[0][0] ^ mul2[state[1][0] & 0xff] ^ mul3[state[2][0] & 0xff] ^ state[3][0]);
         tmp[2][0] = (byte)(state[0][0] ^ state[1][0] ^ mul2[state[2][0] & 0xff] ^ mul3[state[3][0] & 0xff]);
         tmp[3][0] = (byte)(mul3[state[0][0] & 0xff] ^ state[1][0] ^ state[2][0] ^ mul2[state[3][0] & 0xff]);

         tmp[0][1] = (byte)(mul2[state[0][1] & 0xff] ^ mul3[state[1][1] & 0xff] ^ state[2][1] ^ state[3][1]);
         tmp[1][1] = (byte)(state[0][1] ^ mul2[state[1][1] & 0xff] ^ mul3[state[2][1] & 0xff] ^ state[3][1]);
         tmp[2][1] = (byte)(state[0][1] ^ state[1][1] ^ mul2[state[2][1] & 0xff] ^ mul3[state[3][1] & 0xff]);
         tmp[3][1] = (byte)(mul3[state[0][1] & 0xff] ^ state[1][1] ^ state[2][1] ^ mul2[state[3][1] & 0xff]);

         tmp[0][2] = (byte)(mul2[state[0][2] & 0xff] ^ mul3[state[1][2] & 0xff] ^ state[2][2] ^ state[3][2]);
         tmp[1][2] = (byte)(state[0][2] ^ mul2[state[1][2] & 0xff] ^ mul3[state[2][2] & 0xff] ^ state[3][2]);
         tmp[2][2] = (byte)(state[0][2] ^ state[1][2] ^ mul2[state[2][2] & 0xff] ^ mul3[state[3][2] & 0xff]);
         tmp[3][2] = (byte)(mul3[state[0][2] & 0xff] ^ state[1][2] ^ state[2][2] ^ mul2[state[3][2] & 0xff]);

         tmp[0][3] = (byte)(mul2[state[0][3] & 0xff] ^ mul3[state[1][3] & 0xff] ^ state[2][3] ^ state[3][3]);
         tmp[1][3] = (byte)(state[0][3] ^ mul2[state[1][3] & 0xff] ^ mul3[state[2][3] & 0xff] ^ state[3][3]);
         tmp[2][3] = (byte)(state[0][3] ^ state[1][3] ^ mul2[state[2][3] & 0xff] ^ mul3[state[3][3] & 0xff]);
         tmp[3][3] = (byte)(mul3[state[0][3] & 0xff] ^ state[1][3] ^ state[2][3] ^ mul2[state[3][3] & 0xff]);
        
         
         for(int i = 0; i < 4; i++) {
             for(int j = 0; j < 4; j++) {
                 state[i][j] = tmp[i][j];
             }
         }
    };
    
    private static void invMixColumns() {
        byte[][] tmp = new byte[4][4];
        
        tmp[0][0] = (byte)(mul14[state[0][0] & 0xff] ^ mul11[state[1][0] & 0xff] ^ mul13[state[2][0] & 0xff] ^ mul9[state[3][0] & 0xff]);
        tmp[1][0] = (byte)(mul9[state[0][0] & 0xff] ^ mul14[state[1][0] & 0xff] ^ mul11[state[2][0] & 0xff] ^ mul13[state[3][0] & 0xff]);
        tmp[2][0] = (byte)(mul13[state[0][0] & 0xff] ^ mul9[state[1][0] & 0xff] ^ mul14[state[2][0] & 0xff] ^ mul11[state[3][0] & 0xff]);
        tmp[3][0] = (byte)(mul11[state[0][0] & 0xff] ^ mul13[state[1][0] & 0xff] ^ mul9[state[2][0] & 0xff] ^ mul14[state[3][0] & 0xff]);

        tmp[0][1] = (byte)(mul14[state[0][1] & 0xff] ^ mul11[state[1][1] & 0xff] ^ mul13[state[2][1] & 0xff] ^ mul9[state[3][1] & 0xff]);
        tmp[1][1] = (byte)(mul9[state[0][1] & 0xff] ^ mul14[state[1][1] & 0xff] ^ mul11[state[2][1] & 0xff] ^ mul13[state[3][1] & 0xff]);
        tmp[2][1] = (byte)(mul13[state[0][1] & 0xff] ^ mul9[state[1][1] & 0xff] ^ mul14[state[2][1] & 0xff] ^ mul11[state[3][1] & 0xff]);
        tmp[3][1] = (byte)(mul11[state[0][1] & 0xff] ^ mul13[state[1][1] & 0xff] ^ mul9[state[2][1] & 0xff] ^ mul14[state[3][1] & 0xff]);
        
        tmp[0][2] = (byte)(mul14[state[0][2] & 0xff] ^ mul11[state[1][2] & 0xff] ^ mul13[state[2][2] & 0xff] ^ mul9[state[3][2] & 0xff]);
        tmp[1][2] = (byte)(mul9[state[0][2] & 0xff] ^ mul14[state[1][2] & 0xff] ^ mul11[state[2][2] & 0xff] ^ mul13[state[3][2] & 0xff]);
        tmp[2][2] = (byte)(mul13[state[0][2] & 0xff] ^ mul9[state[1][2] & 0xff] ^ mul14[state[2][2] & 0xff] ^ mul11[state[3][2] & 0xff]);
        tmp[3][2] = (byte)(mul11[state[0][2] & 0xff] ^ mul13[state[1][2] & 0xff] ^ mul9[state[2][2] & 0xff] ^ mul14[state[3][2] & 0xff]);
        
        tmp[0][3] = (byte)(mul14[state[0][3] & 0xff] ^ mul11[state[1][3] & 0xff] ^ mul13[state[2][3] & 0xff] ^ mul9[state[3][3] & 0xff]);
        tmp[1][3] = (byte)(mul9[state[0][3] & 0xff] ^ mul14[state[1][3] & 0xff] ^ mul11[state[2][3] & 0xff] ^ mul13[state[3][3] & 0xff]);
        tmp[2][3] = (byte)(mul13[state[0][3] & 0xff] ^ mul9[state[1][3] & 0xff] ^ mul14[state[2][3] & 0xff] ^ mul11[state[3][3] & 0xff]);
        tmp[3][3] = (byte)(mul11[state[0][3] & 0xff] ^ mul13[state[1][3] & 0xff] ^ mul9[state[2][3] & 0xff] ^ mul14[state[3][3] & 0xff]);
        
        // copy back to state
        for(int i = 0; i < 4; i++) {
             for(int j = 0; j < 4; j++) {
                 state[i][j] = tmp[i][j];
             }
         }
    };
    
    private static void invShiftRows() {
            byte[][] tmp = new byte[4][4];      

            // R0 no change
            tmp[0][0] = state[0][0];
            tmp[0][1] = state[0][1];
            tmp[0][2] = state[0][2];
            tmp[0][3] = state[0][3];
            
            // R1 1 right
            tmp[1][0] = state[1][3];
            tmp[1][1] = state[1][0];
            tmp[1][2] = state[1][1];
            tmp[1][3] = state[1][2];
            
            // R2 2 right
            tmp[2][0] = state[2][2];
            tmp[2][1] = state[2][3];
            tmp[2][2] = state[2][0];
            tmp[2][3] = state[2][1];
                    
            // R3 3 right
            tmp[3][0] = state[3][1];
            tmp[3][1] = state[3][2];
            tmp[3][2] = state[3][3];
            tmp[3][3] = state[3][0];
            
            // copy back to state
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    state[i][j] = tmp[i][j];
                }
            }
    };
    
    private static void invSubBytes() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                state[j][i] = invSbox[state[j][i] & 0xff];
            }
        };
    }
    
    private static int rotWord(int i) {
        return Integer.rotateLeft(i, 8);
    };
    
    private static void shiftRows() {
            byte[][] tmp = new byte[4][4];
            
            // R0
            tmp[0][0] = state[0][0];
            tmp[0][1] = state[0][1];
            tmp[0][2] = state[0][2];
            tmp[0][3] = state[0][3];
            
            // R1
            tmp[1][0] = state[1][1];
            tmp[1][1] = state[1][2];
            tmp[1][2] = state[1][3];
            tmp[1][3] = state[1][0];
            
            // R2
            tmp[2][0] = state[2][2];
            tmp[2][1] = state[2][3];
            tmp[2][2] = state[2][0];
            tmp[2][3] = state[2][1];
                    
            // R3
            tmp[3][0] = state[3][3];
            tmp[3][1] = state[3][0];
            tmp[3][2] = state[3][1];
            tmp[3][3] = state[3][2];
            
            // copy back
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    state[i][j] = tmp[i][j];
                }
            }
    };
     
    private static void subBytes() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                state[j][i] = sbox[state[j][i] & 0xff];
            }
        }
    };

    private static int subWord(int i) {
        // bit shifts are used to manipulate each 8 bits in the
        // 32 bit word and then place them correctly back into the
        // word once modified
        return ((sbox[(i >>> 24) & 0xff]) << 24) |
                ((sbox[(i >>> 16) & 0xff] & 0xff) << 16) |
                ((sbox[(i >>> 8) & 0xff] & 0xff) << 8) |
                ((sbox[(i) & 0xff] & 0xff));
    };
    
    private static void keyExpansion() {
        // each element in schedule is a 4 byte word (using int)
        // copy over original key
        // going from byte array to 4 byte int
        // bits must be shifted to properly place bits into
        // the word
        for (int i = 0, k = 0; i < Nk; i++, k += 4) {
            schedule[i] =
                    // build 4 byte word from original key
                    ((K[k]) << 24) |
                            ((K[k + 1] & 0xff) << 16) | // 3rd lowest 8 bits
                            ((K[k + 2] & 0xff) << 8) | // 2nd lowest 8 bits
                            ((K[k + 3] & 0xff)); // lowest 8 bits
        }
        
        int[] rcon = new int[11];
        
        // must shift to the left to xor
        // since value x^{i-1} must be in 
        // the left most 8 bits of a 32 bit word
        rcon[1] = (byte) 0x01 << 24;
        rcon[2] = (byte) 0x02 << 24;
        rcon[3] = (byte) 0x04 << 24;
        rcon[4] = (byte) 0x08 << 24;
        rcon[5] = (byte) 0x10 << 24;
        rcon[6] = (byte) 0x20 << 24;
        rcon[7] = (byte) 0x40 << 24;
        rcon[8] = (byte) 0x80 << 24;
        rcon[9] = (byte) 0x1b << 24;
        rcon[10] = (byte) 0x36 << 24;
        
        int temp;
        
        for (int i = Nk; i < Nb * (Nr + 1); i++) {
            // use previous word in schedule
            temp = schedule[i - 1];
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp));
                temp ^= rcon[i / Nk];
                // 256 bit key expansion is slightly different 
                // than 128 bit key expansion
            } else if (Nk > 6 && i % Nk == 4) {
                temp = subWord(temp);
            }
            schedule[i] = schedule[i - Nk] ^ temp;
        }
        // printBinary(schedule); // debugging step
    }
    
    private static byte[][] encrypt() {
        addRoundKey(0);
        
        for(int round = 1; round < Nr; round++) {
            subBytes();
            shiftRows();
            mixColumns();
            addRoundKey(round);
        }
        
        subBytes();
        shiftRows();
        addRoundKey(Nr);
        
        printBinary(state);
        return(state);
    }
    
    private byte[][] decrypt() {
        addRoundKey(Nr);
        
        for (int round = 1; round < Nr; round++) {
            invShiftRows();
            invSubBytes();
            addRoundKey(Nr - round);
            invMixColumns();
        }
        invShiftRows();
        invSubBytes();
        addRoundKey(0);
        
        printBinary(state);
        return state;
    }
    
    private static void printBinary(int[] i) {
        for(int x : i) {
            byte[] bytes = ByteBuffer.allocate(4).putInt(x).array();
            
            for(int j = 0; j < bytes.length; j++) {
                if(j % 15 == 0) out.println("\n");
                System.out.format("%02x", bytes[j]);
            }
        }
    }
    
    private static void printBinary(byte[][] input) {
        int len = input.length * input[0].length;
        byte[] tmp = new byte[len];
        
        int counter = 0;
        for(int i = 0; i < input.length; i++) {
            for(int j = 0; j < input.length; j++) {
                tmp[counter++] = input[j][i];
            }
        }
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(tmp));
    }
    
    private static void printBinary(byte[] input) {
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(input));
    }
    
    // Zero length Padding. Pad with zero except the last byte which is equal to the length of the padding
     // 123456789
     // 123456789000006
     private static byte[] padInput(byte[] input) {
         out.println("Input before padding: " + Arrays.toString(input)); // debugging step
         byte[] res = new byte[input.length + (16 - input.length % 16)];
         for (int i = 0; i < input.length; i++) { 
             res[i] = input[i]; 
         }
         for (int i = input.length; i < 15; i++) {
             res[i] = (byte) 0;
         }
         res[15] = (byte) (16 - input.length);
         out.println("Padded input is: " + Arrays.toString(res)); // debugging step
         return res;
     }

    private static byte[] loadKey(String filename, int keyBytes) throws Exception {
        byte[] key = new byte[keyBytes]; // assign to dummy byte array
        try {
            FileInputStream file = new FileInputStream(filename);
            int size = file.available();
            if (size != keyBytes) {
                throw new Exception("Keyfile does match with Keysize.");
            }
            for(int i = 0; i < size; i++) {
                key[i] = (byte) file.read();
            }
        } catch (IOException ex) {
            out.printf("IO Exception %s\n", ex);
        }
        return key;
    }

    private static void writeOutput(FileOutputStream fileStream, byte[][] result) {
        try {
            // FileOutputStream file = new FileOutputStream(filename);
            int counter = 0;
            for(int i = 0; i < result.length; i++) {
                for(int j = 0; j < result.length; j++) {
                    fileStream.write(result[j][i]);
                }
            }
        } catch (IOException ex) {
            out.printf("IO Exception %s\n", ex);
        }
        
    }
    
    public static void main(String[] args) throws IOException {
        int keysize = Integer.parseInt(args[1]);
        if (! (keysize == 128 || keysize == 256)) { 
            out.println("Invalid Keysize.");
            return;
        }
        String keyfile = args[3];
        String inputfile = args[5];
        String outputfile = args[7];
        String mode = args[9];
        byte[] key = new byte[16];
        // We assume that the key is 128 or 256 bits long
        try {
            key = loadKey(keyfile, keysize / 8);
        } catch (Exception ex) {
            out.println("Keysize and length of Keyfile do not match up.");
            return;
        }
        

        try {
            FileInputStream fileInStream = new FileInputStream(inputfile);
            FileOutputStream fileOutStream = new FileOutputStream(outputfile);
            byte[] buffer = new byte[16];
            int c = 0; 
            AES aes;
            byte[][] result;
            
            // Read 16 bytes at a time if you can
            // https://docs.oracle.com/javase/tutorial/essential/io/bytestreams.html
            while (fileInStream.available() >= 16) {
                fileInStream.read(buffer);
                aes = new AES(key, buffer);
                if (mode.toLowerCase().equals("encrypt")) {
                    result = aes.encrypt();
                    writeOutput(fileOutStream, result);
                } else if (mode.toLowerCase().equals("decrypt")) {
                    result = aes.decrypt();
                    writeOutput(fileOutStream, result);
                }
            }

            int size = fileInStream.available();
            // Padding here
            // Enter if there are still bytes left in the file to encrpypt/decrypt
            if (size > 0) { 
                byte[] tail = new byte[size];
                for (int i = 0; i < size; i++) {
                    tail[i] = (byte) fileInStream.read();
                }
                aes = new AES(key, padInput(tail));
                if (mode.toLowerCase().equals("encrypt")) {
                    result = aes.encrypt();
                    writeOutput(fileOutStream, result);
                } else if (mode.toLowerCase().equals("decrypt")) {
                    result = aes.decrypt();
                    writeOutput(fileOutStream, result);
                }
            }
        } catch (IOException ex) {
            out.printf("IO Exception %s\n", ex);
        }
    }      
}