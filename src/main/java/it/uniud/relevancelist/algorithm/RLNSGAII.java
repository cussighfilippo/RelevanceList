package it.uniud.relevancelist.algorithm;

import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public class RLNSGAII<S extends Solution<?>> extends NSGAII<S> {
	
	double bestFitness;
	double maxErrTolerance;
	S bestSolution;
	
	public RLNSGAII(Problem<S> problem, int maxEvaluations, int populationSize, double maxErrTolerance, int matingPoolSize,
			int offspringPopulationSize, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
			SelectionOperator<List<S>, S> selectionOperator, Comparator<S> dominanceComparator,
			SolutionListEvaluator<S> evaluator) {
		super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator,
				mutationOperator, selectionOperator, dominanceComparator, evaluator);
		
		this.maxErrTolerance = maxErrTolerance;
		bestFitness = Double.MAX_VALUE;
		bestSolution = null;
	}


	
	  @Override protected boolean isStoppingConditionReached() {
		    return ((evaluations >= maxEvaluations) || (bestFitness <= maxErrTolerance));
		  }
	
	  @Override protected List<S> evaluatePopulation(List<S> population) {
		    population = evaluator.evaluate(population, getProblem());
		    population.stream().forEach(s -> { if (s.getConstraint(0) >= 0)
		    	{if (s.getObjective(0) < bestFitness) { 
		    		bestFitness = s.getObjective(0); 
		    		bestSolution = s; 
		    		}}});
		    return population;
		  }
	  
	  public S getBestSolution() {
		  return bestSolution;
	  }
}
