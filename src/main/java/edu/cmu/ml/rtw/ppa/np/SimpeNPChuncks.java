package edu.cmu.ml.rtw.ppa.np;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.ml.rtw.ppa.util.FinalData;
import edu.cmu.ml.rtw.ppa.util.WordSequence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * 
 * @author ndapa
 * 
 */
public class SimpeNPChuncks {

  // regular expressions to capture noun phrases
  final static String nounPhrase = "((NNP){1}(\\s){0,}){1,}(NNPS\\s){0,}(NNS\\s){0,}((NN){1,}(\\s){0,}){0,}";
  final static String nounPhraseIN = "((NNP){1}(\\s){0,}){1,}((OF){1}(\\s){0,}){1}((NNP){1}(\\s){0,}){1,}";
  public static final String nounPhrase2 = "(JJ\\s){0,}((NN){1}(\\s){0,}){1,}((NNS){1}(\\s){0,}){0,}";
  public static final String nounPhrase3 = "(JJ\\s){0,}((NNS){1}(\\s){0,}){1,}((NN){1}(\\s){0,}){0,}";
  public static final String np_currency = "(\\s\\$){1}(\\sCD){1,}";
  public static final String anynumber = "(CD){1}";
  public static final String percentage = "(CD\\s){1}(NN){1}";

  public static final String percentagepattern = "\\d{1,3}\\s{1,}(%){1}";

  public static final String npDay1 = "(CD\\s){1}(NNP\\s){1}(CD){1}";

  public static final String npDay2 = "(NNP\\s{1,}){1}(CD){1}(\\s){0,}(CD){0,1}";

  // dd/Month/YYYY
  public static final String date1 = "((\\d){1,2}(/|-|\\s{1,}){1}(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|June?|July?|Aug(ust)?|Sep(t(ember)?)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(/|-|\\s{1,}){1}(\\d){4})";

  // dd/mm/YYYY
  public static final String date2 = "\\d{1,2}(/|-|\\s{1,})\\d{1,2}(/|-|\\s{1,})\\d{2,4}";

  // YYYY/mm/mm
  public static final String date3 = "\\d{4}(/|-|\\s{1,})\\d{1,2}(/|-|\\s{1,})\\d{1,2}";
  // public static final String months = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|June?|July?|Aug(ust)?|Sep(t(ember)?)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)";

  // Month/dd/yyy
  public static final String date4 = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|June?|July?|Aug(ust)?|Sep(t(ember)?)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(\\s)(\\d{0,2})\\s{0,}\\d{4}";
  public static final String date5_justyear = "\\d{4}";

  final String nounPhrasePronoun = "(PRP)";

  //  final String date_prefix = "<nell_date>";
  //  final String percentage_prefix = "<nell_percent>";
  //  final String currency_prefix = "<nell_amount>";
  final String date_prefix = "";
  final String percentage_prefix = "";
  final String currency_prefix = "";

  private static List<String> nounPhrases = new ArrayList<String>(Arrays.asList(new String[] {
      percentage,
      npDay1,
      npDay2,
      np_currency,
      anynumber,
      nounPhraseIN,
      nounPhrase,
      nounPhrase2,
      nounPhrase3 }));
  private static List<String> nounPhrasesProper = new ArrayList<String>(
      Arrays.asList(new String[] { np_currency, anynumber, nounPhrase, nounPhraseIN }));

  int maxSentenceSize = 200;
  boolean lowerCaseNPs = false;
  MaxentTagger tagger;

  public SimpeNPChuncks() throws Exception {
    tagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
  }

  /** Patterns with not content-bearing words */
  public boolean isStopword(String word) {
    if (FinalData.stopWords.contains(word)) {
      ;
    } else return false;

    return true;
  }

