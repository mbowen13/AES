# AES
Claire Dubiel, Tin La, Michael Bowen

## Introduction
This program takes in a 128 or 256 bit key and a 16 byte plaintext or ciphertext byte file. Depending on the key size, the plaintext/ciphertext will be encrypted/decrypted with AES128 or AES256. The output will be an encrypted ciphertext or decrypted plaintext.

## How To Run
```
Directory structure

src/
	AES.java
	encrypt/
		128/
			KEYFILE
			TESTFILE
			OUTFILENAME
		256/
			KEYFILE
			TESTFILE
			OUTFILENAME
	decrypt
		128/
			KEYFILE
			TESTFILE
			OUTFILENAME
		256/
			KEYFILE
			TESTFILE
			OUTFILENAME

1. Compile
cd src
javac --add-modules java.xml.bind AES.java
(--add-modules flag if it complains about module package without the flag, otherwise remove --add-modules flag the command)



2. Pick one 
a. encrypt 128
java --add-modules java.xml.bind AES --keysize 128 --keyfile encrypt/128/KEYFILE --inputfile encrypt/128/INPUTFILE --outputfile encrypt/128/OUTFILENAME --mode ENCRYPT

b. encrypt 256
java --add-modules java.xml.bind AES --keysize 256 --keyfile encrypt/256/KEYFILE --inputfile encrypt/256/INPUTFILE --outputfile encrypt/256/OUTFILENAME --mode ENCRYPT

c. decrypt 128
java --add-modules java.xml.bind AES --keysize 128 --keyfile decrypt/128/KEYFILE --inputfile decrypt/128/INPUTFILE --outputfile decrypt/128/OUTFILENAME --mode DECRYPT

d. decrypt 256
java --add-modules java.xml.bind AES --keysize 256 --keyfile decrypt/256/KEYFILE --inputfile decrypt/256/INPUTFILE --outputfile decrypt/256/OUTFILENAME --mode DECRYPT
```

## Implementation

The following is a brief explanation of our implementation. For more information please read the inline comments in src/AES.java.


### Algorithm
AES is an iterated symmetric block cipher, which means that:
- AES works by repeating the same defined steps multiple times.
- AES is a secret key encryption algorithm.
- AES operates on a fixed number of bytes

AES uses repeat cycles or "rounds". There are 10, 12, or 14 rounds for keys of 128, 192, and 256 bits,
respectively, for both encryption and decryption. In our implementation we only had to handle 128 and 256 bit key sizes.

The input text is represented as a 4 x 4 array of bytes. 

The key is represented as a 4 x n array of bytes, where n depends on the key size.


#### Encryption
Each round of the encryption algorithm consists of four steps:

- subBytes: for each byte in the array, use its value as an index into a fixed 256-element lookup table, Sbox,
and replace its value in the state by the byte value stored at that location in the table. 

- shiftRows: Let Ri denote the ith row in state. Shift R0 in the state left 0 bytes (i.e., no change); shift
R1 left 1 byte; shift R2 left 2 bytes; shift R3 left 3 bytes. These are circular shifts. They do not affect
the individual byte values themselves.

- mixColumns: for each column of the state, replace the column by its value multiplied by a fixed 4 x
4 matrix of integers (in a particular Galois Field). We used lookup tables mul2 and mul3 here.

- addRoundkey: XOR the state with a 128-bit round key derived from the original key K by a recursive
process.

#### Decryption
Each round of the decryption algorithm consists of four steps:

- invSubBytes: for each byte in the array, use its value as an index into a fixed 256-element lookup table, which is Sbox reversed, and replace its value in the state by the byte value stored at that location in the table. 

- invShiftRows: Let Ri denote the ith row in state. Shift R0 in the state right 0 bytes (i.e., no change); shift
R1 right 1 byte; shift R2 right 2 bytes; shift R3 right 3 bytes. These are circular shifts. They do not affect
the individual byte values themselves.

- invMixColumns: for each column of the state, replace the column by its value multiplied by a fixed 4 x
4 matrix of integers (in a particular Galois Field). We used lookup tables mul9, mul11, mul13 and mul14 here.

- addRoundkey: XOR the state with a 128-bit round key derived from the original key K by a recursive
process.

#### Key Expansion
Each time the Add Round Key function is called a different part of the expanded key is XORed against the state. In order
for this to work the Expanded Key must be large enough so that it can provide key material for every time the Add Round
Key function is executed. The Add Round Key function gets called for each round as well as one extra time at the
beginning of the algorithm.

Therefore the size of the expanded key will always be equal to:
16 * (number of rounds + 1) bytes

The key expansion routine executes a maximum of 4 consecutive functions per round. These functions are:
- ROT WORD
- SUB WORD
- RCON
- EK <- values from the Expanded Key
- K <- values from the original Key

The first bytes of the expanded key are always equal to the key. If the key is 16 bytes long the first 16 bytes of the
expanded key will be the same as the original key. If the key size is 32 bytes then the first 32 bytes of the expanded key
will be the same as the original key.

Each round adds 4 bytes to the Expanded Key. With the exception of the first rounds each round also takes the previous
rounds 4 bytes as input operates and returns 4 bytes.

One more important note is that not all of the 4 functions are always called in each round. The algorithm only calls all 4 ofthe functions every:
4 Rounds for a 16 byte Key
8 Rounds for a 32 byte Key


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
- **keyExpansion()**: creates an expanded key from the original key
- **rotWord()**: this does a circular shift on 4 bytes similar to the Shift Row Function.
- **subWord()**: this step applies the S-box value substitution as described in Bytes Sub function to each of the 4 bytes in
the argument.

- **addRoundKey()**: transformation in the Cipher and Inverse Cipher in which a Round Key is added to the State using an XOR operation. The length of a
Round Key equals the size of the State (i.e., for Nb = 4, the Round
Key length equals 128 bits/16 bytes). 

- **subBytes()**: transformation on the State using a nonlinear byte substitution table (S-box) that operates on each of the
State bytes independently. 

- **shiftRows()**: transformation on the State by cyclically shifting the last three rows of the State by different offsets.

- **mixColumns()**: transformation operates on the State column-by-column, treating each column as a four-term polynomial. Multiplication tables are used here to prevent extra calculations.

- **inbSubBytes()**: transformation on the State using the reverse of a nonlinear byte substitution table (inverse S-box) that operates on each of the
State bytes independently. 
- **invShiftRows()**: transformation on the State by cyclically shifting the last three rows of the State by different offsets in the opposite direction of shiftRows(). 
- **invMixColumns()**: transformation operates on the State column-by-column, treating each column as a four-term polynomial. Multiplication tables are used here to prevent extra calculations.


- **encrypt()**: encrypt a plaintext of arbitrary size with a key of 128 or 256 bits, padding when necessary.
- **decrypt()**: decrypt a ciphertext of arbitrary size with a key of 128 or 256 bits.



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

#### Multiplication
To improve efficiency, we used multiplication tables mul2, mul3, mul9, mul11, mul13, and mul14 for multiplying polynomials. This way we didn't need to do any extra calculations.

## Sources
[AES Simplified](https://www.ime.usp.br/~rt/cranalysis/AESSimplified.pdf)

[AES Standard](https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf)

