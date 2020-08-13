package it.uniud.relevancelist.problem;

import java.util.ArrayList;

import java.util.List;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;

import it.uniud.relevancelist.program.Program.EvaluationFunction;

public class RLBinaryProblem extends AbstractBinaryProblem {
	
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

	@Override
	public void evaluate(BinarySolution solution) {

		// double actualValue = avg01Metric(solutionArray);
		double actualValue = 100;

		switch (evalFun) {
		case avgPrecision:
			int bitSetLength = solution.getVariable(0).getBinarySetLength();
			boolean[] solutionArray = new boolean[bitSetLength];
			for (int i = 0; i < bitSetLength; i++)
				solutionArray[i] = solution.getVariable(0).get(i);
			actualValue = avgPrecisionMetric(solution.getVariable(0));
			break;
		default:
		}

		solution.setObjective(0, Math.abs(actualValue - targetValue));
		evaluateConstraints(solution);
	}

	private void evaluateConstraints(BinarySolution sol) {
		double constraint;
		BinarySet topics = sol.getVariable(0);
		int numberOfSelectedTopics = 0;
		for (int i = 0; i < topics.getBinarySetLength(); i++)  if (topics.get(i)) numberOfSelectedTopics++;
		constraint = relevantDocs - numberOfSelectedTopics;
		sol.setConstraint(0, constraint);
	}

	private double avgPrecisionMetric(BinarySet  bitSet) {
		double returnValue = 0;
		int nOnes = 0;
		for (int i = 0; i < bitSet.getBinarySetLength(); i++) {
			if (bitSet.get(i)) {
				nOnes++;
				returnValue = returnValue + ((double) nOnes / (i + 1));
			}
		}
		double returnVal = returnValue / relevantDocs;
		return returnVal;
	}

	@Override
	public List<Integer> getListOfBitsPerVariable() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < getNumberOfVariables(); i++)
			list.add(listLength);
		return list;
	}
	
	@Override
	public BinarySolution createSolution() {
		return factory.generateNewSolution();
	}
	
	public RLBinarySolutionFactory getFactory() {
		return factory;
	}
	
}
