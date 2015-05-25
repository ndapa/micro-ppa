package edu.cmu.ml.rtw.ppa.util;

import java.util.HashSet;

import edu.stanford.nlp.process.Morphology;

public class PhraseOps {


  public static String toUpperCase(String phrase) {
    StringBuilder sb = new StringBuilder();
    for (String word : phrase.split(" ")) {
      String w1 = word;
      w1 = w1.replace(w1.charAt(0), Character.toUpperCase(w1.charAt(0)));
      sb.append(w1 + " ");

    }

    return sb.toString().trim();
  }

  /** To Lowercase **/
  public static String toLowerCase(String phrase) {
    StringBuilder sb = new StringBuilder();
    for (String word : phrase.split(" ")) {
      sb.append(word.toLowerCase() + " ");

    }

    return sb.toString().trim();
  }

  /** Gets constituent words **/
  public static HashSet<String> getWords(String phrase) {
    HashSet<String> words = new HashSet<String>();
    for (String word : phrase.split(" ")) {
      words.add(word);
    }

    return words;
  }

  /** Gets constituent words **/
  public static HashSet<String> getWords(String phrase, String seperator) {
    HashSet<String> words = new HashSet<String>();
    for (String word : phrase.split(seperator)) {
      words.add(word);
    }

    return words;
  }

  /** Gets constituent lowercase words **/
  public static HashSet<String> getLowerCaseWords(String phrase) {
    HashSet<String> words = new HashSet<String>();
    for (String word : phrase.split(" ")) {
      words.add(word.toLowerCase());
    }
    return words;
  }

  /** Gets the last of the constituent words **/
  public static HashSet<String> getLastName(String phrase) {
    HashSet<String> words = new HashSet<String>();
    String[] w = phrase.split(" ");
    words.add(w[w.length - 1]);
    return words;
  }

  /** Approximate match between two strings **/
  public static double fuzzyStringMatch(String s1, String s2) {
    HashSet<String> uniqueWords1 = new HashSet<String>();
    String[] words = s1.split(" ");
    for (String w : words) {
      if (w.length() < 3) continue;

      uniqueWords1.add(toLowerCase(w));
    }

    HashSet<String> uniqueWords2 = new HashSet<String>();
    words = s2.split(" ");
    for (String w : words) {
      if (w.length() < 3) continue;
      uniqueWords2.add(toLowerCase(w));
    }

    int intersect = SetOps.getSetIntersectionSize(uniqueWords1, uniqueWords2);
    int union = uniqueWords1.size() + uniqueWords2.size() - intersect;

    return intersect / (double) union;
  }

  /** Approximate match between two strings **/
  public static double fuzzyStringMatch(String s1, String s2, String seperator) {
    HashSet<String> uniqueWords1 = new HashSet<String>();
    String[] words = s1.split(seperator);
    for (String w : words) {
      if (w.length() < 3) continue;

      uniqueWords1.add(toLowerCase(w));
    }

    HashSet<String> uniqueWords2 = new HashSet<String>();
    words = s2.split(" ");
    for (String w : words) {
      if (w.length() < 3) continue;
      uniqueWords2.add(toLowerCase(w));
    }

    int intersect = SetOps.getSetIntersectionSize(uniqueWords1, uniqueWords2);
    int union = uniqueWords1.size() + uniqueWords2.size() - intersect;

    return intersect / (double) union;
  }
}
