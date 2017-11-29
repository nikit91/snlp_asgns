package edu.upb.snlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DRecognize {
	
	//Stores the mapping of index of each input character
	Map<Character, Integer> inputIndexMap;
	//Stores the mapping of each state to a list of states it could transition to
	Map<Integer, List<Integer>> stateTransitionMap;
	//Stores the list of valid states
	List<Integer> validStates;

	public static void main(String[] args) {
		try {
			// verify the input
			if (args.length != 3) {
				System.out.println("Invalid input count! 3 inputs required.");
				return;
			}
			Path path = Paths.get(args[0]);
			List<String> ttLines = new ArrayList<String>();
			Files.lines(path).forEachOrdered(s -> ttLines.add(s));
			// System.out.println(ttLines);
			
			path = Paths.get(args[1]);
			StringBuilder inputStrBuilder = new StringBuilder();
			Files.lines(path).forEachOrdered(s -> inputStrBuilder.append(s).append("\n"));
			String inputStr = inputStrBuilder.toString();
			DRecognize drec = new DRecognize();
			String outputFile = args[2];
			drec.processInput(inputStr, ttLines,outputFile);
		} catch (IOException exception) {
			System.out.println("Error locating files.");
		}
	}
	/**
	 * Method to process the input string with help of transition tables
	 * @param inputStr signifies the tape
	 * @param ttLines lines from the file containing transition table
	 * @param outputFile path to the output file
	 * @throws IOException
	 */
	public void processInput(String inputStr, List<String> ttLines,String outputFile) throws IOException {
		// generate the inputIndexMap and stateTransitionMap from ttLines
		inputIndexMap = new HashMap<>();
		stateTransitionMap = new HashMap<>();
		generateMaps(ttLines);
		// Start reading the inputStr and write the output to file
		readInputStr(inputStr,outputFile);
	}
	/**
	 * Method to read the input string and generate the sequence of transition states
	 * @param inputStr signifies the input tape
	 * @param outputFile path to output file
	 * @throws IOException
	 */
	public void readInputStr(String inputStr,String outputFile) throws IOException {
		int curSt = 0;
		Integer tempIndex;
		Integer nextState;
		List<String> resList = new ArrayList<>();
		resList.add("0");
		boolean endRead = false;
		for (int i = 0; i < inputStr.length(); i++) {
			tempIndex = inputIndexMap.get(inputStr.charAt(i));
			if (tempIndex == null) {
				endRead = true;
			} else {
				nextState = stateTransitionMap.get(curSt).get(tempIndex);
				if (!validStates.contains(nextState)) {
					endRead = true;
				} else {
					curSt = nextState;
					resList.add(String.valueOf(curSt));
				}
			}
			if (endRead) {
				resList.add("no");
				break;
			}
		}
		// write the output
		writeOutputFile(resList, outputFile);
	}
	/**
	 * Method to write the list to an output file
	 * @param resList list to written
	 * @param outputFile path to the output file
	 * @throws IOException
	 */
	public void writeOutputFile(List<String> resList,String outputFile) throws IOException {
		Path path = Paths.get(outputFile);
		Files.write(path, resList);
		System.out.println("output successfully written to: "+path.toAbsolutePath());
	}
	/**
	 * Method to generate the transition table maps from the list of transition table lines
	 * @param ttLines list of transition table lines
	 */
	public void generateMaps(List<String> ttLines) {
		// Set inputIndexMap
		String[] tempInp = ttLines.get(0).split("\t");
		for (int i = 1; i < tempInp.length; i++) {
			inputIndexMap.put(tempInp[i].charAt(0), i - 1);
		}
		List<Integer> tempList;
		validStates = new ArrayList<>();
		boolean isValidState = true;
		Integer tempSt;
		// Set stateTransitionMap
		for (int i = 1; i < ttLines.size(); i++) {
			tempInp = ttLines.get(i).split("\t");
			tempSt = Integer.valueOf(String.valueOf(tempInp[0].charAt(0)));
			if (isValidState)
				validStates.add(tempSt);
			if (tempInp[0].matches(".*\\:$"))
				isValidState = false;
			tempList = new ArrayList<>();
			for (int j = 1; j < tempInp.length; j++) {
				tempList.add(Integer.valueOf(tempInp[j]));
			}
			stateTransitionMap.put(Integer.valueOf(String.valueOf(tempInp[0].charAt(0))), tempList);
		}
	}

}
