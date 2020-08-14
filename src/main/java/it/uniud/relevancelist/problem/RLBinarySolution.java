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
	
	private int numberOfRelevantDocs;
	

	
	RLBinarySolution(boolean[] docsStatus) {
		super(nVariables, nObjectives, nCostraints);
		numberOfRelevantDocs = 0;
		for(int i=0; i<docsStatus.length; i++) if (docsStatus[i]) numberOfRelevantDocs++;
		setVariable(0, createNewBitSet(docsStatus.length, docsStatus));
	}
	
	RLBinarySolution(int numberOfDocs) {
		super(nVariables, nObjectives, nCostraints);
		boolean[] topics = new boolean[numberOfDocs];
		for (int i = 0; i<numberOfDocs; i++) topics[i]= false;
		setVariable(0, createNewBitSet(topics.length, topics));
		numberOfRelevantDocs = 0;
	}

	
	RLBinarySolution(RLBinarySolution solution) {
		super(nVariables, nObjectives, nCostraints);
		numberOfRelevantDocs = solution.numberOfRelevantDocs;
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
        BinarySet docsStatusValues = getVariable(0);
        if (docsStatusValues.get(index) != value) {
            docsStatusValues.set(index, value);
            if (value) numberOfRelevantDocs++; else numberOfRelevantDocs--;
        }
        setVariable(0, docsStatusValues);
    }


	@Override
	public Solution<BinarySet> copy() {
		return new RLBinarySolution(this);
	}
	

	public int getNumberOfRelevantDocs() {
		return numberOfRelevantDocs;
	}

	public void setNumberOfRelevantDocs(int n) {
		this.numberOfRelevantDocs = n;
	}
	
    public boolean[] retrieveDocsStatus() {
       boolean[] docsStatusValues = new boolean[(getVariable(0).getBinarySetLength())];
        for (int i=0; i<getVariable(0).getBinarySetLength(); i++) docsStatusValues[i] = getVariable(0).get(i);
        return docsStatusValues;
    }

}
