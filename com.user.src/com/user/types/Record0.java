/*******************************************************************************
 *  Copyright (c) 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.user.types;

import com.ibm.layout.IntPointer ;
import com.ibm.layout.BytePointer ;
import com.ibm.layout.Pointer;
import com.ibm.layout.Array1D;
import com.ibm.layout.Layout;
import com.ibm.layout.LayoutDesc;

/* Generated by LD2J */
@LayoutDesc({"col0:jint:4","col1:Byte[23]:23","col2:jbyte:1","col3:Byte[23]:23"})
public interface Record0 extends Layout {

	interface EffectiveAddress {

		public IntPointer col0();

		public Pointer<Byte> col1();

		public BytePointer col2();

		public Pointer<Byte> col3();

	}

	public Record0.EffectiveAddress EA();

	public long sizeof();

	public int col0();

	public Array1D<Byte> col1();

	public byte col2();

	public Array1D<Byte> col3();

	public void col0(int val);

	public void col2(byte val);

	@Override
	public String toString();

}
