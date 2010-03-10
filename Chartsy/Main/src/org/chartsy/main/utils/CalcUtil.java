package org.chartsy.main.utils;

import org.chartsy.main.dataset.Dataset;

/**
 *
 * @author viorel.gheba
 */
public class CalcUtil {

    private CalcUtil() {}

    public static double stdDev(Dataset dataset, String price, int curr, int per) {
        double med = 0;
        double sum = 0;

        for (int i = curr; i > curr - per && i > 0; i--) {
            if (dataset.getPriceValue(i, price) != 0) {
                double val = dataset.getPriceValue(i, price);
                med += val;
                sum += val * val;
            }
        }

        med = med / per;
        sum = sum / per;

        double result = Math.sqrt(sum - med * med);
        return result;
    }

}
