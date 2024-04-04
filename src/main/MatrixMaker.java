package main;
public class MatrixMaker {
    public static void main(String[] args) {
        int[] arr = {3, 3, 3, 3, 3};
        int[][] matrix = arrayToMatrix(arr);
        int[][] transpose = transposeMatrix(matrix);

        int[][] otherMatrix = arrayToMatrix(new int[]{3, 2, 3, 3});

        int[][] mul = matrixMultiply(transpose, otherMatrix);

        System.out.println(java.util.Arrays.deepToString(matrix));
        System.out.println(java.util.Arrays.deepToString(transpose));
        System.out.println(java.util.Arrays.deepToString(mul));
    }
    
    public static int[][] arrayToMatrix(int[] arr) {
        return new int[][] { arr };
    }


    public static int[][] transposeMatrix(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int[][] transposedMatrix = new int[n][m];

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                transposedMatrix[x][y] = matrix[y][x];
            }
        }

        return transposedMatrix;
    }

    public static int[][] matrixMultiply(int[][] widthsMatrix, int[][] heightsMatrix) {
        int A[][] = new int[widthsMatrix.length][heightsMatrix[0].length];
        
        for (int i = 0; i < widthsMatrix.length; i++) {
            for (int j = 0; j < heightsMatrix[0].length; j++) {
                for (int k = 0; k < heightsMatrix.length; k++)
                    A[i][j] += widthsMatrix[i][k] * heightsMatrix[k][j];
            }
        }

        return A;
    }
}
