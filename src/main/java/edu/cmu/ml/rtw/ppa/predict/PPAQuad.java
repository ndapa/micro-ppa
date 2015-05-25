package edu.cmu.ml.rtw.ppa.predict;

import edu.cmu.ml.rtw.generic.util.StringSerializable;

public class PPAQuad implements StringSerializable {

  public String N0;
  public String verb;

  public String stemmedVerb;

  public String N1;

  public String prep;

  public String N2;

  public String label;

  public String N1Origin;

  public String N2Origin;

  public String truelabel;

  public double confidenceOfLabel = 0;
  public double scoreOfLabel = 0;

  public String semantics = "?";

  public PPAQuad(String stemmedVerb, String verb, String N1, String prep, String N2, String label) {
    this.verb = verb;
    this.N1 = N1;
    this.prep = prep;
    this.N2 = N2;
    this.label = label;
    this.stemmedVerb = stemmedVerb;
    N1Origin = N1;
    N2Origin = N1;
  }

  public PPAQuad(String stemmedVerb, String verb, String N1, String prep, String N2, String label, String truelabel) {
    this.verb = verb;
    this.N1 = N1;
    this.prep = prep;
    this.N2 = N2;
    this.label = label;
    this.stemmedVerb = stemmedVerb;
    this.truelabel = truelabel;
    N1Origin = N1;
    N2Origin = N1;
  }

  public PPAQuad(String stemmedVerb, String verb, String N1, String prep, String N2, String label, String truelabel, double conf, double score) {
    this.verb = verb;
    this.N1 = N1;
    this.prep = prep;
    this.N2 = N2;
    this.label = label;
    this.stemmedVerb = stemmedVerb;
    this.truelabel = truelabel;
    this.confidenceOfLabel = conf;
    this.scoreOfLabel = score;
    N1Origin = N1;
    N2Origin = N1;
  }

  public PPAQuad(String verb, String N1, String prep, String N2, String label) {
    this.verb = verb;
    this.N1 = N1;
    this.prep = prep;
    this.N2 = N2;
    this.label = label;
    N1Origin = N1;
    N2Origin = N1;
  }

  public String verb() {
    return verb;
  }

  public String N1() {
    return N1;
  }

  public String Prep() {
    return prep;
  }

  public String N2() {
    return N2;
  }

  public void setConfidenceOfLabel(double conf) {
    confidenceOfLabel = conf;
  }

  public void setScoreOfLabel(double score) {
    scoreOfLabel = score;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setTrueLabel(String label) {
    this.truelabel = label;
  }

  public void setStemmedVerb(String stemmedVerb) {
    this.stemmedVerb = stemmedVerb;
  }

  public void setN0(String N0) {
    this.N0 = N0;
  }

  @Override
  public String toString() {
    return verb + "\t" + N1 + "\t" + prep + "\t" + N2 + "\t" + label;
  }

  public boolean fromString(String arg0) {
    try {
      String[] parts = arg0.toLowerCase().split("\t");
      int i = 0;
      String label = parts[i++].trim();
      String V = parts[i++].trim();
      String N1 = parts[i++].trim();
      String prep = parts[i++].trim();
      String N2 = parts[i++].trim();

      new PPAQuad(V, N1, prep, N2, label);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public String toStringLabelled() {
    return label + "\t" + verb + "\t" + N1 + "\t" + prep + "\t" + N2;
  }

  public String toStringLabelledSemantics() {
    return label + "\t" + semantics + "\t" + verb + "\t" + N1 + "\t" + prep + "\t" + N2;
  }
}