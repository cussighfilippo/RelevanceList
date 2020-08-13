package it.uniud.relevancelist.problem;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import it.uniud.relevancelist.problem.RLBinarySolution;

public class BinaryMutation implements MutationOperator<BinarySolution> {
	
	double probability;
	JMetalRandom randomGenerator;
	EnumeratedIntegerDistribution distribution; 
	
	public BinaryMutation(double mutationProbability, EnumeratedIntegerDistribution dist ) {
		this.probability = mutationProbability;
		this.randomGenerator = JMetalRandom.getInstance();
		this.distribution = dist;
	}

	@Override
	public BinarySolution execute(BinarySolution solution) {
		if(randomGenerator.nextDouble() < probability){
			if(randomGenerator.nextDouble() > 0.5){
				swapMutation(solution);
				//sumMutation(solution);
			}else{
				//swapMutation(solution);
				sumMutation(solution);
			}
		}
		return null;
	}
	
	public void swapMutation(BinarySolution solution) {
		BinarySet variable =  solution.getVariable(0);
		//int swapIndex1 = (int) Math.round(PseudoRandom.randDouble()*(variable.getLength()-1));
		//int swapIndex2 = (int) Math.round(PseudoRandom.randDouble()*(variable.getLength()-1));
		int swapIndex1 = distribution.sample();
		int swapIndex2 = distribution.sample();
		boolean value1 = variable.get(swapIndex1);
		boolean value2 = variable.get(swapIndex2);
		((RLBinarySolution) solution).setBitValue(swapIndex1, value2);
		((RLBinarySolution) solution).setBitValue(swapIndex2, value1);
		
	}
	
	public void sumMutation(BinarySolution solution) {
		int sumIndex = distribution.sample();
		((RLBinarySolution) solution).setBitValue(sumIndex, true);
	}
	

	@Override
	public double getMutationProbability() {
		return probability;
	}


}
