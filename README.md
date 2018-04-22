# AES

## Introduction
This program takes in a 128 or 256 bit key and a plaintext or ciphertext byte file of up to 16 bytes. Depending on the key size, the plaintext/ciphertext will be encrypted/decrypted with AES128 or AES256. The output will be an encrypted ciphertext/decrypted plaintext.

## How To Run

## Implementation

The following is a brief explanation of our implementation. For more information please read the inline comments in src/AES.java.

### Variables:
- **K** - the cipher key given in args.
- **Nk** - the number of 4 byte words comprising the cipher key. 4 for 128bit key and 8 for 256bit key.
- **Nb** - the number of 4 byte words in the state. 
- **Nr** - The number of rounds for encryption/decryption. 10 for 128 and 14 for 256.
- **Schedule** - the expanded key. An array of 4 byte words that has (Nb * (Nr +1)) elements. 
- **State** - a 4x4 array of bytes that is used as an intermediary to encrypt the plaintext with the expanded key.
- **Round Key** - values derived from the cipher key using the key expansion routine. They are applied to the State in the Encryption and Decryption methods.
- **Sbox** - Non-linear substitution table used in several byte substitution
transformations and in the Key Expansion routine to perform a onefor-one
substitution of a byte value. Used in Encryption method. 
- **InvSbox** - Sbox run in reverse. Used in Decryption method.
- **Mul tables** - Lookup tables used to perform multiplication between Galoi Fields.
- **Rcon** - 



### Methods:
- **addRoundKey()**:
