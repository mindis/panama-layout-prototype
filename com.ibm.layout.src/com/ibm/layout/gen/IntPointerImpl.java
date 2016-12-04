/*******************************************************************************
 *  Copyright (c) 2014, 2016 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.ibm.layout.gen;

import com.ibm.layout.IntPointer;
import com.ibm.layout.Layout;
import com.ibm.layout.LayoutType;
import com.ibm.layout.LayoutTypeImpl;
import com.ibm.layout.Location;
import com.ibm.layout.PointerType;

/**
 * Pointer Layout
 */
public class IntPointerImpl extends LayoutTypeImpl implements IntPointer {	
	public int lValue() {
		return UnsafeImplHelper.loadNativeInt(this.location.getData(), this.location.getOffset());
	}
	
	public long sizeof() {
		return (long) UnsafeImplHelper.getUnsafe().addressSize();
	}

	@SuppressWarnings("unchecked")
	public PointerType castTo(Class<?> clazz) {
		LayoutType pointer = null;
		if (Layout.class.isAssignableFrom(clazz)) {
			pointer = LayoutType.getPointer((Class<Layout>) clazz);
		} else {
			pointer =  LayoutType.getPrimPointer(clazz);
		}
		pointer.bindLocation(this.location);
		return (PointerType) pointer;
	}
	
	@Override
	public String toString() {
		return "IntPointer base: " + this.location.getData() + " offset: " + this.location.getOffset(); 
	}

	@Override
	public boolean containsVLA() {
		return false;
	}

	@Override
	public Location getLocation() {
		return this.location;
	}
	
	@Override
	public IntPointer atOffset(long offset) {
		IntPointer newPointer = LayoutType.getPrimPointer(int.class);
		newPointer.bindLocation(new Location(this.location, offset * 4));
		return newPointer;
	}
	
}
