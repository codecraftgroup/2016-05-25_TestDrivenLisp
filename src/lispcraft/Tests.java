package lispcraft;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class Tests {

	private boolean isEssExpression(String text) {
		return text.charAt(0) == '(' && text.charAt(text.length()-1) == ')';
	}
	
	private Object evaluateEssExpression(String text, Map<String, Object> symtable) {
		String innards = text.substring(1, text.length()-1);
		String[] parts = innards.split(" ");
		final String key = parts[0];
		if(key.equals("quote"))
			return new Symbol(parts[1]);
		
		if(key.equals("+")) {
			return this.evalAddition(parts);
		}
		if(key.equals("*")) {
			return this.evalMultiply(parts);
		}
		throw new RuntimeException();
	}
	
	Object eval(String text, Map<String, Object> symtable) {
		try{
		    return Double.parseDouble(text);
		}
		catch(Exception ex) {
			if (isEssExpression(text)) {
				return evaluateEssExpression(text, symtable);
			}
			
			String unquotedText = text.replaceAll("\"","");
			Object value = symtable.get(unquotedText);
			if(value == null){
				return unquotedText;
			} else {
				return value;
			}
		}
	}
	
	Object evalMultiply(String[] parts) {
		int result = 1;
		for (int i = 1; i < parts.length; i++) {
			result *= Integer.valueOf(parts[i]);
		}
			return new Integer(result);
	}

	Object evalAddition(String[] parts) {
		int result = 0;
		for (int i = 1; i < parts.length; i++) {
			result += Integer.valueOf(parts[i]);
		}
		return new Integer(result);	
	}
	
	Object eval(String text){
		return eval(text, Collections.emptyMap());
	}
	
	@Test
	public void numberLiteralsAreThemselves() {
		// given
		String input = "1";
		
		// when
	    Object output = eval(input);
		
		//then
		assertEquals(output, 1.0d);
	}
	
	@Test
	public void stringLiteralsAreThemselves() {
		// given
		String input = "\"this is a string\"";
		
		// when
		Object output = eval(input);
		
		//then
		assertEquals(output, "this is a string");
	}
	
	enum Foo {A, B}
	
	@Test
	public void symbolsAreTheAssignedValue() {
		
		// given
		Map <String, Object> symbolTable = new HashMap<String, Object>();
		String input = "x";
		symbolTable.put("x", "5");
		
		// when
		Object output = eval(input,symbolTable);
		
		// then
		assertEquals(output, "5");
	}

	@Test
	public void bareWordsAreReadAsSymbols() {
		
		String input = "(quote x)";
		
		

		// when
		Object output = eval(input);
		
		// then
		assertEquals(output, new Symbol("x"));
		
	}
	static class Symbol{
		final String value;
		
		public Symbol(String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Symbol) return ((Symbol)obj).value.equals(value);
		    return false;
		}
		
	}

	@Test(expected = Exception.class)
	public void callingANonFunctionThrows() {
		eval("(1 2 3)");
	}
	
	@Test
	public void addFunction() {
		assertEquals(eval("(+ 1 2 3)"), 6);
	}
	
	@Test
	public void multiplyFunction() {
		assertEquals(eval("(* 1 1 3)"), 3);
	}

//	@Test
//	public void nestedSExpressionFunction() {
//		assertEquals(eval("(+ (* 1 3) 1)"), 4);
//	}


}
