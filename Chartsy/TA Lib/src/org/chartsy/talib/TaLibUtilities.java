/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.talib;

/**
 *
 * @author joshua.taylor
 */
public class TaLibUtilities {
    private TaLibUtilities(){}

    public static double[] fixOutputArray(double[] outArray, int lookback){
        double tempOutput[] = new double[outArray.length];
        /*for (int i = 0; i < tempOutput.length; i++) {
            if(i<lookback)
                tempOutput[i] = 0.0;
        }*/
        System.arraycopy(outArray, 0, tempOutput, lookback, outArray.length-lookback);

        return tempOutput;
    }

    public static double[] fixOutputArray(int[] outArray, int lookback){
        double tempOutput[] = new double[outArray.length];
        int j = 0;
        for (int i = 0; i < tempOutput.length; i++) {
            if(i<lookback)
                tempOutput[i] = 0.0;
            if(i>=lookback)
                tempOutput[i] = (double)outArray[j++];
        }
        
        return tempOutput;
    }

    public static void showOutputArray(double[] outputArray){
        System.out.println("The output array is as follows:");
        for (int i = 0; i < outputArray.length; i++) {
            System.out.println("outputArray[" + i + "]= " + outputArray[i]);
        }
    }

    public static void showLastElementsOfOutputArray(double[] outputArray, int numberOfElements){
        System.out.println("The last " + numberOfElements + " elements of the output array are as follows:");
        for (int i = 0; i < outputArray.length; i++) {
            if(i > outputArray.length - 1 - numberOfElements)
                System.out.println("outputArray[" + i + "]= " + outputArray[i]);
        }
    }

    public static void showArraysTogether(double[] input, double[] output){
    	System.out.println("\nHere's what the input and output arrays look like together:");
    	System.out.println("Input \tIndicator (output)");
    	for (int i = 0; i < output.length; i++) {
            System.out.println(input[i] + ",\t " + output[i]);
        }
    }

    public static void showLastElementsOfArraysTogether(double[] input, double[] output, int numberOfElements){
    	System.out.println("The last " + numberOfElements + " elements of both arrays are as follows:");
    	System.out.println("Input \tIndicator (output)");
        for (int i = 0; i < output.length; i++) {
            if(i > output.length - 1 - numberOfElements)
            	System.out.println(input[i] + ",\t " + output[i]);
        }
    }
}
