/*******************************************************************************
 *  Copyright (c) 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.user.types;

import com.ibm.layout.FloatPointer ;
import com.ibm.layout.Layout;
import com.ibm.layout.LayoutDesc;

/* Generated by LD2J */
@LayoutDesc({"value:jfloat:4"})
public interface Float extends Layout {

	interface EffectiveAddress {

		public FloatPointer value();

	}

	public Float.EffectiveAddress EA();

	public long sizeof();

	public float value();

	public void value(float val);

	@Override
	public String toString();

}
