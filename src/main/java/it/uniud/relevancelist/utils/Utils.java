package it.uniud.relevancelist.utils;

public class Utils {
	
	public static double getMinimum(double[] array, int numRelevantValues) {
	    double min = Double.MAX_VALUE;
	    for(int i=0; i<numRelevantValues; i++){
	    	double element = array[i];
	        if(element < min){
	        	min = element;
	        }
	    }
	    return min;
	}	
	
	
	
	
	public static double getAverage(double[] array, int numRelevantValues, int validSolutionsCount) {
	    double total = 0;
	    for(int i=0; i<numRelevantValues; i++){
	    	double element = array[i];
	    	if(element != 10000000){
	    		total += element;
	    	}
	    }
	    double average = total / validSolutionsCount;
	    return average;
	}
	
	
	
	public static double getStddev(double[] array, double average, int numRelevantValues, int validSolutionsCount){
		double sd = 0;
		for (int i=0; i<numRelevantValues; i++){
			if(array[i] != 10000000){
				sd += Math.pow(array[i] - average, 2) / validSolutionsCount;
			}
		}
		return Math.sqrt(sd);
	}
}
