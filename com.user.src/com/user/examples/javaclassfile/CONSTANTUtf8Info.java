/*******************************************************************************
 *  Copyright (c) 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.user.examples.javaclassfile;

import com.ibm.layout.BytePointer ;
import com.ibm.layout.ShortPointer ;
import com.ibm.layout.Pointer;
import com.ibm.layout.Location;
import com.ibm.layout.VLArray;
import com.ibm.layout.Layout;
import com.ibm.layout.LayoutDesc;

/* Generated by LD2J */
@LayoutDesc({">tag:jbyte:1",">length:jshort:2",">bytes:Byte[length]:0"})
public interface CONSTANTUtf8Info extends Layout {

	interface EffectiveAddress {

		public BytePointer tag();

		public ShortPointer length();

		public Pointer<Byte> bytes();

	}

	public CONSTANTUtf8Info.EffectiveAddress EA();

	public long sizeof();

	public byte tag();

	public short length();

	public VLArray<Byte> bytes();

	public void bindLocation(Location loc, short repeatCountInitializer);

	public void tag(byte val);

	@Override
	public String toString();

}
