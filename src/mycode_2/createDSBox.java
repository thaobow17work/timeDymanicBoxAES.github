package mycode_2;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.Console;
import java.lang.reflect.Array;
import java.math.BigInteger;

public class createDSBox {
    public static int[] createValue() {
        BigInteger shift = new BigInteger("67483244"); // Shift value
        long timeUNIX = new Date().getTime() / 1000;
        System.out.println("Unix time: " + timeUNIX);
        System.out.println("So dich chuyen: " + shift);
        BigInteger time = new BigInteger(String.valueOf(timeUNIX));

        // int resultMultiply = multiply(time, shift);
        // System.out.println(time * shift);
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
        System.out.println("mang so hang da dung: ");
        System.out.println(rowUsedArray);
        System.out.println("mang so cot da dung: ");
        System.out.println(columnUsedArray);

        int rowIndex = valueInArray & 0x0F;
        int columnIndex = valueInArray >> 4;
        System.out.println("index hang khoi tao: " + rowIndex);
        System.out.println("index cot khoi tao: " + columnIndex);
        if (rowUsedArray.contains(rowIndex)) {
            int nextRowIndex = rowIndex;
            while (rowUsedArray.contains(nextRowIndex)) {
                nextRowIndex = (nextRowIndex + 1) % 16;
            }
            rowIndex = nextRowIndex;
        }

        if (columnUsedArray.contains(columnIndex)) {
            int nextColumnIndex = columnIndex;
            while (columnUsedArray.contains(nextColumnIndex)) {
                nextColumnIndex = (nextColumnIndex + 1) % 16;
            }
            columnIndex = nextColumnIndex;
        }
        System.out.println("index hang ket qua: " + rowIndex);
        System.out.println("index cot ket qua: " + columnIndex);

        rowUsedArray.add(rowIndex);
        columnUsedArray.add(columnIndex);

        return new IndexResult(rowIndex, columnIndex);
    }

    public static void shiftRow(int rowIndex, int shiftCount, String[][] sBox) {
        shiftCount = shiftCount % 15;
        String[] row = sBox[rowIndex];
        System.out.println("hang " + rowIndex + " truoc khi dich");
        showArrStr(row);

        String[] shiftedRow = shiftRight(row, shiftCount);
        System.out.println("hang " + rowIndex + " sau khi dich");
        showArrStr(shiftedRow);

        sBox[rowIndex] = shiftedRow;
        System.out.println("sbox sau khi dich hang " + rowIndex);
        showArrStr2D(sBox);
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
        int numRows = sBoxAES.length;
        shiftCount = shiftCount % 15;

        for (int i = 0; i < shiftCount; i++) {
            String lastElement = sBoxAES[numRows - 1][columnIndex];

            for (int j = numRows - 1; j > 0; j--) {
                sBoxAES[j][columnIndex] = sBoxAES[j - 1][columnIndex];
            }

            sBoxAES[0][columnIndex] = lastElement;
        }
        System.out.println("sbox sau khi dich cot " + columnIndex);
        showArrStr2D(sBoxAES);
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

    public static String[] GenerateDynamicSbox(String[] sBox) {
        int[] valueInArray = createValue();
        String[][] sBoxAES = convertTo2DArray(sBox, 16);
        System.out.println("Sbox goc: ");
        showArrStr2D(sBoxAES);
        ArrayList<Integer> rowUsedArray = new ArrayList<>();
        ArrayList<Integer> columnUsedArray = new ArrayList<>();
        int shiftCount = getShiftCount(valueInArray);
        System.out.println("So luong phep dich chuyen: " + shiftCount);

        for (int i = 0; i < 2; i++) {
            System.out.println("Lay 1 chu so kep: " + valueInArray[i]);
            IndexResult obj = GetProperIndex(valueInArray[i], rowUsedArray, columnUsedArray);
            int rowIndex = obj.rowIndex;
            int columnIndex = obj.columnIndex;
            shiftRow(rowIndex, shiftCount, sBoxAES);

            shiftColumn(columnIndex, shiftCount, sBoxAES);

            swap(rowIndex, columnIndex, sBoxAES);
            System.out.println("sbox sau khi hoan vi");
            showArrStr2D(sBoxAES);
            System.out.println("---------");
        }
        // System.out.println("row:" + rowUsedArray);
        // System.out.println("column:" + columnUsedArray);
        String[] newSBox = convertTo1DArray(sBoxAES);
        return newSBox;
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

    // --------------------------------------------------------------------
    // -----------------SBOX--------------------
    public static void main(String[] args) {
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
        String[] newSBox = GenerateDynamicSbox(sBox);
        System.out.println("-----SBOX----");
        showArrStr(newSBox);
        System.out.println("-------Inv_SBOX1-----");
        String[] invSBox = InitInvSBox(newSBox);
        showArrStr(invSBox);

        // for (int i = 1; i <= 10; i++) {
        // System.out.println("-----SBOX" + i + "----");
        // newSBox = GenerateDynamicSbox(newSBox);
        // showArrStr(newSBox);
        // System.out.println("----Inv_SBOX" + i + "---");
        // invSBox = InitInvSBox(newSBox);
        // showArrStr(invSBox);
        // }
    }
}
