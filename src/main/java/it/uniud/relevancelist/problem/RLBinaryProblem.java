package it.uniud.relevancelist.problem;

import java.util.ArrayList;

import java.util.List;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.util.binarySet.BinarySet;

import it.uniud.relevancelist.solution.RLBinarySolution;
import it.uniud.relevancelist.solution.RLBinarySolutionFactory;


public class RLBinaryProblem extends AbstractGenericProblem<RLBinarySolution> {
	
	public static final int nVariables = 1;
	public static final int nObjectives = 1;
	public static final int nCostraints = 1;

	private double targetValue;
	private EvaluationFunction evalFun;
	private int relevantDocs;
	private int listLength;
	private RLBinarySolutionFactory factory;

	public RLBinaryProblem(double targetValue, EvaluationFunction evalFun, RLBinarySolutionFactory fac) {
		this.targetValue = targetValue;
		this.evalFun = evalFun;
		this.relevantDocs = fac.getRelevantDocs();
		this.listLength = fac.getListLength();
		this.factory = fac;

	}

	// evaluates the solution and updates its objective and constraint values  
	@Override
	public void evaluate(RLBinarySolution solution) {
		double actualValue = 100;

		switch (evalFun) {
		case avgPrecision:
			actualValue = avgPrecision(solution);
			break;
		default:
			System.err.print("invalid evaluation function");
			System.exit(1);
		}

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

	// returns the avgPrecision of the solution
	// the solution's objective values are not modified by the method
	private double avgPrecision(RLBinarySolution solution) {
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
	
}
