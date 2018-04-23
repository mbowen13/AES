# AES

## Introduction
This program takes in a 128 or 256 bit key and a 16 byte plaintext or ciphertext byte file. Depending on the key size, the plaintext/ciphertext will be encrypted/decrypted with AES128 or AES256. The output will be an encrypted ciphertext or decrypted plaintext.

## How To Run

## Implementation

The following is a brief explanation of our implementation. For more information please read the inline comments in src/AES.java.

### Variables:
- **K** - the cipher key given in args.
- **Nk** - the number of 4 byte words comprising the cipher key. 4 for 128bit key and 8 for 256bit key.
- **Nb** - the number of 4 byte words in the state. 
- **Nr** - the number of rounds for encryption/decryption. 10 for 128 and 14 for 256.
- **Schedule** - the expanded key. An array of 4 byte words that has (Nb * (Nr +1)) elements. 
- **State** - a 4x4 array of bytes that is used as an intermediary to encrypt the plaintext with the expanded key.
- **Round Key** - values derived from the cipher key using the key expansion routine. They are applied to the State in the Encryption and Decryption methods.
- **Sbox** - non-linear substitution table used in several byte substitution
transformations and in the Key Expansion routine to perform a onefor-one
substitution of a byte value. Used in Encryption method. 
- **InvSbox** - Sbox run in reverse. Used in Decryption method.
- **Mul tables** - lookup tables used to perform multiplication between polynomials.
- **Rcon** - round constant that is simply 2^{i} such that i is Nk<= i < Nb * (Nr +1).



### Methods:
- **keyExpansion()**: 
- **rotWord()**:
- **subWord()**:

- **addRoundKey()**: transformation in the Cipher and Inverse Cipher in which a Round Key is added to the State using an XOR operation. The length of a
Round Key equals the size of the State (i.e., for Nb = 4, the Round
Key length equals 128 bits/16 bytes). 

- **subBytes()**: transformation in the Cipher that processes the State using a nonlinear byte substitution table (S-box) that operates on each of the
State bytes independently. 

- **shiftRows()**: transformation in the Cipher that processes the State by cyclically shifting the last three rows of the State by different offsets.

- **mixColumns()**: transformation operates on the State column-by-column, treating each column as a four-term polynomial as described in Sec. 4.3. The columns are considered as polynomials over GF(2^8) and multiplied modulo 
x^4 + 1 with a fixed polynomial a(x). Multiplication tables are used here to prevent extra calculations.

- **inbSubBytes()**:
- **invShiftRows()**:
- **invMixColumns()**:


- **encrypt()**:
- **decrypt()**:



### Bit Manipulation
Bit manipulation was used to place bits into the 4 word bytes and return correct values from the lookup tables when converting bytes to an int. 

In many areas, 0xff was used to mask in order to get the first 8 bits. 

Example: 
```
int i  = 5 << 24 | (1 & 0xff) << 16 | (0 & 0xff) << 8 | (7 & 0xff);
```
- i >>> 24 & 0xff, gives first 8 bits, here it is 7.
- i & 0xff, gives last 8 bits, here it is 5.


### Mathematics
#### Addition 
Finite field elements can be described as modulo 2 addition of corresponding bits in the byte. 
XOR is the sum of 2 bits, modulo 2 addition. 

(x^6 + x^4 + x^2 + x + 1) + (x^7 + x +1) = x^7 + x^6 + x^4 + x^2 (polynomial notation);

{01010111} ^ {10000011} = {11010100} (binary notation);

### Multiplication
To improve efficiency, we used multiplication tables mul2, mul3, mul9, mul11, mul13, and mul14 for multiplying polynomials. This way we didn't need to do any extra calculations.

