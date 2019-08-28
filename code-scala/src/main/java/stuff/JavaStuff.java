package stuff;

public class JavaStuff {

    static Integer one = 0;

    public static Integer addOne(Integer i) {
        one++;

        if (one == 3) {
            return null;
        }
        else if (one == 4) {
            throw new IllegalStateException();
        }
        else {
            return i + one;
        }
    }

}
