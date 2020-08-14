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
import it.uniud.relevancelist.utils.*;

import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.*;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

/**
 * Hello world!
 *
 */
public class Program
{
	public enum EvaluationFunction {avgPrecision, test};
	
	static int populationSize;
	static int maxEvaluations;
	static double crossoverProbability;
	static double mutationProbability;
	static int listLength;
	static int maxCellValue;
	static double targetValue;
	static int relevantDocs;
	static double maxErrTolerance;	
	static int numExperimentIterations;
	static EvaluationFunction evalFunction;
	static String fileName;
	static double fractNonZero;
	
    /**
     * @param args
     * @throws IOException 
     */
    public static void main( String[] args ) throws IOException
    {

    	
    	//cmdline  java -jar .\target\RelevanceList-1.0-SNAPSHOT-jar-with-dependencies.jar 50 50000 0.8 0.3 50 1 0.8957 6 0.00005 10 "avgPrecision" "risultati.csv" 0.1
		// Lettura dei parametri
		
		if(args.length != 13){
			System.err.println("Wrong number of parameters specified: " + args.length + " != 13");
			System.err.println("Parameters: populationSize maxEvaluations crossoverProbability mutationProbability "
								+ "listLength maxCellValue targetValue relevantDocs maxRelTolerance numExperimentIterations "
								+ "funzioneValutazione filePath fractNonZero");
			System.exit(1);
		}
		
		
			
		populationSize = Integer.parseInt(args[0]);
	    maxEvaluations = Integer.parseInt(args[1]);
		crossoverProbability = Double.parseDouble(args[2]);
		mutationProbability = Double.parseDouble(args[3]);
		listLength = Integer.parseInt(args[4]);
		maxCellValue = Integer.parseInt(args[5]);
		targetValue = Double.parseDouble(args[6]);
		relevantDocs = Integer.parseInt(args[7]);
		maxErrTolerance = Double.parseDouble(args[8]);	
		numExperimentIterations = Integer.parseInt(args[9]);
		// avg01Metric , avgPrecisionMetric , ndcgMetric
		
		if (!Arrays.stream(EvaluationFunction.values()).anyMatch((t) -> t.name().equals(args[10]))) {
			System.err.println("Invalid evaluation function: " + args[10]);
			System.err.println("Valid functions: " + Arrays.toString(EvaluationFunction.values()));
			System.exit(1);
		};
		evalFunction = EvaluationFunction.valueOf(args[10]);
		fileName = args[11];
		fractNonZero = Double.parseDouble(args[12]);
		
		
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
        
        
        // copiato dall'originale 
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
    	
    	
    	// problem setup
    	
    	RLBinarySolutionFactory factory = new RLBinarySolutionFactory(1, listLength, relevantDocs, distribution, fractNonZero);
        RLBinaryProblem problem = new RLBinaryProblem(targetValue, evalFunction, factory);       
        CrossoverOperator<RLBinarySolution> crossover = new BinaryCrossover(crossoverProbability, problem);
        MutationOperator<RLBinarySolution> mutation = new BinaryMutation(mutationProbability, distribution);
        SelectionOperator<List<RLBinarySolution>, RLBinarySolution> selection = new BinaryTournamentSelection<RLBinarySolution>(new RankingAndCrowdingDistanceComparator<RLBinarySolution>());
        RLNSGAIIBuilder<RLBinarySolution> builder = new RLNSGAIIBuilder(problem, crossover, mutation, populationSize, maxErrTolerance);
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
	        System.out.println((seed+1) + "° exp Solution" + "\t" + currentBestSolution.getVariable(0).toString() + "\t" + (currentBestSolution.getObjective(0)) + "\t" + currentBestSolution.getNumberOfRelevantDocs());
	        System.out.println();
	        if (bestSolution==null || currentBestSolution.getObjective(0)<bestSolution.getObjective(0) ) {
	        	bestSolution = currentBestSolution;
	        	bestError = bestSolution.getObjective(0);
	        }
	        solutions.add(currentBestSolution);
	        seed++;
        }
        
		// Results printing on my file
        

        String PATH_SEPARATOR = System.getProperty("file.separator").toString();
		String filePath =  System.getProperty("user.dir") + PATH_SEPARATOR + "Target" + PATH_SEPARATOR + fileName;
		FileWriter outfile = new FileWriter(filePath, true);
		outfile.write("\n" + targetValue+","+relevantDocs +","+listLength +","+evalFunction.toString()+","+
				crossoverProbability+","+mutationProbability+","+maxEvaluations+","+maxErrTolerance+","+
			    "\t" + bestSolution.getNumberOfRelevantDocs() +","+ bestSolution.getObjective(0) +"," +bestSolution.getVariable(0).toString());
		outfile.close();

		
		//Results printing on his file with original format
		oldFilePrinting(solutions);
		
		
		System.out.println("execution completed");
		
    }
    
    static void oldFilePrinting(List<RLBinarySolution> solutions) throws IOException {
    	
    	double bestError = 10000000;
		int bestRelevantCount = -1;
		double bestValue = 10000000;
		String bestList = "UNDEF";
        int listSize = solutions.size();
        
		double[] vettoreErrori = new double[listSize];	
		double[] vettoreBestValues = new double[listSize];
		int[] vettoreRelevantCount = new int[listSize];	
		String[] vettoreListe = new String[listSize];
		// int[] vettoreRelevantCountNONADM = new int[numExperimentIterations];	
		
		int counter = 0;
		for(RLBinarySolution a : solutions) {
			vettoreErrori[counter] = a.getObjective(0);
			vettoreRelevantCount[counter] = a.getNumberOfRelevantDocs();
			// vettoreRelevantCountNONADM[counter] = returnValues.bestNonAdmissibleDocsNumber;
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
        
		int validSolutionsCount = counter;  //Tengo una lista di soluzioni, l'originale aveva un array fisso sul numero degli esperimenti
											//quindi in caso di terminazione anticipata per errTolerance il resto dell'array era invalido
		double averageRelError = Utils.getAverage(vettoreErrori, counter, validSolutionsCount);
		double minError = Utils.getMinimum(vettoreErrori, counter);
		double stddevRelError = Utils.getStddev(vettoreErrori, averageRelError, counter, validSolutionsCount);
        


        String PATH_SEPARATOR = System.getProperty("file.separator").toString();
		String filePath =  System.getProperty("user.dir") + PATH_SEPARATOR + "Target" + PATH_SEPARATOR + "esperimenti_genetico.csv";
	    FileWriter outfile = new FileWriter(filePath, true);
		outfile.write("\n"+targetValue+","+relevantDocs +","+listLength +","+evalFunction.toString()+","+
				crossoverProbability+","+mutationProbability+","+maxEvaluations+","+maxErrTolerance+","+
				counter+","+stddevRelError+","+averageRelError+","+minError+","+bestValue+","+bestRelevantCount+
				","+ "\t"+bestList);
	    
		
		outfile.close();
    }

	
}
