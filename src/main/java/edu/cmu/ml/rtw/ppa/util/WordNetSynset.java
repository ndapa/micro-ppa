package edu.cmu.ml.rtw.ppa.util;

import java.util.HashSet;

public class WordNetSynset {

	HashSet<String> surfaceForms;
	HashSet<String> antonyms;
	boolean hasantonyms;

	public WordNetSynset(HashSet<String> surfaceForms, HashSet<String> antonyms) {
		this.surfaceForms = new HashSet<String>(surfaceForms);
		this.antonyms = new HashSet<String>(antonyms);
		hasantonyms = antonyms.size() > 0;
	}

	public boolean hasAntonyms() {
		return hasantonyms;
	}

	public HashSet<String> getSurfaceForms() {
		return surfaceForms;
	}

	public HashSet<String> getAntonyms() {
		return antonyms;
	}
}
