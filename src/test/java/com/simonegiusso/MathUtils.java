package com.simonegiusso;

import java.util.*;

final class MathUtils {

    private MathUtils() { }

    static double logBase2(double number) {
        return StrictMath.log(number) / StrictMath.log(2);
    }

    static <T> int max(int[] numbers) {
        return Arrays.stream(numbers).max().getAsInt();
    }

}
