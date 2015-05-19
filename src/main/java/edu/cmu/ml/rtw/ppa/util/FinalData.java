package edu.cmu.ml.rtw.ppa.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FinalData {

  public static final List<String> months = new ArrayList<String>(Arrays
      .asList(new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
          "September", "October", "November", "December"
      }));
  
  public static final List<String> calendar = new ArrayList<String>(Arrays
      .asList(new String[] { "january", "february", "march", "april", "may", "june", "july", "august",
          "september", "october", "november", "december", "monday", "tuesday","wednesday","thursday","friday",
          "saturday","sunday"
      }));
  
  
	public static final List<String> otherStopwords = new ArrayList<String>(Arrays
			.asList(new String[] { "a", "about", "above", "above", "across", "after", "afterwards",
					"again", "against", "all", "almost", "alone", "along", "already", "also",
					"although", "always", "am", "among", "amongst", "amoungst", "amount", "an",
					"and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere",
					"are", "around", "as", "at", "back", "be", "became", "because", "become",
					"becomes", "becoming", "been", "before", "beforehand", "behind", "being",
					"below", "beside", "besides", "between", "beyond", "bill", "both", "bottom",
					"but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt",
					"cry", "de", "describe", "detail", "do", "does", "done", "down", "due", "during",
					"each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty",
					"enough", "etc", "even", "ever", "every", "everyone", "everything",
					"everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire",
					"first", "five", "for", "former", "formerly", "forty", "found", "four", "from",
					"front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have",
					"he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon",
					"hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie",
					"if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself",
					"keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many",
					"may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most",
					"mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither",
					"never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone",
					"nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once",
					"one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours",
					"ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put",
					"rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious",
					"several", "she", "should", "show", "side", "since", "sincere", "six", "sixty",
					"so", "some", "somehow", "someone", "something", "sometime", "sometimes",
					"somewhere", "still", "such", "system", "take", "ten", "than", "that", "the",
					"their", "them", "themselves", "then", "thence", "there", "thereafter",
					"thereby", "therefore", "therein", "thereupon", "these", "they", "thickv",
					"thin", "third", "this", "those", "though", "three", "through", "throughout",
					"thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve",
					"twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via",
					"was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever",
					"where", "whereafter", "whereas", "whereby", "wherein", "whereupon",
					"wherever", "whether", "which", "while", "whither", "who", "whoever", "whole",
					"whom", "whose", "why", "will", "with", "within", "without", "would", "yet",
					"you", "your", "yours", "yourself", "yourselves", "the", "zu"

			}));
	public static final List<String> stopWords = new ArrayList<String>(Arrays.asList(new String[] {
			"a", "about", "above", "after", "afterwards", "again", "against", "all", "almost",
			"alone", "along", "also", "although", "always", "am", "among", "amongst", "an", "and",
			"another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways",
			"anywhere", "apart", "appear", "appreciate", "appropriate", "around", "as", "aside",
			"ask", "asking", "associated", "at", "available", "away", "awfully", "b", "because",
			"before", "beforehand", "behind", "being", "below", "beside", "besides", "best",
			"better", "between", "beyond", "both", "brief", "but", "by", "c", "c'mon", "c's",
			"cause", "certain", "certainly", "changes", "clearly", "co", "com", "concerning",
			"consequently", "corresponding", "course", "currently", "d", "definitely", "described",
			"despite", "different", "down", "downwards", "during", "e", "each", "edu", "eg",
			"eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et",
			"etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere",
			"ex", "exactly", "example", "except", "f", "far", "few", "fifth", "first", "five",
			"for", "forth", "four", "from", "further", "furthermore", "g", "greetings", "h",
			"happens", "hardly", "he", "he's", "hello", "help", "hence", "her", "here", "here's",
			"hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself",
			"his", "hither", "hopefully", "how", "howbeit", "however", "i", "i'd", "i'll", "i'm",
			"i've", "ie", "if", "immediate", "in", "inasmuch", "inc", "indeed", "inner", "insofar",
			"instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll", "it's", "its",
			"itself", "j", "just", "k", "l", "last", "lately", "later", "latter", "latterly",
			"least", "less", "lest", "let", "let's", "likely", "little", "ltd", "m", "mainly",
			"many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more",
			"moreover", "most", "mostly", "much", "must", "my", "myself", "n", "name", "namely",
			"nd", "near", "nearly", "necessary", "need", "needs", "neither", "never",
			"nevertheless", "next", "nine", "no", "nobody", "non", "none", "noone", "nor",
			"normally", "not", "nothing", "now", "nowhere", "o", "obviously", "of", "off", "often",
			"oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other",
			"others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over",
			"overall", "own", "p", "particular", "particularly", "per", "perhaps", "placed",
			"please", "plus", "possible", "presumably", "probably", "provides", "q", "que",
			"quite", "qv", "r", "rather", "rd", "re", "really", "reasonably", "regardless",
			"relatively", "respectively", "s", "same", "saw", "second", "secondly", "see",
			"seeing", "self", "selves", "sensible", "sent", "serious", "seriously", "seven",
			"several", "shall", "she", "since", "six", "so", "some", "somebody", "somehow",
			"someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon",
			"sorry", "still", "sub", "such", "sup", "sure", "t", "t's", "th", "than", "thank",
			"thanks", "thanx", "that", "that's", "thats", "the", "their", "theirs", "them",
			"themselves", "then", "thence", "there", "there's", "thereafter", "thereby",
			"therefore", "therein", "theres", "thereupon", "these", "they", "they'd", "they'll",
			"they're", "they've", "think", "third", "this", "thorough", "thoroughly", "those",
			"though", "three", "through", "throughout", "thru", "thus", "to", "together", "too",
			"toward", "towards", "truly", "twice", "two", "u", "un", "under", "unfortunately",
			"unless", "unlikely", "until", "unto", "up", "upon", "us", "useful", "usually", "uucp",
			"v", "value", "various", "very", "via", "viz", "vs", "w", "was", "way", "we", "we'd",
			"we'll", "we're", "we've", "well", "went", "what", "what's", "whatever", "when",
			"whence", "whenever", "where", "where's", "whereafter", "whereas", "whereby",
			"wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who",
			"who's", "whoever", "whole", "whom", "whose", "why", "willing", "wish", "with",
			"within", "without", "wonder", "x", "y", "yes", "yet", "you", "you'd", "you'll",
			"you're", "you've", "your", "yours", "yourself", "yourselves", "z", "zero", }));
}
