package edu.cmu.ml.rtw.ppa.util;

import java.util.HashSet;
import java.util.Set;

public class SetOps {
  
  /**
   * Computes set set diff 
   * s1 - s2
   * 
   * @param <T>
   **/
  public static <T> Set<T> getSetDiff(Set<T> set1, Set<T> set2) {
    Set<T> a;
    Set<T> b;
    Set<T> c = new HashSet<T>();
    if (set1 == null || set2 == null)
      return c;
    if (set1.size() <= set2.size()) {
      a = set1;
      b = set2;
    } else {
      a = set2;
      b = set1;
    }
    for (T e : a) {
      if (b.contains(e)) {
        c.add(e);
      }
    }
    return c;
  }
  

	/**
	 * Computes set intersection 
	 * 
	 * @param <T>
	 **/
	public static <T> Set<T> getSetIntersection(Set<T> set1, Set<T> set2) {
		Set<T> a;
		Set<T> b;
		Set<T> c = new HashSet<T>();
		if (set1 == null || set2 == null)
			return c;
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2;
		} else {
			a = set2;
			b = set1;
		}
		for (T e : a) {
			if (b.contains(e)) {
				c.add(e);
			}
		}
		return c;
	}

	/**
	 * Computes set intersection size
	 * 
	 * @param <T>
	 **/
	public static <T> int getSetIntersectionSize(Set<T> set1, Set<T> set2) {
		Set<T> a;
		Set<T> b;
		if (set1 == null || set2 == null)
			return 0;
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2;
		} else {
			a = set2;
			b = set1;
		}
		int count = 0;
		for (T e : a) {
			if (b.contains(e)) {
				count++;
			}
		}
		return count;
	}

}
