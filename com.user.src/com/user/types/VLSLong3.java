/*******************************************************************************
 *  Copyright (c) 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.user.types;

import com.ibm.layout.LongPointer ;
import com.ibm.layout.Pointer;
import com.ibm.layout.Location;
import com.ibm.layout.VLArray;
import com.ibm.layout.Layout;
import com.ibm.layout.LayoutDesc;

/* Generated by LD2J */
@LayoutDesc({"c:jlong:8","d:VLSLong2[c]:0","e:jlong:8"})
public interface VLSLong3 extends Layout {

	interface EffectiveAddress {

		public LongPointer c();

		public Pointer<VLSLong2> d();

		public LongPointer e();

	}

	public VLSLong3.EffectiveAddress EA();

	public long sizeof();

	public long c();

	public VLArray<VLSLong2> d();

	public long e();

	public void bindLocation(Location loc, long repeatCountInitializer);

	public void e(long val);

	@Override
	public String toString();

}
