/*******************************************************************************
 *  Copyright (c) 2015 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.user.examples.myline;

/**
 * User-defined layout. It extends the class 'Line' which is generated by LD2J.
 */
public interface MyLine extends Line {

	public default double length() {
		int xdiff = end().x() - start().x();
		int ydiff = end().y() - start().y();
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	public default void st(int x, int y) {
		start().x(x);
		start().y(y);
	}

	public default void en(int x, int y) {
		end().x(x);
		end().y(y);
	}
}
