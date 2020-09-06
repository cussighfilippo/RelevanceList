package it.uniud.relevancelist.metric;

import java.util.Arrays;

import it.uniud.relevancelist.solution.RLBinarySolution;

public class NDCGEvaluator extends MetricEvaluator{

	public NDCGEvaluator(){}
	
	private double ngdc(double[] array) {
		 double dcg = 0;
		  for(int i=0; i<array.length; i++){
			  dcg = dcg + ((array[i])/ (Math.log(i + 2) / Math.log(2)));
		  }
		  Arrays.sort(array);
		  double idcg = 0;
		  for(int i=(array.length-1); i>=0; i--){
			  int indVal = (array.length-1) - i;
			  idcg = idcg + ((array[i])/ (Math.log(indVal + 2) / Math.log(2)));
		  }	  
		  if(idcg == 0){
			  idcg = 1;
		  }
		  double returnVal = dcg / idcg;
		  return returnVal;
	}

	@Override
	public double evaluate(RLBinarySolution solution) {
		double[] a = new double[solution.getVariable(0).getBinarySetLength()];
		for(int i=0; i<a.length; i++) {
			if(solution.getVariable(0).get(i)) a[i] = 1;
			else a[i] = 0;
		}
		return ngdc(a);
	}

}
