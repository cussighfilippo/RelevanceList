package it.uniud.relevancelist.operators;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import it.uniud.relevancelist.problem.RLBinaryProblem;
import it.uniud.relevancelist.solution.RLBinarySolution;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;

public class BinaryCrossover implements CrossoverOperator<RLBinarySolution>{
	
	double crossoverProbability;
	RLBinaryProblem problem;
	
	public BinaryCrossover(double crossoverProbability, RLBinaryProblem problem) {
		this.crossoverProbability = crossoverProbability;
		this.problem = problem;
	}

	@Override
	public List<RLBinarySolution> execute(List<RLBinarySolution> source) {
		
        RLBinarySolution firstSolution =  source.get(0);
        RLBinarySolution secondSolution = source.get(1);
        boolean[] firstDocsStatus = firstSolution.retrieveDocsStatus();
        boolean[] secondDocsStatus = secondSolution.retrieveDocsStatus();

        List<RLBinarySolution> childrenSolution = new ArrayList<RLBinarySolution>();

        RLBinarySolution firstChild = problem.getFactory().generateNewSolution();
        RLBinarySolution secondChild = problem.getFactory().generateNewSolution();
        childrenSolution.add(firstChild);
        childrenSolution.add(secondChild);
        
        if (JMetalRandom.getInstance().nextDouble() < crossoverProbability) {

            for (int i=0; i<firstDocsStatus.length; i++) {
                firstChild.setBitValue(0, i, firstDocsStatus[i] && secondDocsStatus[i]);
                secondChild.setBitValue(0, i, firstDocsStatus[i] || secondDocsStatus[i]);
            }

            if (firstChild.getNumberOfRelevantDocs() == 0) {
                int flipIndex =(int) Math.floor(JMetalRandom.getInstance().nextDouble() * firstChild.getNumberOfBits(0));
                if (flipIndex == firstChild.getNumberOfBits(0)) flipIndex -= 1;
                firstChild.setBitValue(0, flipIndex, true);
            }

        }
    
		return childrenSolution;
	}

	@Override
	public double getCrossoverProbability() {
		return crossoverProbability;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 2;
	}

}
