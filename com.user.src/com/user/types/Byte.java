/*******************************************************************************
 *  Copyright (c) 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.user.types;

import com.ibm.layout.BytePointer ;
import com.ibm.layout.Layout;
import com.ibm.layout.LayoutDesc;

/* Generated by LD2J */
@LayoutDesc({"value:jbyte:1"})
public interface Byte extends Layout {

	interface EffectiveAddress {

		public BytePointer value();

	}

	public Byte.EffectiveAddress EA();

	public long sizeof();

	public byte value();

	public void value(byte val);

	@Override
	public String toString();

}
