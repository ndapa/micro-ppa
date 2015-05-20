package edu.cmu.ml.rtw.ppa.predict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import edu.cmu.ml.rtw.generic.data.annotation.AnnotationType;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.AnnotationTypeNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.DocumentNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.DocumentNLPInMemory;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.Language;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.PoSTag;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.AnnotationTypeNLP.Target;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.AnnotatorSentence;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.PipelineNLP;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.PipelineNLPStanford;
import edu.cmu.ml.rtw.generic.util.Pair;
import edu.cmu.ml.rtw.ppa.util.FinalData;
import edu.cmu.ml.rtw.ppa.util.WordSequence;
import edu.cmu.ml.rtw.ppa.util.WordnetThesaurus;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Distribution;

public class PPADisambiguator implements AnnotatorSentence<String> {

  public static final AnnotationTypeNLP<String> PPA_DISAMBIG = new AnnotationTypeNLP<String>("PPA_DISAMBIG", String.class, Target.SENTENCE);

  AttachmentExtractor extractor;
  Morphology lemmatizer;
  LinearClassifier<String, String> classifier;
  boolean stemming = true;

  static final AnnotationType<?>[] REQUIRED_ANNOTATIONS = new AnnotationType<?>[] {
      AnnotationTypeNLP.TOKEN,
      AnnotationTypeNLP.SENTENCE,
      AnnotationTypeNLP.POS };

  public String getName() {
    return "mirco-ppa";
  }

  public boolean measuresConfidence() {
    return true;
  }

  public AnnotationType<String> produces() {
    return PPA_DISAMBIG;
  }

  public AnnotationType<?>[] requires() {
    return REQUIRED_ANNOTATIONS;
  }

  public Map<Integer, Pair<String, Double>> annotate(DocumentNLP document) {
    PPAQuad quad;
    Map<Integer, Pair<String, Double>> annotations = new HashMap<Integer, Pair<String, Double>>();
    for (int i = 0; i < document.getSentenceCount(); i++) {
      System.out.println(i + "]\t" + document.getSentence(i));
      List<PoSTag> tags = document.getSentencePoSTags(i);
      List<String> words = document.getSentenceTokenStrs(i);

      WordSequence wordSequence = new WordSequence();
      for (int j = 0; j < words.size(); j++) {
        System.out.println("\t" + j + "." + words.get(j) + "/" + tags.get(j).name() + "/");
        wordSequence.appendTag(tags.get(j).name());
        wordSequence.appendWord(words.get(j));
      }
      List<String> tuples = extractor.findPPAsNoGroups(wordSequence);

      for (String instance : tuples) {
        quad = getPPQuad(instance);
        PPAQuad qdPredicted = getPPAPrediction(quad);
        annotations.put(i, new Pair<String, Double>(quad.toString() + "\t" + qdPredicted.label, qdPredicted.confidenceOfLabel));
        // System.out.println(quad.toString() + "\t" + getPPAPrediction(quad).label);

        //  System.out.println(qd.toString() + "|\t" + qdPredicted.label+" @ "+qdPredicted.confidenceOfLabel) ;
      }

      // System.out.println(i+")\t"+ document.List<PoSTag> getSentencePoSTags
    }

    //        List<PPAQuad> quads =  getQuadsNYCLabelled("src/main/resources/gold_NYC.tsv");
    //        for(PPAQuad qd: quads){
    //          PPAQuad qdPredicted = getPPAPrediction(qd);
    //          System.out.println(qd.toString() + "|\t" + qdPredicted.label+" @ "+qdPredicted.confidenceOfLabel) ;
    //        }
    //    for (Annotation annotation : annotations)
    //      System.out.println(annotation.toJsonString());
    return annotations;
  }

  public PPAQuad getPPQuad(String instance) {
    if (lemmatizer == null) {
      lemmatizer = new Morphology();
    }
    String[] parts = instance.toLowerCase().split("\t");
    int i = 0;
    String N0 = parts[i++].trim();
    String V = parts[i++].trim();
    String N1 = parts[i++].trim();
    String prep = parts[i++].trim();
    String N2 = parts[i++].trim();

    String stemmed = lemmatizer.lemma(V, V);
    PPAQuad quad = new PPAQuad(stemmed, V, N1, prep, N2, "");
    quad.setN0(N0);
    return quad;
  }

