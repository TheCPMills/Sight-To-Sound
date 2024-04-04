package test;
public class Tests
 {
    public static void main(String[] args) {
        System.out.println(test(6, 2)); // 0 - place around middle

        System.out.println(test(5, 2)); // 1 - place around middle

        System.out.println(test(6, 3)); // 2 - nothing you can do about it

        System.out.println(test(5, 3)); // 3 - place at middle
    }

    public static int test(int divisions, int remainder) {
        return (remainder % 2 * 2) + (divisions % 2);
    }

    public static boolean test2(int i, int divisions) {
        boolean a = i % 2 == divisions % 2;
        boolean b = (i + divisions) % 2 == 0;
        return a == b;
    }
}
