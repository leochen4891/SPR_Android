package com.leochen4891.ShaderPuzzleResolver;

public class Test {
	static final String xDesc4 = "2-2.1-1.1-1.1";
	static final String yDesc4 = "3-1-1-4";
	/*
	N	Y	Y	Y
	N	Y	N	N
	Y	N	N	N
	Y	Y	Y	Y
	*/
	
	static final String xDesc5 = "2.1-4-4-1-3";
	static final String yDesc5 = "1-3.1-3.1-3-3";
	/*
	N	N	N	N	Y
	Y	Y	Y	N	Y
	Y	Y	Y	N	Y
	N	Y	Y	Y	N
	Y	Y	Y	N	N
	*/


	static final String xDesc9 = "1.1.1.1-3.1.1-1.3-1-1.1.1-2.1-1.1.3-1.1.2-4.1";
	static final String yDesc9 = "1.1.2-1-2.3-1.1.1.1-2.2.1-1.1-3.2-1.1.3-2.1";
	/*
	Y	N	Y	N	N	N	Y	Y	N
	N	N	N	N	Y	N	N	N	N
	Y	Y	N	N	N	N	Y	Y	Y
	N	Y	N	Y	N	Y	N	N	Y
	Y	Y	N	N	Y	Y	N	N	Y
	N	N	N	N	N	N	Y	N	Y
	Y	Y	Y	N	N	Y	Y	N	N
	N	N	Y	N	Y	N	Y	Y	Y
	N	Y	Y	N	N	N	N	Y	N
	*/
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ShaderPuzzle puzzle4 = new ShaderPuzzle(yDesc4, xDesc4);
		Resolver resolver4 = new Resolver(puzzle4);
		resolver4.Resolve();
		
		ShaderPuzzle puzzle5 = new ShaderPuzzle(yDesc5, xDesc5);
		Resolver resolver5 = new Resolver(puzzle5);
		resolver5.Resolve();
		
		ShaderPuzzle puzzle9 = new ShaderPuzzle(yDesc9, xDesc9);
		Resolver resolver9 = new Resolver(puzzle9);
		resolver9.Resolve();
		
	}

}