  public PPAQuad getPPAPrediction(PPAQuad quad) {

    if (extractor == null) extractor = new AttachmentExtractor();
    if (lemmatizer == null) lemmatizer = new Morphology();

    String classifierlocation = "wsj_wkp_nyt.lcf";

    //Get file from resources folder
    // ClassLoader classLoader = getClass().getClassLoader();
    // File file = new File(classLoader.getResource(classifierlocation).getFile());

    if (classifier == null) {
      try {
        //URL myTestURL = ClassLoader.getSystemResource(classifierlocation);
        //File file = new File(myTestURL.toURI());

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(classifierlocation);
        File file = new File("ppa-classfier.clf");
        FileOutputStream outputStream = new FileOutputStream(file);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
          outputStream.write(bytes, 0, read);
        }
        outputStream.close();
        classifier = (LinearClassifier<String, String>) IOUtils.readObjectFromFile(file);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    HashMap<String, String> verbNetTemplates = new HashMap<String, String>();
    //(labelledData);
    HashMap<String, HashSet<String>> similarVerbSets = new HashMap<String, HashSet<String>>();
    // getSimilarVerbMapping();
    HashMap<String, HashSet<String>> similarNounSets =
    //getSimilarNounMapping();
    new HashMap<String, HashSet<String>>();
    HashMap<String, HashSet<String>> N2VN2Matches = new HashMap<String, HashSet<String>>();
    //getN2VN1Matches();

    // String pair = N1 + "," + N2;
    //String ipair = N2 + "," + N1;
    boolean decisionMade = false;

    String prep = quad.prep;
    String V = quad.stemmedVerb;
    String N1 = quad.N1;
    String N2 = quad.N2;
    // String word = lemmatizer.stem(V) + "\t" + prep;
    if (prep.equals("of")) {
      //if (attachSite.equals("n")) correct++;
      //total++;
      decisionMade = true;
      String attachDecision = "n";
      quad.setLabel(attachDecision);
      quad.setConfidenceOfLabel(1.0);
      //quad.setScoreOfLabel(100);
    }

    if (!decisionMade) {
      HashSet<String> similarVerbs = new HashSet<String>();
      similarVerbs.add(V);

      HashSet<String> features = getBackOffFeaturesOrderMattersSimilarVerbs(V, N1, prep, N2, similarVerbs, similarNounSets, quad);
      // HashSet<String> features = getBackOffFeaturesSet(V, N1, prep, N2);

      // verb role features
      String word = V + "\t" + prep;
      if (verbNetTemplates.containsKey(word)) {
        String[] roleFeatureItems = verbNetTemplates.get(word).split("\t");
        String roleFeature = prep + "_" + roleFeatureItems[roleFeatureItems.length - 1];
        //            System.out.println(rolefeatures++ + "Role Feature: " + word + "\t" + roleFeature);
        features.add(roleFeature);
      }

      if (N2VN2Matches.containsKey(quad.toString())) {
        HashSet<String> verbPrepositions = N2VN2Matches.get(quad.toString());
        for (String prepVerb : verbPrepositions) {
          //System.out.println(prepVerb + "\t ... " + prep);
          if (prepVerb.startsWith(prep)) {
            features.add(prepVerb);
            //System.out.println("\t\t" + rolefeatures++ + " N2VN2 feature" + prepVerb + "\t" + quad.toString());
          }
        }
      }

      Datum<String, String> instance = makeInstance(quad, features, false);
      Counter<String> scores = classifier.scoresOf(instance);

      NumberFormat nf = NumberFormat.getNumberInstance();
      nf.setMinimumFractionDigits(2);
      nf.setMaximumFractionDigits(2);
      if (nf instanceof DecimalFormat) {
        ((DecimalFormat) nf).setPositivePrefix(" ");
      }
      Distribution<String> distr = Distribution.distributionFromLogisticCounter(scores);
      //      StringBuilder footer = new StringBuilder("");
      //      for (String label : classifier.labels()) {
      //        String str = nf.format(distr.getCount(label));
      //        double score = classifier.scoreOf(instance, label);
      //        footer.append(label).append(' ').append(str).append("\t");
      //        footer.append("score: ").append(label).append(' ').append(score).append("\n\t");
      //      }
      // System.out.println(classifier.classOf(instance) + "\n\t" + "\n\t" + footer);
      String output = classifier.classOf(instance);
      double conf = distr.getCount(output);
      double score = classifier.scoreOf(instance, output);
      quad.setConfidenceOfLabel(conf);
      quad.setScoreOfLabel(score);
      quad.setLabel(output);
    }

    return quad;
  }

  /** Get the labelled Quads local gold standard 
   * @throws IOException **/
  public List<PPAQuad> getQuadsNYCLabelled(String file) throws IOException {
    List<String> data = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      data.add(line);
    }
    //FileUtility.readFileIntoList(file);
    List<PPAQuad> result = new ArrayList<PPAQuad>();
    for (String instance : data) {

      instance = instance.toLowerCase();
      String[] parts = instance.split("\t");
      int i = 1;

      String attachSite = parts[i++];
      String N0 = parts[i++];
      String V = parts[i++];
      String N1 = parts[i++];
      String prep = parts[i++];
      String N2 = parts[i++];

      String[] partsN1 = N1.split(" ");
      //System.out.println(N1 + "<- ->" + partsN1.length);
      if (partsN1.length > 1) {
        N1 = partsN1[partsN1.length - 1];
      }

      String[] partsN2 = N2.split(" ");
      if (partsN2.length > 1) {
        N2 = partsN2[partsN2.length - 1];
      }

      PPAQuad qd = new PPAQuad(lemmatizer.stem(V), V, N1, prep, N2, attachSite, attachSite);
      qd.setN0(N0);
      // qd.setNOrgins(fullN1, fullN2);
      result.add(qd);
    }
    return result;
  }

