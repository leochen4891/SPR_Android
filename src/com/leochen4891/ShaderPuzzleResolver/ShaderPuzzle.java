package com.leochen4891.ShaderPuzzleResolver;

import java.util.ArrayList;
import java.util.List;

public class ShaderPuzzle {
	/* A 5*5 puzzle
	    2,1  4   4   1   3  <--vertical rule
      1[hv][h ][h ][h ][h ]
    3,1[ v][  ][  ][  ][  ]
    3,1[ v][  ][  ][  ][  ]
      3[ v][  ][  ][  ][  ]
      3[ v][  ][  ][  ][  ]
      ^
   hor rule
  */
	char[][] mPuzzle;
	String[] mVerRules;
	String[] mHorRules;
	int mX;
	int mY;
	
	// each [  ] has several status
	static public final char STATUS_EMPTY= 'E';
	static public final char STATUS_TRY_YES = 'y';
	static public final char STATUS_TRY_NO = 'n';
	static public final char STATUS_YES = 'Y';
	static public final char STATUS_NO= 'N';
	
	// rules are inputed as "2.1-4-4-1-3" and "1-3.1-3.1-3-3"
	static public final char SEP_NUMBER = '.'; 
	static public final char SEP_LINE = '-';
	
	// and translated into more specific rules array
	// "2.1-4-4-1-3" becomes "*2+1*", "*4*, "*4*", "*1*", "*3*"
	static public final char SEP_NOS = '*'; //0 or more NOs
	static public final char SEP_NOS_PLUS = '+'; //1 or more NOs
	
	static public final int MAX_HOR_LINES = 9;
	static public final int MAX_VER_LINES = 9;
	
	//description string should be like "3.1-1.2-2" for "3,1   1,2   2"
	public ShaderPuzzle(String horRule, String verRule) {
		Initialize(horRule, verRule);
	}
	
	public void Initialize(String horRule, String verRule) {
		if (null == horRule || null == verRule)
			return;
		
		mHorRules = horRule.split("\\"+SEP_LINE, MAX_HOR_LINES);
		mVerRules = verRule.split("\\"+SEP_LINE, MAX_VER_LINES);
		
		mY = mHorRules.length;
		mX = mVerRules.length;
		
		if (null == mPuzzle)
			mPuzzle = new char[MAX_HOR_LINES][MAX_VER_LINES];
		for (int y = 0; y < MAX_HOR_LINES; y++) {
			for (int x = 0; x < MAX_VER_LINES; x++) {
				mPuzzle[y][x] = STATUS_EMPTY;
			}
		}
	}
	
	public char Get(int y, int x) {
		return mPuzzle[y][x];
	}
	
	public char[] GetHorizontalLine(int index) {
		char[] ret = null;
		if (index >= 0 && index < mY) {
			ret = new char[mX];
			for (int i = 0; i < mX; i++) {
				ret[i] = mPuzzle[index][i];
			}
		} else {
			System.out.print("Error GetHorizontalLine, index:" + index);
		}
		return ret;
	}
	
	public char[] GetVerticalLine(int index) {
		char[] ret = null;
		if (index >= 0 && index < mX) {
			ret = new char[mY];
			for (int i = 0; i < mY; i++) {
				ret[i] = mPuzzle[i][index];
			}
		} else {
			System.out.print("Error GetVerticalLine, index:" + index);
		}
		return ret;
	}
	
	public void Set(int y, int x, char value) {
		if (x >= 0 && x < mX && y >= 0 && y < mY) { 
			mPuzzle[y][x] = value;
		} else {
			System.out.print("Error when set (" + y + ", " + x + ") to " + value);
		}
	}
	
	public void SetHorizontalLine(int index, char[] line) {
		if (index >= 0 && index < mY && line.length == mX) {
			for (int i = 0; i < mX; i++) {
				mPuzzle[index][i] = line[i];
			}
		} else {
			System.out.print("error when SetHorizontalLine: " + index + " to " + line.toString());
		}
	}
	
	public void SetVerticalLine(int index, char[] line) {
		if (index >= 0 && index < mX && line.length == mY) {
			for (int i = 0; i < mY; i++) {
				mPuzzle[i][index] = line[i];
			}
		} else {
			System.out.print("error when SetVerticalLine: " + index + " to " + line.toString());
		}
	}
	
	// expand implicit rule "1.3.2" to explicit rule "*1+3+2*"
	// * for [EN]*
	// + for [EN]+
	// n for [EY]{n}
	private String ExpandOriginalRule(String oriRule) {
		return "*" + oriRule.replace('.', '+') + "*";
	}
	
