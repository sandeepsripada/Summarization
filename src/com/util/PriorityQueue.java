package com.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * A priority queue based on a binary heap.  Note that this implementation does
 * not efficiently support containment, removal, or element promotion
 * (decreaseKey) -- these methods are therefore not yet implemented.
 *
 * @author Dan Klein
 */
public class PriorityQueue <E> implements Iterator<E>, Serializable, Cloneable {
	private static final long serialVersionUID = 3846743803859132477L;
	int size;
	int capacity;
	List<E> elements;
	double[] priorities;

	protected void grow(int newCapacity) {
		List<E> newElements = new ArrayList<E>(newCapacity);
		double[] newPriorities = new double[newCapacity];
		if (size > 0) {
			newElements.addAll(elements);
			System.arraycopy(priorities, 0, newPriorities, 0, priorities.length);
		}
		elements = newElements;
		priorities = newPriorities;
		capacity = newCapacity;
	}

	protected int parent(int loc) {
		return (loc - 1) / 2;
	}

	protected int leftChild(int loc) {
		return 2 * loc + 1;
	}

	protected int rightChild(int loc) {
		return 2 * loc + 2;
	}

	protected void heapifyUp(int loc) {
		if (loc == 0) return;
		int parent = parent(loc);
		if (priorities[loc] > priorities[parent]) {
			swap(loc, parent);
			heapifyUp(parent);
		}
	}

	protected void heapifyDown(int loc) {
		int max = loc;
		int leftChild = leftChild(loc);
		if (leftChild < size()) {
			double priority = priorities[loc];
			double leftChildPriority = priorities[leftChild];
			if (leftChildPriority > priority)
				max = leftChild;
			int rightChild = rightChild(loc);
			if (rightChild < size()) {
				double rightChildPriority = priorities[rightChild(loc)];
				if (rightChildPriority > priority && rightChildPriority > leftChildPriority)
					max = rightChild;
			}
		}
		if (max == loc)
			return;
		swap(loc, max);
		heapifyDown(max);
	}

	protected void swap(int loc1, int loc2) {
		double tempPriority = priorities[loc1];
		E tempElement = elements.get(loc1);
		priorities[loc1] = priorities[loc2];
		elements.set(loc1, elements.get(loc2));
		priorities[loc2] = tempPriority;
		elements.set(loc2, tempElement);
	}

	protected void removeFirst() {
		if (size < 1) return;
		swap(0, size - 1);
		size--;
		elements.remove(size);
		heapifyDown(0);
	}

	/**
	 * Returns true if the priority queue is non-empty
	 */
	public boolean hasNext() {
		return ! isEmpty();
	}

	/**
	 * Returns the element in the queue with highest priority, and pops it from
	 * the queue.
	 */
	public E next() {
		E first = peek();
		removeFirst();
		return first;
	}

	/**
	 * Not supported -- next() already removes the head of the queue.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the highest-priority element in the queue, but does not pop it.
	 */
	public E peek() {
		if (size() > 0)
			return elements.get(0);
		throw new NoSuchElementException();
	}

	/**
	 * Gets the priority of the highest-priority element of the queue.
	 */
	public double getPriority() {
		if (size() > 0)
			return priorities[0];
		throw new NoSuchElementException();
	}

	/**
	 * Number of elements in the queue.
	 */
	public int size() {
		return size;
	}

	/**
	 * True if the queue is empty (size == 0).
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Adds a key to the queue with the given priority.  If the key is already in
	 * the queue, it will be added an additional time, NOT promoted/demoted.
	 *
	 * @param key
	 * @param priority
	 */
	public boolean add(E key, double priority) {
		if (size == capacity) {
			grow(2 * capacity + 1);
		}
		elements.add(key);
		priorities[size] = priority;
		heapifyUp(size);
		size++;
		return true;
	}

	/**
	 * Returns a representation of the queue in decreasing priority order.
	 */
	public String toString() {
		return toString(size());
	}

