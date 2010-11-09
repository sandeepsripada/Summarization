package com.util;

import java.util.*;
import java.io.Serializable;

/**
 * The MapFactory is a mechanism for specifying what kind of map is to be used
 * by some object.  For example, if you want a Counter which is backed by an
 * IdentityHashMap instead of the defaul HashMap, you can pass in an
 * IdentityHashMapFactory.
 *
 * @author Dan Klein
 */

public abstract class MapFactory<K,V> implements Serializable {

	private static final long serialVersionUID = 8608066730502125954L;

	public static class HashMapFactory<K,V> extends MapFactory<K,V> {
		private static final long serialVersionUID = -1924493386561984754L;

		public Map<K,V> buildMap() {
			return new HashMap<K,V>();
		}
	}

	public static class IdentityHashMapFactory<K,V> extends MapFactory<K,V> {
		private static final long serialVersionUID = 2461890766542992187L;

		public Map<K,V> buildMap() {
			return new IdentityHashMap<K,V>();
		}
	}

	public static class TreeMapFactory<K,V> extends MapFactory<K,V> {
		private static final long serialVersionUID = 7096400825096818640L;

		public Map<K,V> buildMap() {
			return new TreeMap<K,V>();
		}
	}

	public static class WeakHashMapFactory<K,V> extends MapFactory<K,V> {
		private static final long serialVersionUID = 7952240284547499350L;

		public Map<K,V> buildMap() {
			return new WeakHashMap<K,V>();
		}
	}

	public abstract Map<K,V> buildMap();
}