	// Check if rule matches line
	// rule looks like: "*3+1*", not "3.1"
	// line looks like: "YYENY"
	// "*1+3+2*" is regex "[EN]*[EY]{1}[EN]+[EY]{3}[EN]+[EY]{2}[EN]*"
	// matches "YNYYYNYY" and "YNYYEEEE", but not "YNNYEEEE"
	// NOTE: rule MUST be expanded rule, NOT original rule
	public boolean FindRuleInLine(String rule, String line) {
		// construct regex string
		char[] ruleBytes = rule.toCharArray();
		String regex = "";
		for (int i = 0; i < ruleBytes.length; i++) {
			char cur = ruleBytes[i];
			if (SEP_NOS == cur) { // [EN]*
				regex += "[" + STATUS_EMPTY + STATUS_NO + "]" + SEP_NOS;
			} else if (SEP_NOS_PLUS == cur) { // [EN]+
				regex += "[" + STATUS_EMPTY + STATUS_NO + "]" + SEP_NOS_PLUS;
			} else if (cur > '0' && cur < '9') {
				regex += "[" + STATUS_EMPTY + STATUS_YES + "]" +"{" + cur + "}";
			} else {
				System.out.print("unexpected char:" + cur + " when find rule:" + rule + " in line:" + line);
				return false;
			}
		}
		
		boolean matches = line.matches(regex);
		return matches;
	}
	
	public boolean CheckHorizontalLine(int index) {
		if (index < 0 || index > mY) {
			return false;
		}
		
		String line = "";
		for (int i = 0; i < mX; i++) {
			line += mPuzzle[index][i];
		}
		boolean ret = FindRuleInLine(ExpandOriginalRule(mHorRules[index]), line);
		return ret;
	}
	
	public boolean CheckVerticalLines() {
		for (int i = 0; i < mX; i++) {
			if (!CheckVerticalLine(i))
				return false;
		}
		return true;
	}
	
	public boolean CheckVerticalLine(int index) {
		if (index < 0 || index > mX) {
			return false;
		}
		
		String line = "";
		for (int i = 0; i < mY; i++) {
			line += mPuzzle[i][index];
		}
		boolean ret = FindRuleInLine(ExpandOriginalRule(mVerRules[index]), line);
		return ret;
	}
		
	// check whether the whole puzzle solved 
	public boolean Check () {
		boolean result = true;
		for (int i = 0; i < mY; i++) {
			result = CheckHorizontalLine(i);
			if (false == result) return false;
		}
		for (int i = 0; i < mX; i++) {
			result = CheckVerticalLine(i);
			if (false == result) return false;
		}
		return result;
	}
	
	public List<String> GetValidCasesForHorizontalLine(int index) {
		List<String> ret = null;
		if (index >= 0 && index < mY) {
			ret = new ArrayList<String>();
			// start to find cases, increaseing a number from 0 to 2^mX, each bit represents a [  ]
			// int in java is 32bits, so up to a line of length 32, current max use is 9, so int is adequate
			String rule = ExpandOriginalRule(mHorRules[index]);
			String line = "";
			int length = mX;
			for (int increaser = 0; increaser < Math.pow(2, length); increaser++) {
				line = "";
				for (int i = 0; i < length; i++) {
					int bit =(increaser&(1<<i));
					line += (bit==0)?STATUS_NO:STATUS_YES;
				}
				if (FindRuleInLine(rule, line)) { // found a valid case
					ret.add(line);
				}
			}
		} else {
			System.out.print("Error when GetValidCasesForHorizontalLine, index = " + index);
		}
		return ret;
	}
	
	public void ClearHorizontalLine(int index) {
		char[] line = new char[mX];
		for (int i = 0; i < line.length; i++){
			line[i] = STATUS_EMPTY;
		}
		SetHorizontalLine(index, line);
	}
	
	public void PrintPuzzle() {
		System.out.print("\t");
		for (int i = 0; i < mX; i++){
			System.out.print(mVerRules[i] + "\t");
		}
		System.out.print("\n");
		for (int y = 0; y < mY; y++) {
			for (int x = -1; x < mX; x++) {
				if (x == -1) {
					System.out.print(mHorRules[y] + "\t");
				} else {
					System.out.print(mPuzzle[y][x] + "\t");
				}
			}
			System.out.print("\n");
		}
	}
}
