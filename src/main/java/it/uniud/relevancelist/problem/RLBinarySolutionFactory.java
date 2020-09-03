package it.uniud.relevancelist.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

//  Class for RLBinarySolution generation of fixated length
//  New solutions generated without a relevance profile are based on the probability distribution given

public class RLBinarySolutionFactory {

	int maxValue; // max relevance value of a document
	int listLength; // length of a Solution's relevance list
	int relDocs; // number of relevant documents fixed for the problem
	EnumeratedIntegerDistribution distribution; // probability distribution
	double fractNonZero; // fraction of non-zero relevance documents in new solution generation
	JMetalRandom randomGenerator;
	DistributionMode distMode;

	public RLBinarySolutionFactory(int maxValue, int size, int relDocs, EnumeratedIntegerDistribution distribution,
			DistributionMode mode, double fractNonZero) {
		this.maxValue = maxValue;
		this.listLength = size;
		this.relDocs = relDocs;
		this.distribution = distribution;
		this.distMode = mode;
		this.fractNonZero = fractNonZero;
		randomGenerator = JMetalRandom.getInstance();

	}

	public RLBinarySolution generateNewSolution() {
		RLBinarySolution newSolution = new RLBinarySolution(createDocumentsSet());
		return newSolution;
	}

	public RLBinarySolution generateNewSolution(boolean[] docs) {
		if (docs.length != listLength) {
			System.err.println(Arrays.toString(docs) + " incompatible with factory initialization");
			System.err.println("docs length must match declared number of documents listLength of the factory");
			System.exit(1);
		}
		RLBinarySolution newSolution = new RLBinarySolution(docs);
		return newSolution;
	}

	// generates a random relevance profile as a boolean array
	// code for distribution's use is taken from previous Problem implementation
	private boolean[] createDocumentsSet() {

		int[] array = new int[listLength];
		for (int i = 0; i < listLength; i++) {
			array[i] = 0;
		}

		double fracNonZeroRelevant = fractNonZero; // percentuale dei relevant impostata in input
		double shiftValue = randomGenerator.nextDouble() * fracNonZeroRelevant;
		if (randomGenerator.nextDouble() > 0.5) {
			// somma
			fracNonZeroRelevant = fracNonZeroRelevant + shiftValue;
		} else {
			// sottrai
			fracNonZeroRelevant = fracNonZeroRelevant - shiftValue;
		}
		int howManyNotZero = (int) Math.round(fracNonZeroRelevant * relDocs);
		if (howManyNotZero == 0) {
			howManyNotZero++;
		} else if (howManyNotZero > relDocs) {
			howManyNotZero = relDocs;
		}

		switch (distMode) {

		// variante uniforme
		case uniform: {

			ArrayList<String> indiciDaProvare = new ArrayList<String>();
			for (int i = 0; i < listLength; i++) {
				indiciDaProvare.add(i + "");
			}
			for (int i = 0; i < howManyNotZero; i++) {
				int popIndex = (int) Math.round(randomGenerator.nextDouble() * (indiciDaProvare.size() - 1));
				String indexAsString = indiciDaProvare.get(popIndex);
				int cellIndex = Integer.parseInt(indexAsString);
				indiciDaProvare.remove(indexAsString);
				int cellValue = (int) Math.round(randomGenerator.nextDouble() * (maxValue - 1)) + 1;
				array[cellIndex] = cellValue;
			}
			break;
		}

		// variante geometrica
		case geometric: {
			ArrayList<Integer> indiciNonZero = new ArrayList<Integer>();
			int[] temp = distribution.sample(howManyNotZero);
			for (int i = 0; i < temp.length; i++) {
				indiciNonZero.add(temp[i]);
			}
			Set<Integer> indiciDistinti = new LinkedHashSet<Integer>();
			for (int x : indiciNonZero) {
				indiciDistinti.add(x);
			}
			for (int x : indiciDistinti) {
				indiciNonZero.remove(Integer.valueOf(x));
			}
			if (indiciNonZero.size() > 0) {
				for (int x : indiciNonZero) {
					int nuovoX = x;
					while (indiciDistinti.contains(nuovoX)) {
						nuovoX = (nuovoX + 1) % listLength;
					}
					indiciDistinti.add(nuovoX);
				}
			}
			indiciNonZero = new ArrayList(indiciDistinti);

			for (int cellIndex : indiciNonZero) {
				int cellValue = (int) Math.round(randomGenerator.nextDouble() * (maxValue - 1)) + 1;
				array[cellIndex] = cellValue;
			}
			break;
		}
		default:
			System.err.println("unrecognized distribution mode");
			System.exit(1);
		}

		// soluzione temporanea per tenere il codice originale

		boolean[] booleanArray = new boolean[array.length];
		for (int i = 0; i < array.length; i++)
			if (array[i] == 1)
				booleanArray[i] = true;
			else
				booleanArray[i] = false;
		return booleanArray;
	}

	public int getListLength() {
		return listLength;
	}

	public int getRelevantDocs() {
		return relDocs;
	}
}
