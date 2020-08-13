package it.uniud.relevancelist.program;

import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniud.relevancelist.algorithm.RLNSGAII;
import it.uniud.relevancelist.algorithm.RLNSGAIIBuilder;
import it.uniud.relevancelist.operators.BinaryCrossover;
import it.uniud.relevancelist.operators.BinaryMutation;
import it.uniud.relevancelist.problem.RLBinaryProblem;
import it.uniud.relevancelist.problem.RLBinarySolution;
import it.uniud.relevancelist.problem.RLBinarySolutionFactory;

import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.*;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

/**
 * Hello world!
 *
 */
public class Program
{
	public enum EvaluationFunction {avgPrecision, test};
	
    /**
     * @param args
     * @throws IOException 
     */
    public static void main( String[] args ) throws IOException
    {

    	
    	//cmdline  java -jar .\target\RelevanceList-1.0-SNAPSHOT.jar 50 50000 0.8 0.3 50 1 0.8957 6 0.00005 10 "avgPrecision" "filepath" 0.1
		// Lettura dei parametri
		
		if(args.length != 13){
			System.err.println("Wrong number of parameters specified: " + args.length + " != 13");
			System.err.println("Parameters: populationSize maxEvaluations crossoverProbability mutationProbability "
								+ "listLength maxCellValue targetValue relevantDocs maxRelTolerance numExperimentIterations "
								+ "funzioneValutazione filePath fractNonZero");
			System.exit(1);
		}
		
		
			
		int populationSize = Integer.parseInt(args[0]);
		int maxEvaluations = Integer.parseInt(args[1]);
		double crossoverProbability = Double.parseDouble(args[2]);
		double mutationProbability = Double.parseDouble(args[3]);
		int listLength = Integer.parseInt(args[4]);
		int maxCellValue = Integer.parseInt(args[5]);
		double targetValue = Double.parseDouble(args[6]);
		int relevantDocs = Integer.parseInt(args[7]);
		double maxErrTolerance = Double.parseDouble(args[8]);	
		int numExperimentIterations = Integer.parseInt(args[9]);
		// avg01Metric , avgPrecisionMetric , ndcgMetric
		
		if (!Arrays.stream(EvaluationFunction.values()).anyMatch((t) -> t.name().equals(args[10]))) {
			System.err.println("Invalid evaluation function: " + args[10]);
			System.err.println("Valid functions: " + Arrays.toString(EvaluationFunction.values()));
			System.exit(1);
		};
		EvaluationFunction evalFunction = EvaluationFunction.valueOf(args[10]);
		String fileName = args[11];
		double fractNonZero = Double.parseDouble(args[12]);
		
		
		// Fine lettura dei parametri
		
		//print parametri
        System.out.println( "pop size:\t" + populationSize  );
        System.out.println( "max eval:\t" + maxEvaluations  );
        System.out.println( "crossover prob:\t" + crossoverProbability  );
        System.out.println( "mutation prob:\t" + mutationProbability  );
        System.out.println( "list length:\t" + listLength  );
        System.out.println( "max cell value:\t" + maxCellValue  );
        System.out.println( "target value:\t" + targetValue  );
        System.out.println( "relevanct docs:\t" + relevantDocs  );
        System.out.println( "max error:\t" + maxErrTolerance  );
        System.out.println( "n iterations:\t" + numExperimentIterations  );
        System.out.println( "funzione val:\t" + evalFunction.toString()  );
        System.out.println( "filename:\t" + fileName  );
        System.out.println( "fractNonZero:\t" + fractNonZero  );
        System.out.println();
        
        
    	// DETERMINO UNA DISTRIBUZIONE DI PROBABILITA' PER LA LISTA DEI RELEVANT DOCUMENT (USATA PER INIZIALIZZAZIONE E MUTATION)
    	int[] indexValues = new int[listLength];
    	for(int i=0; i<listLength; i++){
    		indexValues[i] = i;
    	}
    	double[] probabilities = new double[listLength];
    	for(int i=0; i<listLength; i++){
    		//probabilities[i] = (float) 1.0/(i+1);
    		probabilities[i] = (float) 1.0/listLength;
    	}
    	EnumeratedIntegerDistribution distribution = new EnumeratedIntegerDistribution(indexValues, probabilities);
    	
    	RLBinarySolutionFactory factory = new RLBinarySolutionFactory(1, listLength, relevantDocs, distribution, fractNonZero);
        BinaryProblem problem = new RLBinaryProblem(targetValue, evalFunction, factory);
        
//        RLBinarySolution[] solutions = new RLBinarySolution[2];
//        boolean[] temp = {true, true, true, true, true, true, true, true, true, true};
//        for (int i = 0; i < solutions.length; i++) {
//        	solutions[i] = factory.generateNewSolution(temp);
//        	problem.evaluate(solutions[i]);
//        }
//        Arrays.stream(solutions).forEach(s -> System.out.println(s.getVariable(0).toString()));
//        System.out.println();
        
        CrossoverOperator<BinarySolution> crossover = new BinaryCrossover(crossoverProbability, problem);
        MutationOperator<BinarySolution> mutation = new BinaryMutation(mutationProbability, distribution);
        SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<BinarySolution>(new RankingAndCrowdingDistanceComparator<BinarySolution>());

 //      NSGAIIBuilder builder = new NSGAIIBuilder(problem, crossover, mutation, populationSize);
        RLNSGAIIBuilder<BinarySolution> builder = new RLNSGAIIBuilder<BinarySolution>(problem, crossover, mutation, populationSize, maxErrTolerance);
        builder.setSelectionOperator(selection);
        builder.setMaxEvaluations(maxEvaluations);
        Algorithm<List<BinarySolution>> algorithm; 
        RLBinarySolution currentBestSolution;
        RLBinarySolution bestSolution=null;
        
        for(int i=0; i < numExperimentIterations; i++) { 
	        algorithm = builder.build();
	        algorithm.run();
	        currentBestSolution = (RLBinarySolution)  ( (RLNSGAII) algorithm).getBestSolution();
	        System.out.println((i+1) + "° exp Solution" + "\t" + currentBestSolution.getVariable(0).toString() + "\t" + (currentBestSolution.getObjective(0)) + "\t" + currentBestSolution.getNumberOfSelectedTopics());
	        System.out.println();
	        if (bestSolution==null || currentBestSolution.getObjective(0)<bestSolution.getObjective(0) ) bestSolution = currentBestSolution;
        }
        

        String PATH_SEPARATOR = System.getProperty("file.separator").toString();
		String filePath =  System.getProperty("user.dir") + PATH_SEPARATOR + "Target" + PATH_SEPARATOR + fileName;
		FileWriter outfile = new FileWriter(filePath, true);
//		outfile.write("\n"+targetValue+","+relevantDocs +","+listLength +","+evalFunction.toString()+","+
//				crossoverProbability+","+mutationProbability+","+maxEvaluations+","+maxErrTolerance+","+
//				seed+","+stddevRelError+","+averageRelError+","+minError+","+bestValue+","+bestRelevantCount+
//				","+ "\t"+bestList);
		
		outfile.write("\n" + targetValue+","+relevantDocs +","+listLength +","+evalFunction.toString()+","+
				crossoverProbability+","+mutationProbability+","+maxEvaluations+","+maxErrTolerance+","+
			    "\t" + bestSolution.getNumberOfSelectedTopics() +","+ bestSolution.getObjective(0) +"," +bestSolution.getVariable(0).toString());
		outfile.close();
		
		System.out.println("execution completed");
		
    }

	
}
