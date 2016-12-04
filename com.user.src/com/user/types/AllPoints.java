/*******************************************************************************
 *  Copyright (c) 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.user.types;

import com.ibm.layout.Pointer;
import com.ibm.layout.Layout;
import com.ibm.layout.LayoutDesc;

/* Generated by LD2J */
@LayoutDesc({"a:Point:8","b:Point3D:12","c:Point4D:16","d:Point5D:20"})
public interface AllPoints extends Layout {

	interface EffectiveAddress {

		public Pointer<Point> a();

		public Pointer<Point3D> b();

		public Pointer<Point4D> c();

		public Pointer<Point5D> d();

	}

	public AllPoints.EffectiveAddress EA();

	public long sizeof();

	public Point a();

	public Point3D b();

	public Point4D c();

	public Point5D d();

	@Override
	public String toString();

}
