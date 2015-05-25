package edu.cmu.ml.rtw.ppa.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.smu.tspell.wordnet.*;

/**
 * @author ndapa
 * 
 */

public class WordnetThesaurus {

  static WordNetDatabase database;
  static final String dictionaryLocation= "dict";

  private static boolean isGenericType(String type) {
    String ar = type;
    // multi words
    if(ar.split(" ").length> 1) return true;

    if (ar.indexOf("thing") >= 0 || ar.indexOf("wikipedia") >= 0 || ar.indexOf("geoclass_") >= 0
        || ar.indexOf("wikicategory") >= 0 || ar.indexOf("entity") >= 0 || ar.indexOf("object") >= 0 || ar.indexOf("cognition") >= 0
        || ar.indexOf("caus") >= 0 || ar.indexOf("artifact") >= 0 || ar.indexOf("whole") >= 0 || ar.indexOf("organism") >= 0
        || ar.indexOf("abstraction") >= 0 || ar.indexOf("unit") >= 0 || ar.equals("being")) {
      return true;
    }
    return false;

  }

  /** Returns synonyms and antonyms of a word **/
  public static HashSet<String> getParentsRecursivelyNounsOnly(String wordForm) {
    HashSet<String> result = new HashSet<String>();
    if (database == null) {
      System.setProperty("wordnet.database.dir",dictionaryLocation );
      database = WordNetDatabase.getFileInstance();
    }
    // Synset[] synsets = database.
    Synset[] synsets = database.getSynsets(wordForm);
    // if phrase is not in wordnet, or it is has too many senses
    // if too many senses it is difficult to determine which one is referred
    if (synsets.length < 1 || synsets.length > 4  ) return result;

    // go through each synset meaning of the word/phrase
    for (int i = 0; i < synsets.length; i++) {

      // get the word forms of the synset
      //System.out.println(i + ": - current sense");
      int level = 0;
      ArrayList<String> answers = new ArrayList<String>();
      String[] wordForms = synsets[i].getWordForms();
      for (int j = 0; j < wordForms.length; j++) {
        //System.out.print((j > 0 ? ", " : "") + wordForms[j]);
        if (isGenericType(wordForms[j])) continue;
        answers.add(level + "].\t" + wordForms[j]);
       // result.add(wordForms[j]);
      }

      SynsetType type = synsets[i].getType();

      // we are dealing with nouns only
      if (type.compareTo(SynsetType.NOUN) == 0) {

        //System.out.println("\tHYPER :");
        Synset[] parentsHypernymSet = ((NounSynset) synsets[i]).getHypernyms();
        List<Synset> currentParents = new ArrayList<Synset>();
        for (Synset item : parentsHypernymSet) {
          currentParents.add(item);
        }

        while (!currentParents.isEmpty()) {
          level++;
          List<Synset> newcurrentParents = new ArrayList<Synset>();

          for (Synset item : currentParents) {

            parentsHypernymSet = ((NounSynset) item).getHypernyms();
            for (Synset item2 : parentsHypernymSet) {
              newcurrentParents.add(item2);
            }

            String[] wordFormsParent = item.getWordForms();
            for (int j = 0; j < wordFormsParent.length; j++) {
              if (isGenericType(wordFormsParent[j])) continue;
              answers.add(level + "].\t" + wordFormsParent[j]);
              result.add(wordFormsParent[j]);
             // System.out.println(level + "].\t" + wordFormsParent[j]);
            }
          }

          currentParents.clear();
          currentParents.addAll(newcurrentParents);
          newcurrentParents.clear();
        }
      }

      for (int j = 0; j < answers.size(); j++) {
       // System.out.println(answers.get(j));

      }
    }

    return result;
  }

  /** Returns synonyms and antonyms of a word **/
  public static ArrayList<WordNetSynset> getSynsetSynonymAntonyms(String wordForm) {
    ArrayList<WordNetSynset> result = new ArrayList<WordNetSynset>();
    if (database == null) {
      System.setProperty("wordnet.database.dir", dictionaryLocation);
      database = WordNetDatabase.getFileInstance();
    }
    // Synset[] synsets = database.
    Synset[] synsets = database.getSynsets(wordForm);
    // if phrase is not in wordnet, or it is has too many senses
    // if too many senses it is difficult to determine which one is referred
    if (synsets.length < 1) return result;

    for (int i = 0; i < synsets.length; i++) {

      WordSense[] wordFormsAntonyms = synsets[i].getAntonyms(wordForm);

      HashSet<String> surface_forms = new HashSet<String>();
      HashSet<String> antonym_surface_forms = new HashSet<String>();

      // various terms for referring to this word
      String[] wordForms = synsets[i].getWordForms();
      for (int j = 0; j < wordForms.length; j++) {
        surface_forms.add(wordForms[j]);
      }

      // antonyms
      for (int j = 0; j < wordFormsAntonyms.length; j++) {
        antonym_surface_forms.add(wordFormsAntonyms[j].getWordForm());
      }

      result.add(new WordNetSynset(surface_forms, antonym_surface_forms));
    }

    return result;
  }

