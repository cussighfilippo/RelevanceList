package it.uniud.relevancelist.solution;


import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;

import it.uniud.relevancelist.problem.RLBinaryProblem;


@SuppressWarnings("serial")
public class RLBinarySolution extends AbstractSolution<BinarySet> implements BinarySolution{
	
	
	private int numberOfRelevantDocs;
	

	// creates a solution based on a given relevance list
	RLBinarySolution(int nVariables, int nObjectives, int nConstraints, boolean[] docsStatus) {
		super(nVariables, nObjectives, nConstraints);
		numberOfRelevantDocs = 0;
		for(int i=0; i<docsStatus.length; i++) if (docsStatus[i]) numberOfRelevantDocs++;
		setVariable(0, createNewBitSet(docsStatus.length, docsStatus));
	}
	
	// creates a solution of numberOfDocs total documents retrieved
	RLBinarySolution(int nVariables, int nObjectives, int nConstraints, int numberOfDocs) {
		super(nVariables, nObjectives, nConstraints);
		boolean[] topics = new boolean[numberOfDocs];
		for (int i = 0; i<numberOfDocs; i++) topics[i]= false;
		setVariable(0, createNewBitSet(topics.length, topics));
		numberOfRelevantDocs = 0;
	}

	// copy constructor
	RLBinarySolution(RLBinarySolution solution) {
		super(solution.getNumberOfVariables(),solution.getNumberOfObjectives(), solution.getNumberOfConstraints());
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
    
    // updates a single value of variable(0)
    public void setBitValue(int variable, int index, boolean value) {
        BinarySet docsStatusValues = getVariable(variable);
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
	
	@Override
	public void setVariable(int i, BinarySet set) {
		super.setVariable(i, set);
		if(i == 0) numberOfRelevantDocs = set.cardinality();
	}

	
    public boolean[] retrieveDocsStatus() {
       boolean[] docsStatusValues = new boolean[(getVariable(0).getBinarySetLength())];
        for (int i=0; i<getVariable(0).getBinarySetLength(); i++) docsStatusValues[i] = getVariable(0).get(i);
        return docsStatusValues;
    }

}
