package mycode_2;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.Console;
import java.lang.reflect.Array;

import org.w3c.dom.TypeInfo;

public class AES128_192_256 {
	// create S-box
	public String[] sBox;
	String code_text = "";

	// ----------------------------------------
	public static int[] createValue() {
		BigInteger shift = new BigInteger("67483244"); // Shift value
		long timeUNIX = new Date().getTime() / 1000;
		System.out.println("So dich chuyen: " + shift);
		System.out.println("Unix Time: " + timeUNIX);
		BigInteger time = new BigInteger(String.valueOf(timeUNIX));

		long resultGet16FirstNumber = getFirst16Number(shift.multiply(time));
		String resultInsertZeros = insertZeros(resultGet16FirstNumber);
		System.out.println("16 chu so kep: " + resultInsertZeros);
		int[] valueInArray = convertToArray(resultInsertZeros, 2);

		return valueInArray;
	}

	public static int multiply(int time, int shift) {
		return time * shift;
	}

	public static long getFirst16Number(BigInteger number) {
		String string = number.toString();
		if (string.length() == 16 || string.length() > 16)
			return Long.parseLong(string.substring(0, 16));
		else
			return Long.parseLong(String.format("%-16s", string).replace(' ', '0'));
	}

	public static String insertZeros(long number) {
		String numberString = Long.toString(number);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < numberString.length(); i++) {
			result.append(numberString.charAt(i)).append("0");
		}
		while (result.length() < 32) {
			result.append("0");
		}
		return result.toString();
	}

	public static int[] convertToArray(String chain, int length) {
		List<Integer> byteArray = new ArrayList<>();
		for (int i = 0; i < chain.length(); i += length) {
			int byteValue = Integer.parseInt(chain.substring(i, i + length));
			byteArray.add(byteValue);
		}

		int[] resultArray = new int[byteArray.size()];
		for (int i = 0; i < byteArray.size(); i++) {
			resultArray[i] = byteArray.get(i);
		}

		return resultArray;
	}

	public static void showArrInt(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
	}

	public static void showArrStr(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
			if (((i + 1) % 16) == 0) {
				System.out.println();
			}
		}
	}

	public static void showArrStr2D(String[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static int shuffle(ArrayList<Integer> usedArray, int index, String text) {
		if (usedArray.contains(index)) {
			// If index is already present in usedArray,
			// find the next available index
			int nextIndex = index;
			while (usedArray.contains(nextIndex)) {
				nextIndex = (nextIndex + 1) % 16;
			}
			// System.out.println("chi so " + text + ": " + nextIndex);
			return nextIndex;
		} else {
			// If index is not present in usedArray, return it as it is
			// System.out.println("chi so " + text + ": " + index);
			return index;
		}
	}

	public static class IndexResult {
		private int rowIndex;
		private int columnIndex;

		public IndexResult(int rowIndex, int columnIndex) {
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}

		public int getRowIndex() {
			return rowIndex;
		}

		public int getColumnIndex() {
			return columnIndex;
		}
	}

	public static IndexResult GetProperIndex(int valueInArray, ArrayList<Integer> rowUsedArray,
			ArrayList<Integer> columnUsedArray) {
		int rowIndex = valueInArray & 0x0F;
		int columnIndex = valueInArray >> 4;

		rowIndex = shuffle(rowUsedArray, rowIndex, "row");
		columnIndex = shuffle(columnUsedArray, columnIndex, "col");

		rowUsedArray.add(rowIndex);
		columnUsedArray.add(columnIndex);

		return new IndexResult(rowIndex, columnIndex);
	}

	public static void shiftRow(int rowIndex, int shiftCount, String[][] sBox) {
		shiftCount = shiftCount % 15;
		String[] row = sBox[rowIndex];

		String[] shiftedRow = shiftRight(row, shiftCount);
		sBox[rowIndex] = shiftedRow;
	}

	public static String[] shiftRight(String[] arr, int count) {
		int length = arr.length;
		String[] shiftedArr = new String[length];

		// Perform the right shift count times
		for (int i = 0; i < count; i++) {
			String lastElement = arr[length - 1];

			// Shift elements to the right
			for (int j = length - 1; j > 0; j--) {
				arr[j] = arr[j - 1];
			}

			arr[0] = lastElement;
		}

		System.arraycopy(arr, 0, shiftedArr, 0, length);
		return shiftedArr;
	}

	public static void shiftColumn(int columnIndex, int shiftCount, String[][] sBoxAES) {
		shiftCount = shiftCount % 15;
		int numRows = sBoxAES.length;

		for (int i = 0; i < shiftCount; i++) {
			String lastElement = sBoxAES[numRows - 1][columnIndex];

			for (int j = numRows - 1; j > 0; j--) {
				sBoxAES[j][columnIndex] = sBoxAES[j - 1][columnIndex];
			}

			sBoxAES[0][columnIndex] = lastElement;
		}
	}

	public static void swap(int rowIndex, int columnIndex, String[][] sBoxAes) {
		for (int i = 0; i < 16; i++) {
			String temp = sBoxAes[i][columnIndex];
			sBoxAes[i][columnIndex] = sBoxAes[rowIndex][i];
			sBoxAes[rowIndex][i] = temp;
		}
	}

	public static String[] convertTo1DArray(String[][] array2D) {
		int rows = array2D.length;
		int cols = array2D[0].length;
		String[] array1D = new String[rows * cols];

		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				array1D[index++] = array2D[i][j];
			}
		}

		return array1D;
	}

	public static int getShiftCount(int[] valueInArray) {
		int customizingFactor = 0x00;
		int shiftCount = 0;
		for (int i = 0; i < 16; i++) {
			int decimalValue = valueInArray[i];
			shiftCount ^= (decimalValue * (i + 1)) % (0xFF + 1);
		}
		return shiftCount ^ customizingFactor;
	}

	public static String[][] convertTo2DArray(String[] arr, int length) {
		String[][] array2D = new String[16][16];
		int index = 0;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				array2D[i][j] = arr[index];
				index++;
			}
		}

		return array2D;
	}

	// --------------------------------------------------------
	private static void printSbox(String[] a) {
		int row = a.length;
		int index = 0;
		String s = "";
		for (int i = 0; i < row; i++) {
			s += a[i] + " ";
			index++;
			if (index == 16) {
				System.out.println(s);
				index = 0;
				s = "";
			}
		}
	}

	// -------------Create Dynamic Sbox-------
	public static String[] GenerateDynamicSbox(String[] sBox) {
		int[] valueInArray = createValue();
		String[][] sBoxAES = convertTo2DArray(sBox, 16);
		ArrayList<Integer> rowUsedArray = new ArrayList<>();
		ArrayList<Integer> columnUsedArray = new ArrayList<>();
		int shiftCount = getShiftCount(valueInArray);
		for (int i = 0; i < 16; i++) {
			IndexResult obj = GetProperIndex(valueInArray[i], rowUsedArray, columnUsedArray);
			int rowIndex = obj.rowIndex;
			int columnIndex = obj.columnIndex;
			shiftRow(rowIndex, shiftCount, sBoxAES);
			shiftColumn(columnIndex, shiftCount, sBoxAES);
			swap(rowIndex, columnIndex, sBoxAES);
		}
		String[] newSBox = convertTo1DArray(sBoxAES);
		return newSBox;
	}

	// --------------------AES----------------------
	int Nk;
	int Nr;
	int Nb = 4;
	String state[][];
	String[] key_expansion;

	public static void print(String[][] a) {
		int rows = a.length;
		int cols = a[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.print(a[i][j] + " ");
			}
		}

	}

	public void outPut() {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				System.out.print(state[c][r]);
				code_text += state[c][r];
			}
		}
		System.out.println();

	}

	public void outPutState() {
		System.out.println("State");
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				System.out.print(state[c][r]);
			}
			System.out.println();
		}
	}

	private String[] Rcon = { "8D000000", "01000000", "02000000", "04000000", "08000000", "10000000", "20000000",
			"40000000", "80000000", "1B000000", "36000000" };

	// -----------------------------create invSBox from
	// SBox-----------------------------

	private static String[] InitInvSBox(String[] sBox) {
		String[] invSBox = new String[16 * 16];
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				String t = sBox[i * 16 + j];
				int temp = Integer.parseInt(t, 16);
				int x = temp >> 4;
				int y = temp & 0x0F;
				invSBox[x * 16 + y] = String.format("%02X", (i << 4) + j);
			}
		}

		return invSBox;
	}

	// ***************** HELPER AES FUNCTIONS *****************
	public String[] splitStringToArray(String input) {
		String[] array = new String[input.length() / 2];

		for (int i = 0; i < array.length; i++) {
			array[i] = input.substring(2 * i, 2 * i + 2);
		}
		return array;
	}

	public String mergeStringFromArray(String[] input) {
		String str = "";
		for (int i = 0; i < input.length; i++) {
			str += input[i];
		}
		return str;
	}

	// Number to String Byte
	private String hString(int num) {
		num = num & 0xff;
		String fmt = String.format("%02X", num);
		return fmt;
	}

	// -------------------multiplication overGF(2^8)-----------------
	// -------------------0x02 * a----------------------
	public static int gfMultiplicationBy02(int i) {
		i = i & 0xff;
		if (i < 0x80) {
			i = (i << 1);
		} else {
			i = (i << 1);
			i = (i ^ 0x1b);
		}
		return i;
	}

	// -------------------0x03 * a----------------------
	public static int gfMultiplicationBy03(int i) {
		return (gfMultiplicationBy02(i) ^ i);
	}

	/*
	 * 0x09 * a = (a * 0x02 * 0x02 * 0x02 ) + (a * 0x01)
	 */

	public static int gfMultiplicationBy09(int i) {
		return (gfMultiplicationBy02(gfMultiplicationBy02(gfMultiplicationBy02(i))) ^ i);
	}

	/*
	 * 0x0b * a = (a * 0x02 * 0x02 * 0x02 ) + (a * 0x02) + (a * 0x01)
	 */

	public static int gfMultiplicationBy0b(int i) {
		return (gfMultiplicationBy02(gfMultiplicationBy02(gfMultiplicationBy02(i))) ^ gfMultiplicationBy02(i) ^ i);
	}

	/*
	 * 0x0d * a = (a * 0x02 * 0x02 * 0x02 ) + (a * 0x02 * 0x02 ) + (a * 0x01)
	 */

	public static int gfMultiplicationBy0d(int i) {
		return (gfMultiplicationBy02(gfMultiplicationBy02(gfMultiplicationBy02(i)))
				^ gfMultiplicationBy02(gfMultiplicationBy02(i)) ^ i);
	}

	/*
	 * 0x0e * a = (a * 0x02 * 0x02 * 0x02 ) + (a * 0x02 * 0x02 ) + (a * 0x02)
	 */

	public static int gfMultiplicationBy0e(int i) {
		return (gfMultiplicationBy02(gfMultiplicationBy02(gfMultiplicationBy02(i)))
				^ gfMultiplicationBy02(gfMultiplicationBy02(i)) ^ gfMultiplicationBy02(i));
	}

	public AES128_192_256() {
		state = new String[4][4];
	}

	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ---Method --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	// ------------------------ KeyExpansion------------
	private void keyExpansion(String Key, int numRound, String[] sBox) {
		key_expansion = new String[4 * Nb * (numRound + 1)];
		for (int i = 0; i < Nk; i++) {
			key_expansion[i] = Key.substring(8 * i, 8 * i + 2) + Key.substring(8 * i + 2, (8 * i + 2) + 2)
					+ Key.substring(8 * i + 4, (8 * i + 4) + 2) + Key.substring(8 * i + 6, (8 * i + 6) + 2);
		}

		for (int i = Nk; i < Nb * (numRound + 1); i++) {
			String temp = key_expansion[i - 1];
			if (i % Nk == 0) {
				BigInteger RconValue = new BigInteger(Rcon[i / Nk], 16);
				String rot_word = rotWord(temp);
				String sub_word = subWord(rot_word, sBox);
				temp = String.format("%08X", new BigInteger(sub_word, 16).xor(RconValue));
			} else if (Nk > 6 && i % Nk == 4) {
				temp = subWord(temp, sBox);
			} else {
				//
			}
			key_expansion[i] = String.format("%08X",
					new BigInteger(key_expansion[i - Nk], 16).xor(new BigInteger(temp, 16)));
		}

	}

	// -------------------------------- AddRoundKey----------------------
	private void AddRoundKey(int round) {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				int word = Integer.parseInt(key_expansion[round * 4 + c].substring(2 * r, 2 * r + 2), 16);
				int st = Integer.parseInt(state[r][c], 16);
				int res = st ^ word;
				state[r][c] = String.format("%02X", res);
			}
		}
	}

	// -------------------------------- SubByte ---------------------------------
	// use for encrypt
	private void subBytes(String[] sBox) {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				state[r][c] = sBox[Integer.parseInt(state[r][c], 16)];
			}
		}
	}

	// use for decrypt
	private void invSubBytes(String[] invSBox) {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				state[r][c] = invSBox[Integer.parseInt(state[r][c], 16)];
			}
		}
	}

	// -------------------------------- ShiftRows ---------------------------------
	// use for encrypt
	public void shiftRows() {

		String stateNew[][] = new String[state.length][state[0].length];
		stateNew[0] = state[0];
		for (int r = 1; r < state.length; r++) {
			for (int c = 0; c < state[r].length; c++) {
				stateNew[r][c] = state[r][(c + r) % Nb];
			}
		}
		for (int r = 0; r < state.length; r++) {
			for (int c = 0; c < state[r].length; c++) {
				state[r][c] = stateNew[r][c];
			}
		}
	}

	// use for decrypt
	public void invShiftRows() {
		String stateNew[][] = new String[state.length][state[0].length];
		// r = 0 is not shifted
		stateNew[0] = state[0];
		for (int r = 1; r < state.length; r++) {
			for (int c = 0; c < state[r].length; c++) {
				stateNew[r][(c + r) % Nb] = state[r][c];
			}
		}
		for (int r = 0; r < state.length; r++) {
			for (int c = 0; c < state[r].length; c++) {
				state[r][c] = stateNew[r][c];
			}
		}
	}

	// -------------------------------- MixColumns ---------------------------------
	// use for encrypt
	private void mixColumns() {
		int[][] temp = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[i][j] = Integer.parseInt(state[i][j], 16);
			}
		}

		for (int c = 0; c < 4; c++) {
			state[0][c] = hString(
					gfMultiplicationBy02(temp[0][c]) ^ gfMultiplicationBy03(temp[1][c]) ^ temp[2][c] ^ temp[3][c]);

			state[1][c] = hString(
					temp[0][c] ^ gfMultiplicationBy02(temp[1][c]) ^ gfMultiplicationBy03(temp[2][c]) ^ temp[3][c]);

			state[2][c] = hString(
					temp[0][c] ^ temp[1][c] ^ gfMultiplicationBy02(temp[2][c]) ^ gfMultiplicationBy03(temp[3][c]));

			state[3][c] = hString(
					gfMultiplicationBy03(temp[0][c]) ^ temp[1][c] ^ temp[2][c] ^ gfMultiplicationBy02(temp[3][c]));
		}
	}

	// use for decrypt
	private void invMixColumns() {
		int[][] temp = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				temp[i][j] = Integer.parseInt(state[i][j], 16);
			}
		}
		for (int c = 0; c < 4; c++) {
			state[0][c] = hString(gfMultiplicationBy0e(temp[0][c]) ^ gfMultiplicationBy0b(temp[1][c])
					^ gfMultiplicationBy0d(temp[2][c]) ^ gfMultiplicationBy09(temp[3][c]));

			state[1][c] = hString(gfMultiplicationBy09(temp[0][c]) ^ gfMultiplicationBy0e(temp[1][c])
					^ gfMultiplicationBy0b(temp[2][c]) ^ gfMultiplicationBy0d(temp[3][c]));

			state[2][c] = hString(gfMultiplicationBy0d(temp[0][c]) ^ gfMultiplicationBy09(temp[1][c])
					^ gfMultiplicationBy0e(temp[2][c]) ^ gfMultiplicationBy0b(temp[3][c]));

			state[3][c] = hString(gfMultiplicationBy0b(temp[0][c]) ^ gfMultiplicationBy0d(temp[1][c])
					^ gfMultiplicationBy09(temp[2][c]) ^ gfMultiplicationBy0e(temp[3][c]));
		}
	}

	// -------------------------------- RotWord ---------------------------------
	// shift left 1 byte
	private String rotWord(String input) {
		String[] arrayinput = splitStringToArray(input);
		String temp = arrayinput[0];
		for (int i = 0; i < 3; i++) {
			arrayinput[i] = arrayinput[i + 1];
		}
		arrayinput[3] = temp;
		return mergeStringFromArray(arrayinput);
	}

	// -------------------------------- SubWord ---------------------------------
	// thay the qua Sbox. thay the 4 byte
	private String subWord(String input, String[] sBox) {
		String[] arrayinput = splitStringToArray(input);
		for (int i = 0; i < 4; i++) {
			arrayinput[i] = sBox[Integer.parseInt(arrayinput[i], 16)];
		}
		return mergeStringFromArray(arrayinput);
	}

	// ******************************** encryption***********************

	public String[][] encryptAES(String plain_text, String key, String[] sBoxInit) {
		int index = 0;
		int length = plain_text.length();
		String[][] listSBox = new String[Nr][];

		while (plain_text != "") {
			if (plain_text.length() < 32) {
				for (int i = 0; i < (32 - plain_text.length()); i++) {
					plain_text += " ";
				}
			}
			state = new String[4][4];
			for (int i = 0; i < 16; i++) {
				state[i % 4][i / 4] = plain_text.substring(2 * i, 2 * i + 2);
				index += 2;
			}
			outPutState();

			// Khởi tạo sBox
			String[] sBox = GenerateDynamicSbox(sBoxInit);
			// Key Schedule
			keyExpansion(key, Nr, sBox);
			// begin Round
			AddRoundKey(0);
			// PrintStateArray(0, "Encryption");
			for (int round = 1; round <= Nr - 1; round++) {
				// Khởi tạo sBox khi i != 1
				if (round != 1) {
					sBox = GenerateDynamicSbox(sBox);
				}
				System.out.println("s-box vong thu " + round);
				printSbox(sBox);

				subBytes(sBox);
				shiftRows();
				mixColumns();
				AddRoundKey(round);

				// Thêm sBox vào listSBox
				listSBox[round - 1] = sBox;
				outPutState();
			}
			// Khởi tạo sBox
			sBox = GenerateDynamicSbox(sBox);

			// final Round
			System.out.println("s-box vong thu " + Nr);
			printSbox(sBox);
			subBytes(sBox);
			shiftRows();
			AddRoundKey(Nr);
			outPutState();
			// output
			outPut();
			// Thêm sBox vào listSBox
			listSBox[Nr - 1] = sBox;
			String temp = "";
			if (index < length) {
				temp = plain_text.substring(index, length + 1);
			}
			plain_text = temp;
		}
		return listSBox;
	}

	// *********************************** decryption **************************
	public void InvAES_Cipher(String coded_text, String key, String[][] listSBox) {
		int indexSBox = Nr - 1;
		String[] invSBox;
		int index = 0;
		int length = coded_text.length();
		while (coded_text != "") {
			if (coded_text.length() < 32) {
				for (int i = 0; i < (32 - coded_text.length()); i++) {
					coded_text += " ";
				}
			}
			state = new String[4][4];
			for (int i = 0; i < 16; i++) {
				state[i % 4][i / 4] = coded_text.substring(2 * i, 2 * i + 2);
				index += 2;
			}
			outPutState();

			keyExpansion(key, Nr, listSBox[0]);
			AddRoundKey(Nr);
			for (int round = Nr - 1; round >= 1; round--) {
				invSBox = InitInvSBox(listSBox[indexSBox]);

				System.out.println("Vong thu " + (round + 1));
				printSbox(invSBox);
				outPutState();

				invShiftRows();
				invSubBytes(invSBox);
				AddRoundKey(round);
				invMixColumns();
				indexSBox--;
			}
			invSBox = InitInvSBox(listSBox[0]);
			invShiftRows();
			invSubBytes(invSBox);
			AddRoundKey(0);

			System.out.println("Vong thu " + 1);
			printSbox(invSBox);
			outPutState();
			outPut();
			String temp = "";
			if (index < length) {
				temp = coded_text.substring(index, length + 1);
			}
			coded_text = temp;
		}
	}

	String[] invSBox = new String[16 * 16];

	// main
	public static void main(String[] args) {
		// String key = "A00102030405060708090A0B0C0D0E0F";
		String key = "A00102030405060708090A0B0C0D0E0F984AB7683BCD6738984AB7683BCD6738";
		String plaint_text = "023456789012345678901234567890AB";
		String[][] listSBox = new String[10][];

		AES128_192_256 test_aes = new AES128_192_256();
		String[] sBox = {
				"63", "7c", "77", "7b", "f2", "6b", "6f", "c5", "30", "01", "67", "2b", "fe", "d7", "ab", "76",
				"ca", "82", "c9", "7d", "fa", "59", "47", "f0", "ad", "d4", "a2", "af", "9c", "a4", "72", "c0", "b7",
				"fd", "93", "26", "36", "3f", "f7", "cc", "34", "a5", "e5", "f1", "71", "d8", "31", "15", "04", "c7",
				"23", "c3", "18", "96", "05", "9a", "07", "12", "80", "e2", "eb", "27", "b2", "75", "09", "83", "2c",
				"1a", "1b", "6e", "5a", "a0", "52", "3b", "d6", "b3", "29", "e3", "2f", "84", "53", "d1", "00", "ed",
				"20", "fc", "b1", "5b", "6a", "cb", "be", "39", "4a", "4c", "58", "cf", "d0", "ef", "aa", "fb", "43",
				"4d", "33", "85", "45", "f9", "02", "7f", "50", "3c", "9f", "a8", "51", "a3", "40", "8f", "92", "9d",
				"38", "f5", "bc", "b6", "da", "21", "10", "ff", "f3", "d2", "cd", "0c", "13", "ec", "5f", "97", "44",
				"17", "c4", "a7", "7e", "3d", "64", "5d", "19", "73", "60", "81", "4f", "dc", "22", "2a", "90", "88",
				"46", "ee", "b8", "14", "de", "5e", "0b", "db", "e0", "32", "3a", "0a", "49", "06", "24", "5c", "c2",
				"d3", "ac", "62", "91", "95", "e4", "79", "e7", "c8", "37", "6d", "8d", "d5", "4e", "a9", "6c", "56",
				"f4", "ea", "65", "7a", "ae", "08", "ba", "78", "25", "2e", "1c", "a6", "b4", "c6", "e8", "dd", "74",
				"1f", "4b", "bd", "8b", "8a", "70", "3e", "b5", "66", "48", "03", "f6", "0e", "61", "35", "57", "b9",
				"86", "c1", "1d", "9e", "e1", "f8", "98", "11", "69", "d9", "8e", "94", "9b", "1e", "87", "e9", "ce",
				"55", "28", "df", "8c", "a1", "89", "0d", "bf", "e6", "42", "68", "41", "99", "2d", "0f", "b0", "54",
				"bb", "16" };
		boolean check_legal_key = true;
		if ((key.length() == 32)) {
			test_aes.Nk = 4;
			test_aes.Nr = 10;
		} else if ((key.length() == 48)) {
			test_aes.Nk = 6;
			test_aes.Nr = 12;
		} else if ((key.length() == 64)) {
			test_aes.Nk = 8;
			test_aes.Nr = 14;
		} else {
			check_legal_key = false;
		}
		if (check_legal_key) {
			System.out.println("khoa: " + key);
			System.out.println("Plaint text: " + plaint_text);
			System.out.println("So vong: " + test_aes.Nr);
			System.out.println("--------------------encryption---------------------");
			// test_aes.sBox = newSbox;
			listSBox = test_aes.encryptAES(plaint_text, key, sBox);
			System.out.println("--------------------decryption---------------------");
			test_aes.InvAES_Cipher(test_aes.code_text, key, listSBox);
		} else {
			System.out.println("length of key not legal, please input another key");
		}

	}
}