	/**
	 * Returns a representation of the queue in decreasing priority order,
	 * displaying at most maxKeysToPring elements.
	 *
	 * @param maxKeysToPrint
	 */
	public String toString(int maxKeysToPrint) {
		PriorityQueue<E> pq = clone();
		StringBuilder sb = new StringBuilder("[");
		int numKeysPrinted = 0;
		while (numKeysPrinted < maxKeysToPrint && pq.hasNext()) {
			double priority = pq.getPriority();
			E element = pq.next();
			sb.append(element.toString());
			sb.append(" : ");
			sb.append(priority);
			if (numKeysPrinted < size() - 1)
				sb.append(", ");
			numKeysPrinted++;
		}
		if (numKeysPrinted < size())
			sb.append("...");
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Returns a counter whose keys are the elements in this priority queue, and
	 * whose counts are the priorities in this queue.  In the event there are
	 * multiple instances of the same element in the queue, the counter's count
	 * will be the sum of the instances' priorities.
	 *
	 */
	@SuppressWarnings("unchecked")
	public Counter asCounter() {
		PriorityQueue<E> pq = clone();
		Counter<E> counter = new Counter<E>();
		while (pq.hasNext()) {
			double priority = pq.getPriority();
			E element = pq.next();
			counter.incrementCount(element, priority);
		}
		return counter;
	}

	/**
	 * Returns a clone of this priority queue.  Modifications to one will not
	 * affect modifications to the other.
	 */
	public PriorityQueue<E> clone() {
		PriorityQueue<E> clonePQ = new PriorityQueue<E>();
		clonePQ.size = size;
		clonePQ.capacity = capacity;
		clonePQ.elements = new ArrayList<E>(capacity);
		clonePQ.priorities = new double[capacity];
		if (size() > 0) {
			clonePQ.elements.addAll(elements);
			System.arraycopy(priorities, 0, clonePQ.priorities, 0, size());
		}
		return clonePQ;
	}

	public PriorityQueue() {
		this(15);
	}

	protected int getLegalCapacity(int capacity){
		int legalCapacity = 0;
		while (legalCapacity < capacity) {
			legalCapacity = 2 * legalCapacity + 1;
		}
		return legalCapacity;
	}

	public PriorityQueue(int capacity) {
		grow(getLegalCapacity(capacity));
	}

	protected boolean isPowerOfTwo(int num){
		while(num > 1){
			if(num %  2 != 0) return false;
			num /= 2;
		}
		return true;
	}

	public void trim(int newsize){

		if(newsize >= size()) return;

		if(!isPowerOfTwo(newsize+1)) {
			System.err.println("size must be of form (2^n)-1");
			throw new UnsupportedOperationException();
		}

		capacity = newsize;

		List<E> newelems = new ArrayList<E>(newsize);
		double[] newpriorities = new double[newsize];

		for(int i = 0; i < newsize; i++){
			double pri = getPriority();
			E elem = next();
			newelems.add(elem);
			newpriorities[i] = pri;
		}

		elements = newelems;
		priorities = newpriorities;
		capacity = newsize;
		size = newsize;
	}

	public static void main(String[] args) {
		PriorityQueue<String> pq = new PriorityQueue<String>();
		System.out.println(pq);
		pq.add("one",1);
		System.out.println(pq);
		pq.add("three",3);
		System.out.println(pq);
		pq.add("one",1.1);
		System.out.println(pq);
		pq.add("two",2);
		System.out.println(pq);
		System.out.println(pq.toString(2));
		while (pq.hasNext()) {
			System.out.println(pq.next());
		}

		PriorityQueue<Integer> pq2 = new PriorityQueue<Integer>();
		for(int i =1; i <= 32; i++){
			pq2.add(i,i);
		}
		//pq2.trim(100);
		System.out.println("\n"+pq2);
		pq2.trim(31);
		System.out.println("\n"+pq2);
		//pq2.trim(9);
		//System.out.println("\n"+pq2);
		pq2.trim(7);
		System.out.println("\n"+pq2);

		for(int i=-10; i < -5; i++){
			pq2.add(i,i);
		}

		for(int i = 64; i < 69; i++){
			pq2.add(i,i);
		}

		pq2.trim(15);
		System.out.println("\n"+pq2+"\t"+pq2.size());

	}
}