  protected Datum<String, String> makeInstance(PPAQuad quad, HashSet<String> featuresList, boolean keepLabels) {
    List<String> features = new ArrayList<String>();
    features.addAll(featuresList);

    if (keepLabels) return new BasicDatum<String, String>(features, quad.label);
    else return new BasicDatum<String, String>(features, "?");
  }

  /**
   * Returns back-off features of Collins and Brooks
   * and add knowledge features
   * 
   * @param V
   * @param N1
   * @param prep
   * @param N2
   * @return
   */
  HashSet<String> getBackOffFeaturesOrderMattersSimilarVerbs(String V, String N1, String prep, String N2, HashSet<String> similarVerbs,
      HashMap<String, HashSet<String>> similarNounSets, PPAQuad quad) {

    HashSet<String> features = new HashSet<String>();
    for (String verb : similarVerbs) {
      String if1 = verb + "," + N1 + "," + prep + "," + N2;
      features.add(if1);
    }

    HashSet<String> N1WordNetTypes = WordnetThesaurus.getParentsRecursivelyNounsOnly(N1);
    for (String n1 : N1WordNetTypes) {
      String NN1 = "N1_type" + n1;
      features.add(NN1);

      //          for (String verb : similarVerbs) {
      //            String if1 = verb + "," + n1 + "," + prep + "," + N2;
      //            // System.out.println(if1 + "\t\t N1:" + N1);
      //            features.add(if1);
      //          }
    }

    HashSet<String> N2WordNetTypes = WordnetThesaurus.getParentsRecursivelyNounsOnly(N2);
    for (String n2 : N2WordNetTypes) {
      String NN2 = "N2_type" + n2;
      features.add(NN2);
      //          for (String verb : similarVerbs) {
      //            String if1 = verb + "," + N1 + "," + prep + "," + n2;
      //            // System.out.println(if1 + "\t\tN2: " + N2);
      //            features.add(if1);
      //          }
    }

    if (quad.N0 != null) {
      // String N00 = "N0_" + quad.N0;
      // features.add(N00);

      HashSet<String> N0WordNetTypes = WordnetThesaurus.getParentsRecursivelyNounsOnly(quad.N0);
      for (String n0 : N0WordNetTypes) {
        String N0 = "N0_type" + n0;
        features.add(N0);
      }
      //      if (np_type_annotations.get(quad.N0) != null) {
      //        HashSet<String> N0NELLTypes = np_type_annotations.get(quad.N0);
      //        for (String n0 : N0NELLTypes) {
      //          String N0 = "N0_type_NELL" + n0;
      //          features.add(N0);
      //        }
      //      }
    }

    String[] arg2Parts = N2.split(" ");
    boolean startsWithDigit = false;
    boolean containsCalendarItem = false;
    for (String item : arg2Parts) {
      if (FinalData.calendar.contains(item)) {
        containsCalendarItem = true;
        break;
      }
    }

    if (Character.isDigit(N2.charAt(0))) {
      startsWithDigit = true;
    }

    if (startsWithDigit) {
      String isDigit = "N2_digit";
      features.add(isDigit);
    }

    if (containsCalendarItem) {
      String n2Calendar = "N2_calendar";
      features.add(n2Calendar);
    }

    String if2a = V + "," + N1 + "," + prep + ",_";
    String if2b = V + ",_," + prep + "," + N2;
    String if2c = "_," + N1 + "," + prep + "," + N2;

    String if3a = V + ",_," + prep + ",_";
    String if3b = "_," + N1 + "," + prep + ",_";
    String if3c = "_,_," + prep + "," + N2;

    String if4 = prep;

    features.add(if2a);
    features.add(if2b);
    features.add(if2c);

    features.add(if3a);
    features.add(if3b);
    features.add(if3c);
    features.add(if4);

    if (similarNounSets.get(N1) != null) {
      for (String noun : similarNounSets.get(N1)) {
        if2a = V + "," + noun + "," + prep + ",_";
        if2c = "_," + noun + "," + prep + "," + N2;

        if3b = "_," + noun + "," + prep + ",_";
        features.add(if2a);
        features.add(if2c);
        features.add(if3b);
      }
    }

    if (similarNounSets.get(N2) != null) {
      for (String noun : similarNounSets.get(N2)) {
        if2b = V + ",_," + prep + "," + noun;
        if2c = "_," + N1 + "," + prep + "," + noun;

        if3c = "_,_," + prep + "," + noun;
        features.add(if2b);
        features.add(if2c);
        features.add(if3c);
      }
    }
    return features;
  }

