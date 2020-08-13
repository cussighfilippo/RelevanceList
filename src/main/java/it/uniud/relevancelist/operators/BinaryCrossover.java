package it.uniud.relevancelist.operators;

import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import it.uniud.relevancelist.problem.RLBinaryProblem;
import it.uniud.relevancelist.problem.RLBinarySolution;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;

public class BinaryCrossover implements CrossoverOperator<BinarySolution>{
	
	double crossoverProbability;
	RLBinaryProblem problem;
	
	public BinaryCrossover(double crossoverProbability, BinaryProblem problem) {
		this.crossoverProbability = crossoverProbability;
		this.problem = (RLBinaryProblem) problem;
	}

	@Override
	public List<BinarySolution> execute(List<BinarySolution> source) {
		
        RLBinarySolution firstSolution = (RLBinarySolution) source.get(0);
        RLBinarySolution secondSolution = (RLBinarySolution) source.get(1);
        boolean[] firstTopicStatus = firstSolution.retrieveTopicStatus();
        boolean[] secondTopicStatus = secondSolution.retrieveTopicStatus();

        List<BinarySolution> childrenSolution = new ArrayList<BinarySolution>();

        RLBinarySolution firstChild = problem.getFactory().generateNewSolution();
        RLBinarySolution secondChild = problem.getFactory().generateNewSolution();
        childrenSolution.add(firstChild);
        childrenSolution.add(secondChild);
        
        if (JMetalRandom.getInstance().nextDouble() < crossoverProbability) {

            for (int i=0; i<firstTopicStatus.length; i++) {
                firstChild.setBitValue(i, firstTopicStatus[i] && secondTopicStatus[i]);
                secondChild.setBitValue(i, firstTopicStatus[i] || secondTopicStatus[i]);
            }

            if (firstChild.getNumberOfSelectedTopics() == 0) {
                int flipIndex =(int) Math.floor(JMetalRandom.getInstance().nextDouble() * firstChild.getNumberOfBits(0));
                if (flipIndex == firstChild.getNumberOfBits(0)) flipIndex -= 1;
                firstChild.setBitValue(flipIndex, true);
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
