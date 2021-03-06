package it.uniud.relevancelist.program;

import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniud.relevancelist.algorithm.RLNSGAII;
import it.uniud.relevancelist.algorithm.RLNSGAIIBuilder;
import it.uniud.relevancelist.metric.AveragePrecisionEvaluator;
import it.uniud.relevancelist.metric.MetricEvaluator;
import it.uniud.relevancelist.metric.NDCGEvaluator;
import it.uniud.relevancelist.operator.BinaryCrossover;
import it.uniud.relevancelist.operator.BinaryMutation;
import it.uniud.relevancelist.problem.RLBinaryProblem;
import it.uniud.relevancelist.solution.RLBinarySolution;
import it.uniud.relevancelist.solution.RLBinarySolutionFactory;
import it.uniud.relevancelist.utils.*;

import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.*;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

public class Program
{
	static int populationSize;  
	static int maxEvaluations; //	max evaluations of a NSGAII run
	static double crossoverProbability;
	static double mutationProbability;
	static int listLength;		//  length of the documents relevance list
	static int maxCellValue;	//  max relevance value of a document
	static double targetValue;
	static int relevantDocs;
	static double maxErrTolerance;	
	static int numExperimentIterations;
	static EvaluationFunction evalFunction;	//  available evaluation functions specified in its classfile
	static String fileName;
	static double fractNonZero;	//	fraction of non-zero relevance documents in new solution generation 
	static DistributionType initializationDistributionType;  // geometric or uniform distribution 
	static DistributionType mutationDistributionType;	 // geometric or uniform distribution 
	
