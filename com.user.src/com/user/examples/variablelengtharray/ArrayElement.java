/*******************************************************************************
 *  Copyright (c) 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html 
 *******************************************************************************/
package com.user.examples.variablelengtharray;

import com.ibm.layout.Layout;
import com.ibm.layout.LayoutDesc;

/* Generated by LD2J */
@LayoutDesc({"item1:jint:4","item2:jint:4"})
public interface ArrayElement extends Layout {

	public long sizeof();

	public abstract int item1();

	public abstract int item2();

	public abstract void item1(int val);

	public abstract void item2(int val);

	@Override
	public String toString();

}