  /** Returns the relatedness of a pair of words or phrases */
  public static double getWordRelatedness(String word1, String word2) {
    ArrayList<WordNetSynset> relatedwords = getSynsetSynonymAntonyms(word1);

    // synonyms
    HashSet<String> similarwords = new HashSet<String>();
    // antonyms
    HashSet<String> disimilarwords = new HashSet<String>();

    for (WordNetSynset synset : relatedwords) {
      similarwords.addAll(synset.surfaceForms);
      disimilarwords.addAll(synset.antonyms);
    }

    if (similarwords.contains(word2)) return 1;

    if (disimilarwords.contains(word2)) return -1;

    return 0;
  }

  public static void main(String[] args) {
    if (database == null) {
      System.setProperty("wordnet.database.dir", dictionaryLocation);
    }

    // if (args.length > 0) {
    // Concatenate the command-line arguments
    // StringBuffer buffer = new StringBuffer();
    // for (int i = 0; i < args.length; i++) {
    // buffer.append((i > 0 ? " " : "") + args[i]);
    // }
    // String wordForm = buffer.toString();
    String[] terms = { "spaghetti", "apple", "banana", "girl", "bill clinton", "Bill Clinton","Alice","alice","bob", "Bob"};

    for (String term : terms) {
      getParentsRecursivelyNounsOnly(term);
    }

    //    for (String wordForm : terms) {
    //      // String wordForm = "lion";
    //      // Get the synsets containing the word form
    //      WordNetDatabase database = WordNetDatabase.getFileInstance();
    //      Synset[] synsets = database.getSynsets(wordForm);
    //
    //      // Display the word forms and definitions for synsets retrieved
    //      if (synsets.length > 0) {
    //        System.out.println("The following synsets contain '" + wordForm + "' or a possible base form " + "of that text:");
    //
    //        // go through each synset meaning of the word/phrase
    //        for (int i = 0; i < synsets.length; i++) {
    //
    //          WordSense[] wordFormsAntonyms = synsets[i].getAntonyms(wordForm);
    //
    //          //if (wordFormsAntonyms.length < 1) continue;
    //
    //          // get the word forms of the synset
    //          System.out.print(i + ":");
    //          String[] wordForms = synsets[i].getWordForms();
    //          for (int j = 0; j < wordForms.length; j++) {
    //            System.out.print((j > 0 ? ", " : "") + wordForms[j]);
    //          }
    //
    //          // print antonyms
    //          System.out.print(wordFormsAntonyms.length > 0 ? " " : "");
    //          for (int j = 0; j < wordFormsAntonyms.length; j++) {
    //            System.out.print((j > 0 ? ", --" : "--") + wordFormsAntonyms[j].getWordForm());
    //          }
    //
    //          System.out.println("\tDEFN: " + synsets[i].getDefinition());
    //          SynsetType type = synsets[i].getType();
    //          if (type.compareTo(SynsetType.VERB) == 0 || type.compareTo(SynsetType.NOUN) == 0) {
    //
    //            System.out.println("\tHYPER :");
    //            Synset[] parentsHypernymSet = type.compareTo(SynsetType.VERB) == 0 ? ((VerbSynset) synsets[i]).getHypernyms() : ((NounSynset) synsets[i])
    //                .getHypernyms();
    //            for (int ii = 0; ii < parentsHypernymSet.length; ii++) {
    //              System.out.print("\t" + ii + ":");
    //              String[] wordFormsii = parentsHypernymSet[ii].getWordForms();
    //              for (int j = 0; j < wordFormsii.length; j++) {
    //                System.out.print((j > 0 ? ", " : "") + wordFormsii[j]);
    //              }
    //              System.out.println(": " + parentsHypernymSet[ii].getDefinition());
    //            }
    //          }
    //        }
    //      }
    //
    //      else {
    //        System.err.println("No synsets exist that contain " + "the word form '" + wordForm + "'");
    //      }
    //    }

    // } else {
    // System.err.println("You must specify "
    // + "a word form for which to retrieve synsets.");
    // }
  }
}
