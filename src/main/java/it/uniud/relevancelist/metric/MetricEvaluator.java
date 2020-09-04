package it.uniud.relevancelist.metric;

import it.uniud.relevancelist.solution.RLBinarySolution;

public abstract class MetricEvaluator {

	//returns the evaluation of the metric for the solution
	//doesn't modify solution status
	public abstract double evaluate(RLBinarySolution solution);
	
}
