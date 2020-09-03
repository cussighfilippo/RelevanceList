package it.uniud.relevancelist.metric;

import it.uniud.relevancelist.solution.RLBinarySolution;

public abstract class MetricEvaluator {
	
	public abstract double evaluate(RLBinarySolution solution);
	
}
