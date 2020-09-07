package it.uniud.relevancelist.problem;

import java.util.ArrayList;

import java.util.List;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.util.binarySet.BinarySet;

import it.uniud.relevancelist.metric.MetricEvaluator;
import it.uniud.relevancelist.solution.RLBinarySolution;
import it.uniud.relevancelist.solution.RLBinarySolutionFactory;


public class RLBinaryProblem extends AbstractGenericProblem<RLBinarySolution> {
	

	private double targetValue;
	private MetricEvaluator evaluator;
	private int relevantDocs;
	private int listLength;
	private RLBinarySolutionFactory factory;

	public RLBinaryProblem(double targetValue, MetricEvaluator eval, RLBinarySolutionFactory fac) {
		this.targetValue = targetValue;
		this.evaluator = eval;
		this.relevantDocs = fac.getRelevantDocs();
		this.listLength = fac.getListLength();
		this.factory = fac;

		this.setNumberOfConstraints(fac.getnConstraints());
		this.setNumberOfVariables(fac.getnVariables());
		this.setNumberOfObjectives(fac.getnObjectives());
	}

	// evaluates the solution and updates its objective and constraint values  
	@Override
	public void evaluate(RLBinarySolution solution) {
		double actualValue = 100;
	    actualValue = evaluator.evaluate(solution);
		solution.setObjective(0, Math.abs(actualValue - targetValue));
		evaluateConstraints(solution);
	}

	// constraint evaluation of RLBinarySolution
	// solution's relevant documents cannot exceed the fixated value of the problem
	// the constraint violation causes the solution to be dominated in the Pareto front calculation
	private void evaluateConstraints(RLBinarySolution sol) {
		double constraint;
		int numberOfRelevantDocs = sol.getNumberOfRelevantDocs();
		constraint = relevantDocs - numberOfRelevantDocs;
		sol.setConstraint(0, constraint);
	}


	public List<Integer> getListOfBitsPerVariable() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < getNumberOfVariables(); i++)
			list.add(listLength);
		return list;
	}
	
	public RLBinarySolution createSolution() {
		return factory.generateNewSolution();
	}
	
	public RLBinarySolutionFactory getFactory() {
		return factory;
	}

	public double getRelevantDocs() {
		return relevantDocs;
	}
	
}
