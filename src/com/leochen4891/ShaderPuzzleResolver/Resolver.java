package com.leochen4891.ShaderPuzzleResolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Resolver {
	ShaderPuzzle mPuzzle;
	public Resolver(ShaderPuzzle puzzle) {
		mPuzzle = puzzle;
	}
	
	public void SetPuzzle(ShaderPuzzle puzzle) {
		mPuzzle = puzzle;
	}
	
	/*
	    2,1  4   4   1   3   			possible cases
      1[hv][h ][h ][h ][h ]  -> YNNNN, NYNNN, NNYNN, NNNYN, NNNNY
    3,1[ v][  ][  ][  ][  ]  -> YYYNY
    3,1[ v][  ][  ][  ][  ]  -> YYYNY
      3[ v][  ][  ][  ][  ]  -> YYYNN, NYYYN, NNYYY
      3[ v][  ][  ][  ][  ]  -> YYYNN, NYYYN, NNYYY
    */
	// strategy is to find and store every possible cases for each hor line
	// and try every combinations by check ver line
	
	public boolean Resolve() {
		if (null == mPuzzle) {
			System.out.print("puzzle not initialized\n");
			return false;
		}
		// 1. get valid cases for each line
		List<List<String>> cases = GetValidCasesForEachHorizontalLine(mPuzzle);
		// check if every line has at lease one case
		for (int y = 0; y < mPuzzle.mY; y++) {
			if (cases.get(y).size() < 1) {
				System.out.print("cannot find a valid case for horizontal line:" + y);
				return false;
			}
		}
		
		// 2. try each case for each line
		int[] caseIndexes;
		caseIndexes = new int[mPuzzle.mY];
		for (int i = 0; i < caseIndexes.length; i++) 
			caseIndexes[i] = 0;
		int curLine = 0;
		while (curLine < mPuzzle.mY && curLine >= 0) {
			// 2.1 set line
			//check if target case if in range
			if (caseIndexes[curLine] >= cases.get(curLine).size()) {
				// not in range, back to upper level
				mPuzzle.ClearHorizontalLine(curLine);
				caseIndexes[curLine]=0;
				curLine--;
				if (curLine >= 0)
					caseIndexes[curLine]++;
				continue;
			}
			
			char[] line = cases.get(curLine).get(caseIndexes[curLine]).toCharArray();
			mPuzzle.SetHorizontalLine(curLine, line);
			
			// 2.2 check vertical lines
			if (mPuzzle.CheckVerticalLines()) {
				// this case works for curLine, try next line
				curLine++;
			} else {
				// this case doesn't work
				if (caseIndexes[curLine] < cases.get(curLine).size() - 1) {
					//try next case
					caseIndexes[curLine]++;
				} else {
					// out of cases, previous line must have used wrong case, go back
					mPuzzle.ClearHorizontalLine(curLine);
					caseIndexes[curLine]=0;
					curLine--;
					if (curLine >= 0)
						caseIndexes[curLine]++;
				}
			}
		}
		
		if (curLine == mPuzzle.mY) {
			// found solution
			System.out.print("found solution!\n");
			mPuzzle.PrintPuzzle();
			return true;
		} else if (curLine < 0){
			// unable to find a solution
			System.out.print("fail to find a solution\n");
		}
		return false;
	}
	
	
	private List<List<String>> GetValidCasesForEachHorizontalLine(ShaderPuzzle puzzle) {
		List<List<String>> ret = null;
		if (null != puzzle) {
			ret = new ArrayList<List<String>>();
			for (int y = 0; y < puzzle.mY; y++) {
				ret.add(puzzle.GetValidCasesForHorizontalLine(y));
			}
		}
		return ret;
	}
	
}