    /**
     * executes the experiment based on the 13 required arguments declared above
     * saves the results in declared filename inside Target folder
     * also saves the results in previous implementation's format in "esperimenti_genetico.csv" file
     * 
     * the program runs the experiment displaying the best solution obtained per run 
     * the overall best solution between those is saved in the mentioned files
     * if a solution is valued to be better than the maximum error tolerance, the program stops
     */
    public static void main( String[] args ) throws IOException
    {
  	
    	//cmdline  java -jar .\target\RelevanceList-1.0-SNAPSHOT-jar-with-dependencies.jar 100 500000 0.8 0.3 100 1 0.8957 21 0.00005 10 "avgPrecision" "risultati.csv" 0.1 "geometric" "geometric"
		
    	// Lettura dei parametri
		
		if(args.length != 15){
			System.err.println("Wrong number of parameters specified: " + args.length + " != 15");
			System.err.println("Parameters: populationSize maxEvaluations crossoverProbability mutationProbability "
								+ "listLength maxCellValue targetValue relevantDocs maxRelTolerance numExperimentIterations "
								+ "funzioneValutazione filePath fractNonZero "
								+ "initializationDistributionType mutationDistributionType");
			System.exit(1);
		}
		
		
			
		populationSize = Integer.parseInt(args[0]);
	    maxEvaluations = Integer.parseInt(args[1]);
		crossoverProbability = Double.parseDouble(args[2]);
		mutationProbability = Double.parseDouble(args[3]);
		listLength = Integer.parseInt(args[4]);
		maxCellValue = Integer.parseInt(args[5]);
		
		if(maxCellValue != 1) {
			System.err.println("maxCellValue !=1 : program is based on the binary version of the problem");
			System.exit(1);
		}
			
		targetValue = Double.parseDouble(args[6]);
		relevantDocs = Integer.parseInt(args[7]);
		maxErrTolerance = Double.parseDouble(args[8]);	
		numExperimentIterations = Integer.parseInt(args[9]);
		
		if (!Arrays.stream(EvaluationFunction.values()).anyMatch((t) -> t.name().equals(args[10]))) {
			System.err.println("Invalid evaluation function: " + args[10]);
			System.err.println("Valid functions: " + Arrays.toString(EvaluationFunction.values()));
			System.exit(1);
		};
		evalFunction = EvaluationFunction.valueOf(args[10]);
		
		fileName = args[11];
		fractNonZero = Double.parseDouble(args[12]);
		
		if (!Arrays.stream(DistributionType.values()).anyMatch((t) -> t.name().equals(args[13]))) {
			System.err.println("Invalid initialization distribution type: " + args[13]);
			System.err.println("Valid types: " + Arrays.toString(DistributionType.values()));
			System.exit(1);
		};
		
		if (!Arrays.stream(DistributionType.values()).anyMatch((t) -> t.name().equals(args[14]))) {
			System.err.println("Invalid mutation distribution type: " + args[14]);
			System.err.println("Valid types: " + Arrays.toString(DistributionType.values()));
			System.exit(1);
		};
		
		initializationDistributionType = DistributionType.valueOf(args[13]);
		mutationDistributionType = DistributionType.valueOf(args[14]);
		
		
		
		// Fine lettura dei parametri
		
		//print parametri
		
        System.out.println( "pop size:\t" + populationSize  );
        System.out.println( "max eval:\t" + maxEvaluations  );
        System.out.println( "crossover prob:\t" + crossoverProbability  );
        System.out.println( "mutation prob:\t" + mutationProbability  );
        System.out.println( "list length:\t" + listLength  );
        System.out.println( "max cell value:\t" + maxCellValue  );
        System.out.println( "target value:\t" + targetValue  );
        System.out.println( "relevant docs:\t" + relevantDocs  );
        System.out.println( "max error:\t" + maxErrTolerance  );
        System.out.println( "n iterations:\t" + numExperimentIterations  );
        System.out.println( "funzione val:\t" + evalFunction.toString()  );
        System.out.println( "filename:\t" + fileName  );
        System.out.println( "fractNonZero:\t" + fractNonZero  );
        System.out.println( "init dist type:\t" + initializationDistributionType.toString()  );
        System.out.println( "mut dist type:\t" + mutationDistributionType.toString()  );
        System.out.println();
        
        
        // distribution probabilities calculation. Used in new solutions generation and mutation operation 
    	int[] indexValues = new int[listLength];
    	for(int i=0; i<listLength; i++){
    		indexValues[i] = i;
    	}
    	double[] uniformProbabilities = new double[listLength];
    	double[] geometricProbabilities = new double[listLength];
    	for(int i=0; i<listLength; i++){
    		uniformProbabilities[i] = (float) 1.0/listLength;
    		geometricProbabilities[i] = (float) 1.0/(i+1);
    	}
    	EnumeratedIntegerDistribution uniformDistribution = new EnumeratedIntegerDistribution(indexValues, uniformProbabilities);
    	EnumeratedIntegerDistribution geometricDistribution = new EnumeratedIntegerDistribution(indexValues, geometricProbabilities);
    	
    	
    	// problem setup
    	// variables must be consistent between classes initializations
    	MetricEvaluator evaluator = new AveragePrecisionEvaluator(relevantDocs);

		switch (evalFunction) {
		case avgPrecision:
			evaluator = new AveragePrecisionEvaluator(relevantDocs);
			break;
		case ndcg:
			evaluator = new NDCGEvaluator();
			break;
		default:
			System.err.println("should not end up here");
		}
		
		EnumeratedIntegerDistribution initializationDistribution = geometricDistribution;
		EnumeratedIntegerDistribution mutationDistribution = uniformDistribution;
		
		switch (initializationDistributionType) {
		case uniform:
			initializationDistribution = uniformDistribution;
			break;
		case geometric:
			initializationDistribution = geometricDistribution;
			break;
		default:
			System.err.println("should not end up here");
		}
		
		switch (mutationDistributionType) {
		case uniform:
			mutationDistribution = uniformDistribution;
			break;
		case geometric:
			mutationDistribution = geometricDistribution;
			break;
		default:
			System.err.println("should not end up here");
		}

    	
    	RLBinarySolutionFactory factory = new RLBinarySolutionFactory( listLength, relevantDocs, initializationDistribution, fractNonZero );
        RLBinaryProblem problem = new RLBinaryProblem(targetValue, evaluator, factory);       
        CrossoverOperator<RLBinarySolution> crossover = new BinaryCrossover(crossoverProbability, problem);
        MutationOperator<RLBinarySolution> mutation = new BinaryMutation(mutationProbability, mutationDistribution);
        SelectionOperator<List<RLBinarySolution>, RLBinarySolution> selection = new BinaryTournamentSelection<RLBinarySolution>(new RankingAndCrowdingDistanceComparator<RLBinarySolution>());
        RLNSGAIIBuilder<RLBinarySolution> builder = new RLNSGAIIBuilder<RLBinarySolution>(problem, crossover, mutation, populationSize, maxErrTolerance);
        builder.setSelectionOperator(selection);
        builder.setMaxEvaluations(maxEvaluations);
        RLNSGAII<RLBinarySolution> algorithm; 
        
        
        
        // evaluation
        
        RLBinarySolution currentBestSolution;
        RLBinarySolution bestSolution=null;
        double bestError = Double.MAX_VALUE;
        List<RLBinarySolution> solutions = new ArrayList<RLBinarySolution>();
        
        int seed =0;
        while( seed < numExperimentIterations && bestError > maxErrTolerance) { 
	        algorithm = builder.build();
	        algorithm.run();
	        currentBestSolution = algorithm.getBestSolution();
	        System.out.println((seed+1) + "° experiment Solution" + "\t" + currentBestSolution.getVariable(0).toString() + "\t" + (currentBestSolution.getObjective(0)) + "\t" + currentBestSolution.getNumberOfRelevantDocs());
	        System.out.println();
	        if (bestSolution==null || currentBestSolution.getObjective(0)<bestSolution.getObjective(0) ) {
	        	bestSolution = currentBestSolution;
	        	bestError = bestSolution.getObjective(0);
	        }
	        solutions.add(currentBestSolution);
	        seed++;
        }
        

		//Results printing on  file with original format
		filePrinting(solutions);
		System.out.println("execution completed");
		
    }
    
