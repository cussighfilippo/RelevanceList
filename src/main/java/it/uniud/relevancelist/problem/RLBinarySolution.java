package it.uniud.relevancelist.problem;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.*;

import java.util.*;

@SuppressWarnings("serial")
public class RLBinarySolution extends AbstractSolution<BinarySet> implements BinarySolution{

	static final int nVariables = RLBinaryProblem.nVariables;
	static final int nObjectives = RLBinaryProblem.nObjectives;
	static final int nCostraints = RLBinaryProblem.nCostraints;
	
	private int numberOfSelectedTopics;
	

	
	RLBinarySolution(boolean[] topics) {
		super(nVariables, nObjectives, nCostraints);
		numberOfSelectedTopics = 0;
		for(int i=0; i<topics.length; i++) if (topics[i]) numberOfSelectedTopics++;
		setVariable(0, createNewBitSet(topics.length, topics));
	}
	
	RLBinarySolution(int numberOfTopics) {
		super(nVariables, nObjectives, nCostraints);
		boolean[] topics = new boolean[numberOfTopics];
		for (int i = 0; i<numberOfTopics; i++) topics[i]= false;
		setVariable(0, createNewBitSet(topics.length, topics));
		numberOfSelectedTopics = 0;
	}

	
	RLBinarySolution(RLBinarySolution solution) {
		super(nVariables, nObjectives, nCostraints);
		numberOfSelectedTopics = solution.numberOfSelectedTopics;
		for (int i=0; i < this.getNumberOfVariables(); i++) this.setVariable(i,(BinarySet) solution.getVariable(i).clone());
		for (int i=0; i < this.getNumberOfObjectives(); i++) this.setObjective(i, solution.getObjective(i));
	}


	@Override
	public int getNumberOfBits(int index) {
		return getVariable(index).getBinarySetLength();
	}

	@Override
	public int getTotalNumberOfBits() {
        int sum = 0;
        for(int i=0; i < getNumberOfVariables(); i++) sum = getVariable(i).getBinarySetLength();
        return sum;
	}
	

    public BinarySet createNewBitSet(int numberOfBits, boolean[] values) {
    	BinarySet bitSet = new BinarySet(numberOfBits) ;
    	for (int i=0; i < numberOfBits; i++) {
    		if (values[i]) bitSet.set(i); 
    		else bitSet.clear(i);
    	}
        return bitSet;
    }
    
    public void setBitValue(int index, boolean value) {
        BinarySet topicStatusValues = getVariable(0);
        if (topicStatusValues.get(index) != value) {
            topicStatusValues.set(index, value);
            if (value) numberOfSelectedTopics++; else numberOfSelectedTopics--;
        }
        setVariable(0, topicStatusValues);
    }


	@Override
	public Solution<BinarySet> copy() {
		return new RLBinarySolution(this);
	}
	

	public int getNumberOfSelectedTopics() {
		return numberOfSelectedTopics;
	}

	public void setNumberOfSelectedTopics(int numberOfSelectedTopics) {
		this.numberOfSelectedTopics = numberOfSelectedTopics;
	}
	
    public boolean[] retrieveTopicStatus() {
       boolean[] topicStatusValues = new boolean[(getVariable(0).getBinarySetLength())];
        for (int i=0; i<getVariable(0).getBinarySetLength(); i++) topicStatusValues[i] = getVariable(0).get(i);
        return topicStatusValues;
    }

}
