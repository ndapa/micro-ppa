package edu.cmu.ml.rtw.ppa.predict;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


import edu.cmu.ml.rtw.ppa.np.NounPhrase;
import edu.cmu.ml.rtw.ppa.np.SimpeNPChuncks;
import edu.cmu.ml.rtw.ppa.util.FinalData;
import edu.cmu.ml.rtw.ppa.util.Preposition;
import edu.cmu.ml.rtw.ppa.util.WordSequence;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;

/** Extracts prepositional phrases quads from text**/
public class AttachmentExtractor {

  protected final String nounPhrase = "((NNP\\s|NN\\s){1}){1,}(NNPS\\s){0,}(NNS\\s){0,}((NN){1,}(\\s){0,}){0,}";
  protected final String nounPhraseProper = "((NNP){1}(\\s){0,}){1,}(NNPS\\s){0,}(NNS\\s){0,}((NN){1,}(\\s){0,}){0,}";
  protected final String nounPhrase_common = "(NN\\s|NNS\\s){1}";

  protected final String NP0 = "(?<name0>" + nounPhraseProper + ")";
  protected final String ppambig_quad = NP0 + "(?<verb>(VB\\s|VBD\\s|VBN\\s|VBZ\\s))(?<name1>" + nounPhrase + ")(?<prep>(IN\\s))(?<name2>"
      + nounPhrase + ")";

  protected final String ppambig_quad_common = NP0 + "(?<verb>(VB\\s|VBD\\s|VBN\\s|VBZ\\s))(?<name1>" + nounPhrase_common
      + ")(?<prep>(IN\\s))(?<name2>(JJS\\s){0,}(JJ\\s){0,}" + nounPhrase_common + ")";
  protected final String ppambig_quad_common2 = NP0 + "(?<verb>(VB\\s|VBD\\s|VBN\\s|VBZ\\s))(?<name1>" + nounPhrase_common
      + ")(?<prep>(IN\\s))(?<name2>(JJS\\s){0,}(JJ\\s){0,}" + nounPhrase_common + ")";
  // public static final String ppambig_tripleverb = "(VB\\s|VBD\\s|VBN\\s|VBZ\\s)([A-Z]\\s)([A-Z]\\s){0,4}(IN\\s)([A-Z]\\s){0,4}((NNP\\s){1,}(NNPS\\s){0,}(NNS\\s){0,}(NN\\s){0,}){1}";
  // public static final String ppambig_triplenoun = "((NNP\\s){1,}(NNPS\\s){0,}(NNS\\s){0,}(NN\\s){0,}){1}([A-Z]\\s){0,4}(IN\\s)([A-Z]\\s){0,4}((NNP\\s){1,}(NNPS\\s){0,}(NNS\\s){0,}(NN\\s){0,}){1}";

  // works for both proper nouns and common nous as discovered by the simple chunker
  protected final String ppambig_quad_no_grouping = "(NP\\s){1}(VB\\s|VBD\\s|VBN\\s|VBZ\\s)(CD\\s){0,}(DT\\s){0,}(NP\\s){1}(IN\\s)(DT\\s){0,}(CD\\s){0,}(NP\\s){1}";
  protected final String ppambig_quad_no_groupingTwoVerbs = "(NP\\s){1}(VB\\s|VBD\\s|VBN\\s|VBZ\\s){2}(CD\\s){0,}(NP\\s){1}(IN\\s)(DT\\s){0,}(CD\\s){0,}(NP){1}";
  protected List<String> ppAttachpatternsgrouping = new ArrayList<String>(Arrays.asList(new String[] {
      ppambig_quad,
      ppambig_quad_common,
      ppambig_quad_common2 }));

  protected boolean NYCparserLock;

  SimpeNPChuncks chunker;