    static void filePrinting(List<RLBinarySolution> solutions) throws IOException {
    	
    	double bestError = 10000000;
		int bestRelevantCount = -1;
		double bestValue = 10000000;
		String bestList = "UNDEF";
        int listSize = solutions.size();
        
		double[] vettoreErrori = new double[listSize];	
		double[] vettoreBestValues = new double[listSize];
		int[] vettoreRelevantCount = new int[listSize];	
		String[] vettoreListe = new String[listSize];
		
		int counter = 0;
		for(RLBinarySolution a : solutions) {
			vettoreErrori[counter] = a.getObjective(0);
			vettoreRelevantCount[counter] = a.getNumberOfRelevantDocs();
			vettoreBestValues[counter] = Math.abs(targetValue - a.getObjective(0));
			vettoreListe[counter] = a.getVariable(0).toString();
			
			if(a.getObjective(0) < bestError) {
				bestError = vettoreErrori[counter];
				bestRelevantCount = vettoreRelevantCount[counter];
				bestValue = vettoreBestValues[counter];
				bestList = vettoreListe[counter];
			}
			counter++;
		}
        
		int validSolutionsCount = counter;  
		double averageRelError = Utils.getAverage(vettoreErrori, counter, validSolutionsCount);
		double minError = Utils.getMinimum(vettoreErrori, counter);
		double stddevRelError = Utils.getStddev(vettoreErrori, averageRelError, counter, validSolutionsCount);
        


        String PATH_SEPARATOR = System.getProperty("file.separator").toString();
		String filePath =  System.getProperty("user.dir") + PATH_SEPARATOR + "Target" + PATH_SEPARATOR + fileName;
	    FileWriter outfile = new FileWriter(filePath, true);
		outfile.write("\n"+targetValue+","+relevantDocs +","+listLength +","+evalFunction.toString()+","+
				crossoverProbability+","+mutationProbability+","+maxEvaluations+","+maxErrTolerance+","+
				counter+","+stddevRelError+","+averageRelError+","+minError+","+bestValue+","+bestRelevantCount+
				","+ "\t"+bestList);
	    
		
		outfile.close();
    }

	
}
