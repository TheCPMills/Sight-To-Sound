package images;
public class Segmenter {
    public static void main(String[] args) {
        int sizes[] = { 16, 32, 30, 35, 38, 46, 32, 29, 52 };
        int divisions[] = { 8, 6, 7, 8, 7, 8, 7, 6, 9 };

        /**
         * 16 / 8 = 2 R0 [2, 2, 2, 2, 2, 2, 2, 2]
         * 
         * 32 / 6 = 5 R2 [5, 5, 6, 6, 5, 5] 30 / 7 = 4 R2 [4, 4, 5, 4, 5, 4, 4] 35 / 8 =
         * 4 R3 [4, 4, 3, 3, 3, 4, 4, 4] 38 / 7 = 5 R3 [5, 5, 6, 6, 6, 5, 5]
         * 
         * 46 / 8 = 5 R6 [6, 6, 6, 5, 5, 6, 6, 6] 32 / 7 = 4 R4 [5, 5, 4, 4, 4, 5, 5] 29
         * / 6 = 4 R5 [5, 5, 4, 5, 5, 5] 52 / 9 = 5 R7 [6, 6, 6, 5, 6, 5, 6, 6, 6]
         */

        for (int i = 0; i < sizes.length; i++) {
            System.out.printf("Size: %d, Divisions: %d, Segments: %s\n", sizes[i], divisions[i],
                    java.util.Arrays.toString(segment(sizes[i], divisions[i])));
        }
    }

    public static int[] segment(int size, int divisions) {
        int[] segments = new int[divisions];
        int quotient = size / divisions;
        int remainder = size % divisions;

        int i;
        for (i = 0; i < divisions; i++) {
            segments[i] = quotient;
        }

        while (remainder-- != 0) {
            segments[--i]++;
        }

        i = divisions - i;

        if (i != 0) {
            int a = (i % 2 * 2) + (divisions % 2);
            palindromize(segments, a, i > divisions / 2);
        }

        return segments;
    }

    public static void palindromize(int[] segments, int version, boolean largerOnOutside) {
        int firstDifferent = findFirstDifferent(segments);

        int middle = (segments.length) / 2;

        int sizeOfRemainder = segments.length - firstDifferent;
        int index;

        switch (version) {
            case 0:
                if (largerOnOutside) {
                    index = middle + (firstDifferent-- / -2);
                    for (; firstDifferent >= 0; firstDifferent--) {
                        int temp = segments[firstDifferent];
                        segments[firstDifferent] = segments[index];
                        segments[index++] = temp;
                    }
                } else {
                    index = middle + (sizeOfRemainder / -2);
                    int end = middle + (sizeOfRemainder / 2);
                    for (; index < end; index++) {
                        int temp = segments[firstDifferent];
                        segments[firstDifferent++] = segments[index];
                        segments[index] = temp;
                    }
                }
                break;
            case 1:
                if (largerOnOutside) {
                    index = middle + (firstDifferent-- / -2);
                    for (; firstDifferent >= 0; firstDifferent--) {
                        int temp = segments[firstDifferent];
                        segments[firstDifferent] = segments[index];
                        segments[index++] = temp;
                    }
                } else {
                    index = middle + (sizeOfRemainder / -2);
                    int end = middle + (sizeOfRemainder / 2) + 1;
                    for (; index < end; index++) {
                        if (index == middle) {
                            index++;
                        }
                        int temp = segments[firstDifferent];
                        segments[firstDifferent++] = segments[index];
                        segments[index] = temp;
                    }
                }
                break;
            case 2:
                if (largerOnOutside) {
                    index = middle - 1 + (firstDifferent-- / -2);
                    for (; firstDifferent >= 0; firstDifferent--) {
                        int temp = segments[firstDifferent];
                        segments[firstDifferent] = segments[index];
                        segments[index++] = temp;
                    }
                } else {
                    index = middle - 1 + (sizeOfRemainder / -2);
                    int end = middle + (sizeOfRemainder / 2);
                    for (; index < end; index++) {
                        int temp = segments[firstDifferent];
                        segments[firstDifferent++] = segments[index];
                        segments[index] = temp;
                    }
                }
                break;
            case 3:
                if (largerOnOutside) {
                    index = middle + (firstDifferent-- / -2);
                    for (; firstDifferent >= 0; firstDifferent--) {
                        if (index == middle) {
                            index++;
                        }
                        int temp = segments[firstDifferent];
                        segments[firstDifferent] = segments[index];
                        segments[index++] = temp;
                    }
                } else {
                    index = middle + (sizeOfRemainder / -2);
                    int end = middle + (sizeOfRemainder / 2) + 1;
                    for (; index < end; index++) {
                        int temp = segments[firstDifferent];
                        segments[firstDifferent++] = segments[index];
                        segments[index] = temp;
                    }
                }
                break;
        }
    }

    private static int findFirstDifferent(int[] arr) {
        int ref = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] != ref) {
                return i;
            }
        }
        return -1;
    }
}