package foo;

public class spliceUtil {
    // Example usage:
    //
    // int[] numbers = {1, 2, 3, 4, 5, 6, 7};
    // int[][] chunks = chunkArray(numbers, 3);
    //
    // chunks now contains [
    //                         [1, 2, 3],
    //                         [4, 5, 6],
    //                         [7]
    //                     ]
//    public static <T> T[][] chunkArray(T[] array, int chunkSize) {
//        int numOfChunks = (array.length + chunkSize - 1) / chunkSize;
//        T[][] output = new T()[numOfChunks][];
//
//        for (int i = 0; i < numOfChunks; ++i) {
//            int start = i * chunkSize;
//            int length = Math.min(array.length - start, chunkSize);
//
//            int[] temp = new int[length];
//            System.arraycopy(array, start, temp, 0, length);
//            output[i] = temp;
//        }
//
//        return output;
//    }

    public static byte[][] chunkArray(byte[] array, int chunkSize) {
        int numOfChunks = getChunkNum(array, chunkSize);
        byte[][] output = new byte[numOfChunks][];

        for (int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            byte[] temp = new byte[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }
        return output;
    }

    public static int getChunkNum(byte[] array, int chunkSize) {
        return (array.length + chunkSize - 1) / chunkSize;
    }
}
