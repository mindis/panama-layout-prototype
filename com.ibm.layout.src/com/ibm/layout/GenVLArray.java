/*******************************************************************************
 *  Copyright (c) 2016, 2017 IBM Corporation.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.ibm.layout;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.ibm.layout.ImplHelper.TypeInfo;

class GenVLArray implements Opcodes {
	final private String elementInterfaceClassName;
	final private String elementImplClassName;
	final private String arrayImplClassName;
	final private String arrayInterfaceClassName;
	final private String arrayInterfaceClassSig; /* Signature of the array interface class, if it is generic */
	private boolean isComplex = false;
	private boolean isPriviledged = false;
	private boolean isElementVariableLength = false;
	
	/**
	 * Instantiate GenArray1D for user defined array class
	 * 
	 * @param elementInterfaceClass, element type for Array
	 * @param userDefinedArrayClass, user defined class must be subclass of LayoutType 
	 */
	public <E extends Layout, AE extends VLArray<E>, EE extends Layout>
	GenVLArray(Class<E> elementInterfaceClass, Class<AE> userDefinedArrayClass, TypeInfo typeInfo) {
		elementInterfaceClassName = ImplHelper.getInterfaceClassName(elementInterfaceClass);
		elementImplClassName = ImplHelper.getImplClassName(elementInterfaceClass);
		if (null == userDefinedArrayClass) {
			arrayImplClassName = ImplHelper.getVLArrayClassImplName(elementInterfaceClass);
			arrayInterfaceClassName = "com/ibm/layout/VLArray";
		} else {
			arrayImplClassName = LayoutHelper.getVLAImplClassName(userDefinedArrayClass.getSimpleName(), typeInfo.repeatCountMember, typeInfo.enclosingType).replace('.', '/');
			arrayInterfaceClassName = userDefinedArrayClass.getName().replace('.', '/');
			if (ComplexVLArray.class.isAssignableFrom(userDefinedArrayClass)) {
				isComplex = true;
			} else if (PriviledgedVLArray.class.isAssignableFrom(userDefinedArrayClass)) {
				isPriviledged = true;
			}
		}
		arrayInterfaceClassSig = "L" + arrayInterfaceClassName + "<L" + elementInterfaceClassName + ";>;";
		isElementVariableLength = ImplHelper.isVariableLengthType(elementInterfaceClass);
	}

	/**
	 * Generate bytecodes for runtime class
	 * 
	 * @return byte array containing bytecodes for runtime class
	 * @throws Exception
	 */
	public byte[] genBytecode() throws Exception {
		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;
		FieldVisitor fv;
		final boolean itf = false;

		/* If user-defined array class is supplied, use it as superclass, and use its name instead of generating it */
		cw.visit(V1_8, ACC_FINAL + ACC_SUPER, arrayImplClassName, arrayInterfaceClassSig, "java/lang/Object", new String[] {arrayInterfaceClassName});
		{
			fv = cw.visitField(ACC_PROTECTED + ACC_FINAL, "length", "J", null, null);
			fv.visitEnd();
		}
		if (!isElementVariableLength) {
			fv = cw.visitField(ACC_PROTECTED + ACC_FINAL, "elementSize", "J", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "arraySize", "J", null, null);
			fv.visitEnd();
		}
		{

			if (isElementVariableLength) {
				if (isPriviledged) {
					mv = cw.visitMethod(ACC_PROTECTED, "<init>", "(Lcom/ibm/layout/PointerType;)V", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKESPECIAL,"java/lang/Object", "<init>", "()V", false);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "arrayLength", "(Lcom/ibm/layout/PointerType;)J", false);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "length", "J");
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(LCONST_0);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "arraySize", "J");
					mv.visitInsn(RETURN);
					mv.visitMaxs(3, 2);
				} else {
					mv = cw.visitMethod(ACC_PROTECTED, "<init>", "(J)V", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKESPECIAL,"java/lang/Object", "<init>", "()V", false);
					mv.visitVarInsn(ALOAD, 0);
					
					if (isComplex) {
						mv.visitVarInsn(ALOAD, 0);
						mv.visitVarInsn(LLOAD, 1);
						mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "arrayLength", "(J)J", false);
					} else {
						mv.visitVarInsn(LLOAD, 1);
					}
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "length", "J");
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(LCONST_0);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "arraySize", "J");
					mv.visitInsn(RETURN);
					if (isComplex) {
						mv.visitMaxs(4, 3);
					} else {
						mv.visitMaxs(3, 3);
					}
				}
			} else {
				if (isPriviledged) {
					mv = cw.visitMethod(ACC_PROTECTED, "<init>", "(Lcom/ibm/layout/PointerType;J)V", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(LLOAD, 2);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "elementSize", "J");
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "arrayLength", "(Lcom/ibm/layout/PointerType;)J", false);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "length", "J");
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(LLOAD, 2);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, arrayImplClassName, "length", "J");
					mv.visitInsn(LMUL);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "arraySize", "J");
					mv.visitInsn(RETURN);
					mv.visitMaxs(5, 4);
					mv.visitEnd();
				} else {
					mv = cw.visitMethod(ACC_PROTECTED, "<init>", "(JJ)V", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(LLOAD, 3);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "elementSize", "J");
					mv.visitVarInsn(ALOAD, 0);
					if (isComplex) {
						mv.visitVarInsn(ALOAD, 0);
						mv.visitVarInsn(LLOAD, 1);
						mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "arrayLength", "(J)J", false);
					} else {
						mv.visitVarInsn(LLOAD, 1);
					}
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "length", "J");
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(LLOAD, 3);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, arrayImplClassName, "length", "J");
					mv.visitInsn(LMUL);
					mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "arraySize", "J");
					mv.visitInsn(RETURN);
					mv.visitMaxs(5, 5);
					mv.visitEnd();
				}
			}
			
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "sizeof", "()J", null, null);
			mv.visitCode();
			if (isElementVariableLength) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "arraySize", "J");
				mv.visitInsn(LCONST_0);
				mv.visitInsn(LCMP);
				Label l0 = new Label();
				mv.visitJumpInsn(IFNE, l0);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 1);
				Label l1 = new Label();
				mv.visitJumpInsn(GOTO, l1);
				Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitInsn(DUP);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "arraySize", "J");
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ILOAD, 1);
				mv.visitInsn(I2L);
				mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "at", "(J)L" + elementInterfaceClassName + ";", false);
				mv.visitMethodInsn(INVOKEINTERFACE, elementInterfaceClassName, "sizeof", "()J", true);
				mv.visitInsn(LADD);
				mv.visitFieldInsn(PUTFIELD, arrayImplClassName, "arraySize", "J");
				mv.visitIincInsn(1, 1);
				mv.visitLabel(l1);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 1);
				mv.visitInsn(I2L);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "length", "J");
				mv.visitInsn(LCMP);
				mv.visitJumpInsn(IFLT, l2);
				mv.visitLabel(l0);
				mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "arraySize", "J");
				mv.visitInsn(LRETURN);
				mv.visitMaxs(6, 2);
			} else {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "arraySize", "J");
				mv.visitInsn(LRETURN);
				mv.visitMaxs(2, 1);
			}
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "getComponentType", "()Ljava/lang/Class;", "()Ljava/lang/Class<*>;", null);
			mv.visitCode();
			mv.visitLdcInsn(Type.getType("L" + elementInterfaceClassName + ";"));
			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "getVLALength", "()J", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, arrayImplClassName, "length", "J");
			mv.visitInsn(LRETURN);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "at", "(J)L"+ elementInterfaceClassName + ";", null, null);
			mv.visitCode();
			if (isElementVariableLength) {
				mv.visitTypeInsn(NEW, elementImplClassName);
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, elementImplClassName, "<init>", "()V", false);
				mv.visitVarInsn(ASTORE, 3);
				mv.visitInsn(LCONST_0);
				mv.visitVarInsn(LSTORE, 4);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 6);
				Label l0 = new Label();
				mv.visitJumpInsn(GOTO, l0);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitFrame(Opcodes.F_APPEND,3, new Object[] {elementImplClassName, Opcodes.LONG, Opcodes.INTEGER}, 0, null);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitTypeInsn(NEW, "com/ibm/layout/Location");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "location", "Lcom/ibm/layout/Location;");
				mv.visitVarInsn(LLOAD, 4);
				mv.visitMethodInsn(INVOKESPECIAL, "com/ibm/layout/Location", "<init>", "(Lcom/ibm/layout/Location;J)V", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "bindLocation", "(Lcom/ibm/layout/Location;)V", false);
				mv.visitVarInsn(LLOAD, 4);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "sizeof", "()J", false);
				mv.visitInsn(LADD);
				mv.visitVarInsn(LSTORE, 4);
				mv.visitIincInsn(6, 1);
				mv.visitLabel(l0);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 6);
				mv.visitInsn(I2L);
				mv.visitVarInsn(LLOAD, 1);
				mv.visitInsn(LCMP);
				mv.visitJumpInsn(IFLT, l1);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitTypeInsn(NEW, "com/ibm/layout/Location");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "location", "Lcom/ibm/layout/Location;");
				mv.visitVarInsn(LLOAD, 4);
				mv.visitMethodInsn(INVOKESPECIAL, "com/ibm/layout/Location", "<init>", "(Lcom/ibm/layout/Location;J)V", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "bindLocation", "(Lcom/ibm/layout/Location;)V", false);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitInsn(ARETURN);
				mv.visitMaxs(6, 7);
			} else {
				mv.visitTypeInsn(NEW, elementImplClassName);
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, elementImplClassName, "<init>", "()V", false);
				mv.visitVarInsn(ASTORE, 3);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitTypeInsn(NEW, "com/ibm/layout/Location");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "location", "Lcom/ibm/layout/Location;");
				mv.visitVarInsn(LLOAD, 1);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "elementSize", "J");
				mv.visitInsn(LMUL);
				mv.visitMethodInsn(INVOKESPECIAL, "com/ibm/layout/Location", "<init>", "(Lcom/ibm/layout/Location;J)V", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "bindLocationNoCheck", "(Lcom/ibm/layout/Location;)V", false);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitInsn(ARETURN);
				mv.visitMaxs(8, 4);
			}
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "range", "(JJ)Lcom/ibm/layout/Array1D;", "(JJ)Lcom/ibm/layout/Array1D<"
					+ elementInterfaceClassName + ";>;", null);
			mv.visitCode();
			mv.visitTypeInsn(NEW, arrayImplClassName);
			mv.visitInsn(DUP);
			mv.visitVarInsn(LLOAD, 3);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, this.arrayImplClassName, "elementSize", "J");
			mv.visitMethodInsn(INVOKESPECIAL, this.arrayImplClassName, "<init>", "(JJ)V", itf);
			mv.visitVarInsn(ASTORE, 5);
			mv.visitTypeInsn(NEW, "com/ibm/layout/Location");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, arrayImplClassName, "location", "Lcom/ibm/layout/Location;");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, arrayImplClassName, "elementSize", "J");
			mv.visitInsn(LMUL);
			mv.visitMethodInsn(INVOKESPECIAL, "com/ibm/layout/Location", "<init>", "(Lcom/ibm/layout/Location;J)V", itf);
			mv.visitVarInsn(ASTORE, 6);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitVarInsn(ALOAD, 6);
			mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "bindLocation", "(Lcom/ibm/layout/Location;)V", itf);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(7, 7);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "put", "(JLcom/ibm/layout/Layout;)V", null, null);
			mv.visitCode();
			if (isElementVariableLength) {
				mv.visitTypeInsn(NEW, elementImplClassName);
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, elementImplClassName, "<init>", "()V", false);
				mv.visitVarInsn(ASTORE, 4);
				mv.visitInsn(LCONST_0);
				mv.visitVarInsn(LSTORE, 5);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 7);
				Label l0 = new Label();
				mv.visitJumpInsn(GOTO, l0);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitFrame(Opcodes.F_APPEND,3, new Object[] {elementImplClassName, Opcodes.LONG, Opcodes.INTEGER}, 0, null);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitTypeInsn(NEW, "com/ibm/layout/Location");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "location", "Lcom/ibm/layout/Location;");
				mv.visitVarInsn(LLOAD, 5);
				mv.visitMethodInsn(INVOKESPECIAL, "com/ibm/layout/Location", "<init>", "(Lcom/ibm/layout/Location;J)V", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "bindLocation", "(Lcom/ibm/layout/Location;)V", false);
				mv.visitVarInsn(LLOAD, 5);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "sizeof", "()J", false);
				mv.visitInsn(LADD);
				mv.visitVarInsn(LSTORE, 5);
				mv.visitIincInsn(7, 1);
				mv.visitLabel(l0);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 7);
				mv.visitInsn(I2L);
				mv.visitVarInsn(LLOAD, 1);
				mv.visitInsn(LCMP);
				mv.visitJumpInsn(IFLT, l1);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitTypeInsn(NEW, "com/ibm/layout/Location");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "location", "Lcom/ibm/layout/Location;");
				mv.visitVarInsn(LLOAD, 5);
				mv.visitMethodInsn(INVOKESPECIAL, "com/ibm/layout/Location", "<init>", "(Lcom/ibm/layout/Location;J)V", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "bindLocation", "(Lcom/ibm/layout/Location;)V", false);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "copyFrom", "(Lcom/ibm/layout/Layout;)V", false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(6, 8);
			} else {
				mv.visitTypeInsn(NEW, elementImplClassName);
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, elementImplClassName, "<init>", "()V", false);
				mv.visitVarInsn(ASTORE, 4);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitTypeInsn(NEW, "com/ibm/layout/Location");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "location", "Lcom/ibm/layout/Location;");
				mv.visitVarInsn(LLOAD, 1);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, arrayImplClassName, "elementSize", "J");
				mv.visitInsn(LMUL);
				mv.visitMethodInsn(INVOKESPECIAL, "com/ibm/layout/Location", "<init>", "(Lcom/ibm/layout/Location;J)V", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "bindLocation", "(Lcom/ibm/layout/Location;)V", false);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "copyFrom", "(Lcom/ibm/layout/Layout;)V", false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(8, 5);
			}
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "put", "(JL" + elementInterfaceClassName + ";)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(LLOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "at", "(J)L" + elementInterfaceClassName + ";", itf);
			mv.visitTypeInsn(CHECKCAST, elementImplClassName);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitTypeInsn(CHECKCAST, elementImplClassName);
			mv.visitMethodInsn(INVOKEVIRTUAL, elementImplClassName, "copyFrom", "(L" + elementImplClassName + ";)V", itf);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "at", "(J)Lcom/ibm/layout/Layout;", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(LLOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "at", "(J)L" + elementInterfaceClassName + ";", itf);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
			mv.visitCode();
			mv.visitTypeInsn(NEW, "java/lang/StringBuffer");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("[");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitVarInsn(ASTORE, 1);
			mv.visitInsn(LCONST_0);
			mv.visitVarInsn(LSTORE, 2);
			Label l0 = new Label();
			mv.visitJumpInsn(GOTO, l0);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {"java/lang/StringBuffer", Opcodes.LONG}, 0, null);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
			mv.visitInsn(DUP);
			mv.visitLdcInsn(" ");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(LLOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, arrayImplClassName, "at", "(J)L" + elementInterfaceClassName + ";", false);
			mv.visitMethodInsn(INVOKEINTERFACE, elementInterfaceClassName, "toString", "()Ljava/lang/String;", true);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;", false);
			mv.visitInsn(POP);
			mv.visitVarInsn(LLOAD, 2);
			mv.visitInsn(LCONST_1);
			mv.visitInsn(LADD);
			mv.visitVarInsn(LSTORE, 2);
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(LLOAD, 2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, arrayImplClassName, "length", "J");
			mv.visitInsn(LCMP);
			mv.visitJumpInsn(IFLT, l1);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(" ]");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;", false);
			mv.visitInsn(POP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuffer", "toString", "()Ljava/lang/String;", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}
		
		ImplHelper.genLayoutTypeImpl(cw, mv, fv, arrayImplClassName, (isElementVariableLength) ? 2 : 0, true);
		
		cw.visitEnd();

		return cw.toByteArray();
	}
}