  public AttachmentExtractor() {
    try {
      chunker = new SimpeNPChuncks();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 
   * 
   * @param wordSequence
   * @return
   */
  public List<String> findPPAsNoGroups(WordSequence wordSequence) {
    List<String> result = new ArrayList<String>();
    // first find the NPs
    WordSequence wordSequenceRaw = wordSequence;
    List<NounPhrase> nps = chunker.locateNounPhrasePosition(wordSequenceRaw);

    WordSequence updatedwordSequence = new WordSequence();
    int i = 0;

    List<String> sentenceWords = new ArrayList<String>();

    HashMap<String, NounPhrase> idToNPMap = new HashMap<String, NounPhrase>();
    List<HasWord> newSentenceWords = new ArrayList<HasWord>();
    while (i < wordSequence.size()) {
      StringBuilder sb = new StringBuilder(wordSequence.getWord(i));

      boolean entityAdded = false;
      for (int npId = 0; npId < nps.size(); npId++) {
        NounPhrase np = nps.get(npId);

        // if it is only a month skip this np
        if (FinalData.months.contains(np.toString().trim())) {
          continue;
        }

        if (i == np.startInWordSequence()) {
          StringBuilder sbTag = new StringBuilder();
          while (i < np.endInWordSequence() && i < wordSequence.size()) {
            sbTag.append(wordSequence.getTag(i) + " ");
            i++;
          }
          // last part of tag
          sbTag.append(wordSequence.getTag(i) + " ");
          newSentenceWords.add(new Word(np.toString()));
          sentenceWords.add(np.toString());

          // add entity place holder of entity and its tags
          updatedwordSequence.appendWord("Bob" + i);
          updatedwordSequence.appendTag("NP");
          idToNPMap.put("Bob" + i, np);
          np.setTagSequence(sbTag.toString().trim());
          // updatedwordSequence.appendTag(sbTag.toString().trim());

          entityAdded = true;
          break;

        }
      }
      if (!entityAdded) {
        String word = sb.toString();
        sentenceWords.add(word);
        newSentenceWords.add(new Word(word));
        if (wordSequence.getTag(i).indexOf("NP") >= 0) {
          updatedwordSequence.appendWord(word);
          updatedwordSequence.appendTag("OO");
          System.err.println("\t\tSkipping: " + word + " tag: " + wordSequence.getTag(i));
        } else {
          updatedwordSequence.appendWord(word);
          updatedwordSequence.appendTag(wordSequence.getTag(i));
        }
      }
      i++;
    }

    Pattern quadraplesPattern = Pattern.compile(ppambig_quad_no_grouping);
    Matcher matcher = quadraplesPattern.matcher(updatedwordSequence.TagtoString());
    while (matcher.find()) {
      // start and end of character starting positions
      int start = matcher.start();
      int end = matcher.end();

      // find actual word starting positions
      String phrase = updatedwordSequence.TagtoString().substring(start, end);
      int count = new StringTokenizer(phrase).countTokens();
      int wordSeqStart = updatedwordSequence.TagStringPosToSequencePos.get(start);
      WordSequence discoveredSequence = new WordSequence();
      String instanceFound = "";
      int compID = 0;
      String N0 = null, V = null, N1 = null, P = null, N2 = null;
      boolean CDfound = false;
      while (count > 0) {
        count--;
        if (updatedwordSequence.tags.get(wordSeqStart).equals("DT") || updatedwordSequence.tags.get(wordSeqStart).equals("CD")) {
          wordSeqStart++;
          continue;
        }
        discoveredSequence.appendTag(updatedwordSequence.tags.get(wordSeqStart));
        String word = updatedwordSequence.words.get(wordSeqStart++);
        if (idToNPMap.get(word) != null) {
          word = idToNPMap.get(word).toString().trim();
        }
        discoveredSequence.appendWord(word);
        if (compID == 0) {
          N0 = word;
        } else if (compID == 1) {
          V = word;
        } else if (compID == 2) {
          N1 = word;
        } else if (compID == 3) {
          P = word;
        } else if (compID == 4) {
          N2 = word;
        }

        compID++;
      }

      // filter out said verb to be and only keep frequent preps
      if (Preposition.istoBeVerb(V) || !Preposition.isFrequentPreposition(P)) continue;

      instanceFound = N0 + "\t" + V + "\t" + N1 + "\t" + P + "\t" + N2;
      // if (CDfound)
      System.out.println("[1]" + instanceFound);
      result.add(instanceFound);
    }

    quadraplesPattern = Pattern.compile(ppambig_quad_no_groupingTwoVerbs);
    matcher = quadraplesPattern.matcher(updatedwordSequence.TagtoString());
    while (matcher.find()) {
      // start and end of character starting positions
      int start = matcher.start();
      int end = matcher.end();

      // find actual word starting positions
      String phrase = updatedwordSequence.TagtoString().substring(start, end);
      int count = new StringTokenizer(phrase).countTokens();
      int wordSeqStart = updatedwordSequence.TagStringPosToSequencePos.get(start);
      WordSequence discoveredSequence = new WordSequence();
      String instanceFound = "";
      int compID = 0;
      String N0 = null, V = null, N1 = null, P = null, N2 = null;
      while (count > 0) {
        count--;
        if (updatedwordSequence.tags.get(wordSeqStart).equals("DT") || updatedwordSequence.tags.get(wordSeqStart).equals("CD")) {
          wordSeqStart++;
          continue;
        }
        discoveredSequence.appendTag(updatedwordSequence.tags.get(wordSeqStart));
        String word = updatedwordSequence.words.get(wordSeqStart++);
        if (idToNPMap.get(word) != null) {
          word = idToNPMap.get(word).toString().trim();
        }
        discoveredSequence.appendWord(word);
        if (compID == 0) {
          N0 = word;
        } else if (compID == 1) {
          V = word;
        } else if (compID == 2) {
          // V = V + " " + word;
          V = word; // just skip first verb, it is usually, is/has/were/had/
        } else if (compID == 3) {
          N1 = word;
        } else if (compID == 4) {
          P = word;
        } else if (compID == 5) {
          N2 = word;
        }

        compID++;
      }

      if (Preposition.istoBeVerb(V) || !Preposition.isFrequentPreposition(P)) continue;

      instanceFound = N0 + "\t" + V + "\t" + N1 + "\t" + P + "\t" + N2;
      // if (CDfound)
      System.out.println("[2]" + instanceFound);
      result.add(instanceFound);
    }
    return result;
  }



  public List<String> findPPAs(WordSequence wordSequence) {
    List<String> result = new ArrayList<String>();
    for (String ppambig : ppAttachpatternsgrouping) {
      Pattern quadraplesPattern = Pattern.compile(ppambig);
      Map<String, Integer> namedGroups = null;
      try {
        namedGroups = getNamedGroups(quadraplesPattern);
      } catch (Exception e) {
        // Just an example here. You need to handle the Exception properly
        e.printStackTrace();
      }
      Matcher matcher = quadraplesPattern.matcher(wordSequence.TagtoString());
      while (matcher.find()) {

        int start = -1;
        String verb = "", prep = "", np0 = "", np1 = "", np2 = "";
        for (String name : namedGroups.keySet()) {
          start = matcher.start(namedGroups.get(name));
          // int end = matcher.end(namedGroups.get(name));

          String matchedString = matcher.group(namedGroups.get(name));
          int count = new StringTokenizer(matchedString).countTokens();
          int wordSeqStart = wordSequence.TagStringPosToSequencePos.get(start);
          WordSequence discoveredSequence = new WordSequence();
          while (count > 0) {
            discoveredSequence.appendWord(wordSequence.words.get(wordSeqStart++));
            count--;
          }

          //  System.out.print(name + "=" + matchedString + "=" + discoveredSequence.WordtoString() + ", ");
          if (name.indexOf("name0") >= 0) {
            np0 = discoveredSequence.WordtoString() + "\t" + matchedString;

          } else if (name.indexOf("name1") >= 0) {
            np1 = discoveredSequence.WordtoString() + "\t" + matchedString;

          } else if (name.indexOf("verb") >= 0) {
            verb = discoveredSequence.WordtoString() + "\t" + matchedString;

          } else if (name.indexOf("prep") >= 0) {
            prep = discoveredSequence.WordtoString() + "\t" + matchedString;

          } else if (name.indexOf("name2") >= 0) {
            np2 = discoveredSequence.WordtoString() + "\t" + matchedString;
          }
        }

        if (start >= 0) {
          result.add(np0 + "\t" + verb + "\t" + np1 + "\t" + prep + "\t" + np2);
          // System.out.println(verb + "\t" + np1 + "\t" + prep + "\t" + np2 + "\t" + wordSequence.WordtoString() + "\t" + wordSequence.tagString);
        }
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Integer> getNamedGroups(Pattern regex) throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {

    Method namedGroupsMethod = Pattern.class.getDeclaredMethod("namedGroups");
    namedGroupsMethod.setAccessible(true);

    Map<String, Integer> namedGroups = null;
    namedGroups = (Map<String, Integer>) namedGroupsMethod.invoke(regex);

    if (namedGroups == null) {
      throw new InternalError();
    }

    return Collections.unmodifiableMap(namedGroups);
  }


  public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
    AttachmentExtractor me = new AttachmentExtractor();

  }

}
