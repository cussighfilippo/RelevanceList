package it.uniud.relevancelist.metric;

import org.uma.jmetal.util.binarySet.BinarySet;
import it.uniud.relevancelist.solution.RLBinarySolution;

public class AveragePrecisionEvaluator extends MetricEvaluator{
	
	int relevantDocs;

	public AveragePrecisionEvaluator (int relDocs) {
		relevantDocs = relDocs;
	}

	//returns average precision of the given solution without changing its objective value
	public double evaluate(RLBinarySolution solution) {
		BinarySet bitSet = solution.getVariable(0);
		double returnValue = 0;
		int nOnes = 0;
		for (int i = 0; i < bitSet.getBinarySetLength(); i++) {
			if (bitSet.get(i)) {
				nOnes++;
				returnValue = returnValue + ((double) nOnes / (i + 1));
			}
		} 
		return returnValue / relevantDocs; 
	}
}
