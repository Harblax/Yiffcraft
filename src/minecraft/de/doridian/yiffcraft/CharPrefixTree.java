package de.doridian.yiffcraft;

import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.Collection;
import java.util.Map;

public class CharPrefixTree {
	public static class Node {
		public String value;
		public String desc;
		public TCharObjectHashMap<Node> children = new TCharObjectHashMap<Node>();
	}

	Node root = null;

	public boolean isEmpty() {
		return (this.root == null);
	}

	public Node add(final String s, final String d) {
		Node node = add(s);
		node.desc = d;
		return node;
	}

	public Node add(String s) {
		s = s.toLowerCase();

		Node node = this.root;
		if (node == null)
			node = this.root = new Node();

		for(int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if(node.children.containsKey(c)) {
				node = node.children.get(c);
			} else {
				node.children.put(c, node = new Node());
			}
		}
		node.value = s;
		return node;
	}

	public Node get(String s) {
		if(s == null || s.isEmpty()) {
			return root;
		}

		s = s.toLowerCase();

		Node node = this.root;
		for(int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if(node.children.containsKey(c)) {
				node = node.children.get(c);
			} else {
				return null;
			}
		}

		return node;
	}

	public Node getFirstEnd(final String s) {
		Node node = get(s);
		if(node == null) return null;

		while(node.value == null) {
			final TCharObjectIterator<Node> iterator = node.children.iterator();
			if(!iterator.hasNext()) return null;
			iterator.advance();
			node = iterator.value();
		}

		return node;
	}

	public boolean remove(final Object o) {
		if(!(o instanceof String)) {
			return false;
		}

		final String s = (String)o;
		Node node = this.root;

		for (int i = 0; node != null && i < s.length() - 1; i++)
			node = node.children.get(s.charAt(i));

		return node != null;
	}

	public boolean containsAll(final Collection<?> c) {
		for (final Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	public void addAll(final Collection<? extends String> c) {
		for (final String s : c)
			add(s);
	}

	public void addAll(final Map<? extends String, ? extends String> m, char prefix) {
		for (final Map.Entry<? extends String, ? extends String> e : m.entrySet())
			add(prefix + e.getKey(), e.getValue());
	 }

	public void addAll(final Map<? extends String, ? extends String> m) {
		for (final Map.Entry<? extends String, ? extends String> e : m.entrySet())
			add(e.getKey(), e.getValue());
	 }

	public void addAll(final Collection<? extends String> c, char prefix) {
		for (final String s : c)
			add(prefix + s);
	 }

	public void clear() {
		this.root = null;
	}

	public boolean contains(final Object o) {
		if(!(o instanceof String)) {
			return false;
		}

		final String s = ((String)o).toLowerCase();
		if (s.length() == 0)
			return !isEmpty();

		Node node = this.root;
		for (int i = 0; node != null && i < s.length(); i++)
			node = node.children.get(s.charAt(i));
		return node != null;
	}
}