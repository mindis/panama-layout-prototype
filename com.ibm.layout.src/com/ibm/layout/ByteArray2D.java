/*******************************************************************************
 *  Copyright (c) 2014, 2015 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.ibm.layout;

/**
 * 2D array of primtive byte.
 */
public interface ByteArray2D extends LayoutType {
	
	/**
	 * Get the length of 1st dimension
	 * @return number of elements in 1st dimension.
	 */
	public long dim1();

	/**
	 * Get the total size of the array
	 * @return the data size in bytes
	 */
	public long dim2();

	/**
	 * Get an array element
	 * @param i index in 1st dim
	 * @param j index in 2nd dim
	 * @return value of the array element
	 */
	public abstract byte at(long i, long j);

	/**
	 * Set an array element
	 * @param i index in 1st dim
	 * @param j index in 2nd dim
	 * @param value new value of the array element
	 */
	public abstract void put(long i, long j, byte value);

	/**
	 * Get the total size of the array
	 * @return the data size in bytes
	 */
	public long sizeof();

	@Override
	public String toString();
}
