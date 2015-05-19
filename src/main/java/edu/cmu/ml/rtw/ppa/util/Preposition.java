package edu.cmu.ml.rtw.ppa.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Preposition {

  String prep;
  int positionInWordSequence;

  private static HashMap<String, Double> prepositionAttachmentPriors;
  public static List<String> mostFrequentPrepositions = CastingGenerics.castList(String.class,
      Arrays.asList(new String[] { "in", "on", "at", "for", "as", "from", "with", "that", "by", "of" }));

  public static List<String> tobeVerbs = CastingGenerics.castList(
      String.class,
      Arrays.asList(new String[] {
          "be",
          "am",
          "is",
          "are",
          "was",
          "were",
          "been",
          "has",
          "have",
          "had",
          "do",
          "did",
          "does",
          "can",
          "could",
          "shall",
          "should",
          "will",
          "would",
          "may",
          "might",
          "must" }));

  public static final int K = 3;

  public Preposition(String prep, int positionInWordSequence) {
    this.prep = prep;
    this.positionInWordSequence = positionInWordSequence;
  }

  public static boolean isFrequentPreposition(String prep) {
    return mostFrequentPrepositions.contains(prep);
  }

  public static boolean istoBeVerb(String verb) {
    return tobeVerbs.contains(verb);
  }

  public String toString() {
    return prep + "\t" + positionInWordSequence;
  }

  public static double getPronounAttachmentPrior(String preposition, String attachmentsite) {
    if (prepositionAttachmentPriors == null) {

      // from Ndapa's training labels
      prepositionAttachmentPriors = new HashMap<String, Double>();
      prepositionAttachmentPriors.put("in:V", 0.6);
      prepositionAttachmentPriors.put("in:N", 0.4);

      prepositionAttachmentPriors.put("on:V", 0.5);
      prepositionAttachmentPriors.put("on:N", 0.5);

      //  "at",
      prepositionAttachmentPriors.put("at:V", 0.8);
      prepositionAttachmentPriors.put("at:N", 0.2);

      //  "for", 
      prepositionAttachmentPriors.put("for:V", 0.6);
      prepositionAttachmentPriors.put("for:N", 0.4);

      //  "as", 

      prepositionAttachmentPriors.put("as:V", 0.8);
      prepositionAttachmentPriors.put("as:N", 0.2);

      //  "from", 

      prepositionAttachmentPriors.put("from:V", 0.6);
      prepositionAttachmentPriors.put("from:N", 0.4);

      //  "with",
      prepositionAttachmentPriors.put("with:V", 0.7);
      prepositionAttachmentPriors.put("with:N", 0.3);

      // from experts
      //      with  v 0.6673151750972762
      //      with  n 0.3326848249027237
      //      for v 0.5272804774083546/
      //      for n 0.47271952259164535
      //      at  v 0.8011204481792717
      //      at  n 0.19887955182072828
      //      as  v 0.8130990415335463
      //      as  n 0.1869009584664537
      //      on  v 0.5427319211102994
      //      on  n 0.4572680788897005
      //      from  v 0.6914132379248659
      //      from  n 0.3085867620751342
      //      in  v 0.5301172707889126
      //      in  n 0.4698827292110874

    }

    return prepositionAttachmentPriors.get(preposition + ":" + attachmentsite);
  }
}
