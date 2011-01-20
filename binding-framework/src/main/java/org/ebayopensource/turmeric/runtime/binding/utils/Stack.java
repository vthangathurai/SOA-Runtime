/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * The <code>Stack</code> class represents a last-in-first-out 
 * (LIFO) stack of objects. It extends class <tt>ArrayList</tt> with three 
 * basic operations that allow an ArrayList to be treated as a stack. 
 * It only provides the usual <tt>push</tt> and <tt>pop</tt> and <tt>peek</tt> 
 * operations.
 * 
 * @param <E>  Stack Element type.
 */
public class Stack<E> extends ArrayList<E> {
	/**
     * Pushes an item onto the top of this stack. 
     *
     * @param  o  the element to be pushed onto this stack.
     */
	public void push(E o) {
		add(o);
	}

	/**
     * Removes the object at the top of this stack and returns that 
     * object as the value of this function. 
     *
     * @return  E  The element at the top of this stack (the last item 
     *             of the <tt>Vector</tt> object).
     */
	public E pop() {
		if (isEmpty()) {
			throw new EmptyStackException();
		}

		return remove(size() - 1);
	}

	 /**
     * Looks at the object at the top of this stack without removing it 
     * from the stack. 
     *
     * @return  E  the element at the top of this stack (the last item 
     *             of the <tt>Vector</tt> object). 
     */
	public E peek() {
		if (isEmpty()) {
			throw new EmptyStackException();
		}

		return get(size() - 1);
	}

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 835486766282112209L;
}