  @SuppressWarnings("unchecked")
  public void testDocument() throws URISyntaxException, IOException {

    // PipelineNLPStanford stanfordPipe = new PipelineNLPStanford();

    // this is where we add our micro-reader
    // PipelineNLPMicro microPipe = new PipelineNLPMicro();

    // we now tell the stanford to add new microreader pipeline
    //PipelineNLP stanfordMicroPipe = stanfordPipe.weld(microPipe);

    // pass a document through the pipeline
    String billDoc = "I baked a cake in the oven.  Sam helped.";
    String doc = "John  ate salad with a fork. Bob cooked rice in March 1945. Google bought  20% of  Youtube.";
    // DocumentNLP document = new DocumentNLPInMemory(new MicroDataTools(), "Ndapa's document", doc, Language.English, stanfordMicroPipe);

    //    AnnotationType<?>[] REQUIRED_ANNOTATIONS = new AnnotationType<?>[] {
    //        AnnotationTypeNLP.TOKEN,
    //        AnnotationTypeNLP.SENTENCE,
    //        AnnotationTypeNLP.POS };
    // List<Annotation> annotations = document.toMicroAnnotation().getAllAnnotations();

    //    for (int i = 0; i < document.getSentenceCount(); i++) {
    //      System.out.println(i + "]\t" + document.getSentence(i));
    //      List<PoSTag> tags = document.getSentencePoSTags(i);
    //      List<String> words = document.getSentenceTokenStrs(i);
    //
    //      WordSequence wordSequence = new WordSequence();
    //
    //      for (int j = 0; j < words.size(); j++) {
    //        System.out.println("\t" + j + "." + words.get(j) + "/" + tags.get(j).name() + "/");
    //        wordSequence.appendTag(tags.get(j).name());
    //        wordSequence.appendWord(words.get(j));
    //      }
    //      List<String> tuples = extractor.findPPAsNoGroups(wordSequence);
    //
    //      for (String instance : tuples) {
    //        quad = getPPQuad(instance);
    //        System.out.println(quad.toString() + "\t" + getPPAPrediction(quad).label);
    //      }
    //      
    //      // System.out.println(i+")\t"+ document.List<PoSTag> getSentencePoSTags
    //    }
    //    
    //    List<PPAQuad> quads =  getQuadsNYCLabelled("src/main/resources/gold_NYC.tsv");
    //    for(PPAQuad qd: quads){
    //      PPAQuad qdPredicted = getPPAPrediction(qd);
    //      System.out.println(qd.toString() + "|\t" + qdPredicted.label+" @ "+qdPredicted.confidenceOfLabel) ;
    //    }
    //    for (Annotation annotation : annotations)
    //      System.out.println(annotation.toJsonString());
  }
}