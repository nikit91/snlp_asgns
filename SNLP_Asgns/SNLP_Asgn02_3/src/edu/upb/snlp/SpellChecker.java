package edu.upb.snlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SpellChecker {

	private Map<Integer, List<String>> tripleMap;
	private int i = 1;
	private CalcLevDist dist;
	private Stream<String> lines;
	private Path corpusPath;
	public static void main(String[] args) throws IOException {
		String fileName = args[0];
		String testFile = args[1];
		SpellChecker checker = new SpellChecker();
		// Create triple map by mapping line number to list of triples
		checker.fillTripleMap(fileName);
		Path path = Paths.get(testFile);
		Files.lines(path).forEachOrdered(s -> checker.printResults(s,checker));
	}

	public void printResults(String testString, SpellChecker checker) {
		try {
		// Fetch the triple and line number
		int[] res = checker.findMatchingTriple(testString.toLowerCase());
		// Fetch the exact word from sentence after removing punctuation
		String matchingWord = checker.findMatchingWord(res);
		// display the result
		System.out.println(testString + ", " + (matchingWord == null ? "" : matchingWord));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void fillTripleMap(String fileName) throws IOException {
		dist = new CalcLevDist();
		tripleMap = new HashMap<>();
		corpusPath = Paths.get(fileName);
		
		Files.lines(corpusPath).forEachOrdered(s -> addToMap(s));
	}

	public void addToMap(String line) {
		String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		List<String> tripleList = new ArrayList<>();
		tripleMap.put(i, tripleList);
		StringBuilder tempStr;
		if (words.length >= 3) {
			for (int j = 0; j < words.length - 2; j++) {
				tempStr = new StringBuilder();
				tempStr.append(words[j]).append(" ").append(words[j + 1]).append(" ").append(words[j + 2]);
				tripleList.add(tempStr.toString());
			}
		}
		// increment i
		i++;
	}

	public int[] findMatchingTriple(String testString) {
		int[] res = new int[2];
		int minDist = -1;
		int tempDist;
		List<String> tempList;
		for (int j : tripleMap.keySet()) {
			tempList = tripleMap.get(j);
			for (int k = 0; k < tempList.size(); k++) {
				tempDist = dist.calculateDistance(testString, tempList.get(k));
				if (minDist == -1 || minDist > tempDist) {
					res[0] = j;
					res[1] = k;
					minDist = tempDist;
				}
			}
		}
		return res;
	}

	public String findMatchingWord(int[] res) throws IOException {
		lines = Files.lines(corpusPath);
		String str = null;
		int lineNum = res[0];
		String matchingTriple = tripleMap.get(res[0]).get(res[1]);

		String fileStr = lines.skip(lineNum - 1).findFirst().get().replaceAll("[^a-zA-Z ]", "");
		Pattern pattern = Pattern.compile("(?i)(" + matchingTriple + ")");
		Matcher matcher = pattern.matcher(fileStr);
		if (matcher.find()) {
			str = matcher.group(1).split("\\s")[2];
		}
		return str;
	}

}