  /**
   * Returns a list of noun phrases
   * 
   * @return
   */
  public List<NounPhrase> locateNounPhrasePosition(ArrayList<TaggedWord> taggedSentence) {
    List<NounPhrase> nounPhraseList = new ArrayList<NounPhrase>();
    WordSequence wordSequence = new WordSequence();
    for (int twPos = 0; twPos < taggedSentence.size(); twPos++) {
      TaggedWord tw = taggedSentence.get(twPos);
      String word = tw.word().toLowerCase();
      if (word.equals("of")) {
        wordSequence.appendTag("OF");
      } else {
        wordSequence.appendTag(tw.tag());
      }

      wordSequence.appendWord(tw.word());

    }
    for (int patternId = 0; patternId < nounPhrases.size(); patternId++) {
      String nounphrasePattern = nounPhrases.get(patternId);
      Pattern familyNameSuffixPattern = Pattern.compile(nounphrasePattern);
      Matcher matcher = familyNameSuffixPattern.matcher(wordSequence.TagtoString());

      while (matcher.find()) {

        // start and end of character starting positions
        int start = matcher.start();
        int end = matcher.end();

        // find actual word starting positions
        // System.out.println(wordSequence.WordtoString() + "\t\t" + wordSequence.TagtoString());
        String phrase = wordSequence.TagtoString().substring(start, end);
        int count = new StringTokenizer(phrase).countTokens();
        //System.out.println("\t" + phrase + "\t" + start + "\t" + end);

        if (nounphrasePattern.equals(np_currency)) {
          start++;
        }

        int wordSeqStart = wordSequence.TagStringPosToSequencePos.get(start);
        String entityPhrase = "";
        while (count > 0) {
          entityPhrase = entityPhrase + wordSequence.words.get(wordSeqStart++) + " ";
          count--;
        }
        NounPhrase np = new NounPhrase(entityPhrase, wordSequence.TagStringPosToSequencePos.get(start).intValue(), (wordSeqStart - 1));
        if (nounphrasePattern.equals(nounPhrase2) || nounphrasePattern.equals(nounPhrase3)) {
          np.commonNoun = true;
        }

        if (nounphrasePattern.equals(npDay1) || nounphrasePattern.equals(npDay2)) {
          if (Pattern.matches(date1, entityPhrase.trim()) || Pattern.matches(date4, entityPhrase.trim())) {
            NounPhrase np_annotated = new NounPhrase(date_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
            np_annotated.commonNoun = true;
            np_annotated.numberNoun = true;
            nounPhraseList.add(np_annotated);
          }

        } else if (nounphrasePattern.equals(anynumber)) {
          //if (Pattern.matches(date2, entityPhrase.trim()) || Pattern.matches(date3, entityPhrase.trim())) {

          if (Pattern.matches(date5_justyear, entityPhrase.trim())) {
            NounPhrase np_annotated = new NounPhrase(date_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
            np_annotated.commonNoun = true;
            np_annotated.numberNoun = true;
            nounPhraseList.add(np_annotated);

          }
        } else if (nounphrasePattern.equals(percentage)) {
          if (Pattern.matches(percentagepattern, entityPhrase.trim())) {
            NounPhrase np_annotated = new NounPhrase(percentage_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
            np_annotated.commonNoun = true;
            np_annotated.percentNoun = true;
            np_annotated.numberNoun = true;
            nounPhraseList.add(np_annotated);
          }
        } else if (nounphrasePattern.equals(np_currency)) {
          NounPhrase np_annotated = new NounPhrase(currency_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
          np_annotated.commonNoun = true;
          np_annotated.percentNoun = true;
          np_annotated.numberNoun = true;
          nounPhraseList.add(np_annotated);
        } else {
          // dont keep track of months here
          //          if (Pattern.matches(months, entityPhrase.trim())) {
          //            //System.out.println(np.toString() + " ");
          //            //nounPhraseList.add(np);
          //          } 
          //          if (entityPhrase.trim().equals("%")) {
          //
          //          } else {

          nounPhraseList.add(np);
          // }
        }
        // nounPhraseList.add(np);

      }

    }

    return nounPhraseList;
  }

  /**
   * Returns a list of noun phrases
   * 
   * @return
   */
  public List<NounPhrase> locateNounPhrasePosition(WordSequence wordSequence) {
    List<NounPhrase> nounPhraseList = new ArrayList<NounPhrase>();

    for (int patternId = 0; patternId < nounPhrases.size(); patternId++) {
      String nounphrasePattern = nounPhrases.get(patternId);
      Pattern familyNameSuffixPattern = Pattern.compile(nounphrasePattern);
      Matcher matcher = familyNameSuffixPattern.matcher(wordSequence.TagtoString());

      while (matcher.find()) {

        // start and end of character starting positions
        int start = matcher.start();
        int end = matcher.end();

        // find actual word starting positions
        // System.out.println(wordSequence.WordtoString() + "\t\t" + wordSequence.TagtoString());
        String phrase = wordSequence.TagtoString().substring(start, end);
        int count = new StringTokenizer(phrase).countTokens();
        //System.out.println("\t" + phrase + "\t" + start + "\t" + end);

        if (nounphrasePattern.equals(np_currency)) {
          start++;
        }

        int wordSeqStart = wordSequence.TagStringPosToSequencePos.get(start);
        String entityPhrase = "";
        while (count > 0) {
          entityPhrase = entityPhrase + wordSequence.words.get(wordSeqStart++) + " ";
          count--;
        }
        NounPhrase np = new NounPhrase(entityPhrase, wordSequence.TagStringPosToSequencePos.get(start).intValue(), (wordSeqStart - 1));
        if (nounphrasePattern.equals(nounPhrase2) || nounphrasePattern.equals(nounPhrase3)) {
          np.commonNoun = true;
        }

        if (nounphrasePattern.equals(npDay1) || nounphrasePattern.equals(npDay2)) {
          if (Pattern.matches(date1, entityPhrase.trim()) || Pattern.matches(date4, entityPhrase.trim())) {
            NounPhrase np_annotated = new NounPhrase(date_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
            np_annotated.commonNoun = true;
            np_annotated.numberNoun = true;
            nounPhraseList.add(np_annotated);
          }

        } else if (nounphrasePattern.equals(anynumber)) {
          //if (Pattern.matches(date2, entityPhrase.trim()) || Pattern.matches(date3, entityPhrase.trim())) {

          if (Pattern.matches(date5_justyear, entityPhrase.trim())) {
            NounPhrase np_annotated = new NounPhrase(date_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
            np_annotated.commonNoun = true;
            np_annotated.numberNoun = true;
            nounPhraseList.add(np_annotated);

          }
        } else if (nounphrasePattern.equals(percentage)) {
          if (Pattern.matches(percentagepattern, entityPhrase.trim())) {
            NounPhrase np_annotated = new NounPhrase(percentage_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
            np_annotated.commonNoun = true;
            np_annotated.percentNoun = true;
            np_annotated.numberNoun = true;
            nounPhraseList.add(np_annotated);
          }
        } else if (nounphrasePattern.equals(np_currency)) {
          NounPhrase np_annotated = new NounPhrase(currency_prefix + np.toString().trim(), np.startInWordSequence, np.endInWordSequence);
          np_annotated.commonNoun = true;
          np_annotated.percentNoun = true;
          np_annotated.numberNoun = true;
          nounPhraseList.add(np_annotated);
        } else {
          // dont keep track of months here
          //          if (Pattern.matches(months, entityPhrase.trim())) {
          //            //System.out.println(np.toString() + " ");
          //            //nounPhraseList.add(np);
          //          } 
          //          if (entityPhrase.trim().equals("%")) {
          //
          //          } else {

          nounPhraseList.add(np);
          // }
        }
        // nounPhraseList.add(np);

      }

    }

    return nounPhraseList;
  }

  /**
   * Returns a list of noun phrases
   * 
   * @return
   */
  public List<NounPhrase> locateNounPhrasePositionProperNouns(WordSequence wordSequence) {
    List<NounPhrase> nounPhraseList = new ArrayList<NounPhrase>();

    for (String nounphrasePattern : nounPhrasesProper) {
      Pattern familyNameSuffixPattern = Pattern.compile(nounphrasePattern);
      Matcher matcher = familyNameSuffixPattern.matcher(wordSequence.TagtoString());

      while (matcher.find()) {

        // start and end of character starting positions
        int start = matcher.start();
        int end = matcher.end();

        // find actual word starting positions
        String phrase = wordSequence.TagtoString().substring(start, end);
        int count = new StringTokenizer(phrase).countTokens();
        int wordSeqStart = wordSequence.TagStringPosToSequencePos.get(start);
        String entityPhrase = "";
        while (count > 0) {
          entityPhrase = entityPhrase + wordSequence.words.get(wordSeqStart++) + " ";
          count--;
        }
        NounPhrase np = new NounPhrase(entityPhrase, wordSequence.TagStringPosToSequencePos.get(start).intValue(), (wordSeqStart - 1));
        if (nounphrasePattern.equals(nounPhrase2) || nounphrasePattern.equals(nounPhrase3)) {
          np.commonNoun = true;
        }
        nounPhraseList.add(np);

      }

    }

    return nounPhraseList;
  }

  /** Clean the patterns **/
  private String textHashCleanPattern(String pattern) {
    StringBuilder b = new StringBuilder("");
    int tags = 0;
    int words = 0;
    StringTokenizer st = new StringTokenizer(pattern);
    while (st.hasMoreTokens()) {
      String word = st.nextToken();
      // // if it starts with strange characters
      // if (words == 0 && (word.replaceAll("[^a-zA-Z@]", "").length() ==
      // 0)) {
      // return "";
      // }
      if (word.startsWith("[[") || FinalData.stopWords.contains(word)) {
        b.append(word + " ");
        tags++;
        words++;
      } else {
        word = word.replaceAll("[^a-zA-Z@]", "");
        if (word.length() > 0 && Character.isLowerCase(word.charAt(0))) {
          b.append(word + " ");
          words++;
        }
      }
    }
    if (tags == words) return "";
    return b.toString().trim();

  }

  public static void main(String[] args) throws Exception {

  }

}
