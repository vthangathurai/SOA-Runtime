/************************************************************************

 This is a lexer specification file for JSON.  The JSONLexer.java is 
 generated from this file using jflex 1.4.1
 
 Any time this file is modified, please follow the steps below to generate
 JSONLexer.java
 
 1.  In a command window, chdir to this directory.
 2.  run 
 		java -jar jflex.jar json.flex
 	from this directory and copy the generated JSONLexer.java to 
 		runtime\binding-framework\src\main\java\org\ebayopensource\turmeric\binding\impl\parser\json
    to override the existing file.
    
 	 You may need to modify your gen.bat to point to your jflex 1.4.1 
     jflex can be downloaded from http://jflex.de/ or from \\d-sjc-wdeng1\Software\jflex-1.4.1.
     
 3.  manually change the generated JSONLexer.java to make it able to handle utf16 by
     change:
      int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
     to
       int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput > 255 ? 255 : zzInput] ];
        
   
 ************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.json;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.ParseException;

%%

%{
    private static int NUMBER_TOKEN_TYPES = JSONTokenType.values().length;
	private static final char[][] SPECIAL_CHARS 
		= new char[NUMBER_TOKEN_TYPES][6];
	static {
		for (int i=0; i<SPECIAL_CHARS.length; i++) {
			for (int j=0; j<6; j++) {
				SPECIAL_CHARS[i][j] = (char) 0;
			}
		}
		SPECIAL_CHARS[JSONTokenType.LPAR.ordinal()][0]='(';
		SPECIAL_CHARS[JSONTokenType.RPAR.ordinal()][0]=')';
		SPECIAL_CHARS[JSONTokenType.COMMA.ordinal()][0]=',';
		SPECIAL_CHARS[JSONTokenType.COLON.ordinal()][0]=':';
		SPECIAL_CHARS[JSONTokenType.LBLANKET.ordinal()][0]='[';
		SPECIAL_CHARS[JSONTokenType.RBLANKET.ordinal()][0]=']';
		SPECIAL_CHARS[JSONTokenType.LCURLY.ordinal()][0]='{';
		SPECIAL_CHARS[JSONTokenType.RCURLY.ordinal()][0]='}';
		SPECIAL_CHARS[JSONTokenType.FALSE.ordinal()][0]='f';
		SPECIAL_CHARS[JSONTokenType.FALSE.ordinal()][1]='a';
		SPECIAL_CHARS[JSONTokenType.FALSE.ordinal()][2]='l';
		SPECIAL_CHARS[JSONTokenType.FALSE.ordinal()][3]='s';
		SPECIAL_CHARS[JSONTokenType.FALSE.ordinal()][4]='e';
		SPECIAL_CHARS[JSONTokenType.TRUE.ordinal()][0]='t';
		SPECIAL_CHARS[JSONTokenType.TRUE.ordinal()][1]='r';
		SPECIAL_CHARS[JSONTokenType.TRUE.ordinal()][2]='u';
		SPECIAL_CHARS[JSONTokenType.TRUE.ordinal()][3]='e';
		SPECIAL_CHARS[JSONTokenType.NULL.ordinal()][0]='n';
		SPECIAL_CHARS[JSONTokenType.NULL.ordinal()][1]='u';
		SPECIAL_CHARS[JSONTokenType.NULL.ordinal()][2]='l';
		SPECIAL_CHARS[JSONTokenType.NULL.ordinal()][3]='l';
	}
	
	StringBuilder string = new StringBuilder(16); 
	int prefixEnd = -1;
	
	public int getRow() {
		return yyline;
	}
	
	public int getColumn() {
		return yycolumn;
	}

	JSONToken createToken(JSONTokenType type) {
		JSONToken token =  new JSONToken(type, yyline, yycolumn);
		if (type != JSONTokenType.STRING) {
			int len = zzMarkedPos-zzStartRead;
			token.m_chars = new char[len];
			System.arraycopy(zzBuffer, zzStartRead, token.m_chars, 0, len);
			return token;
		}
		char[] specialChars = SPECIAL_CHARS[type.ordinal()];
		if ((char) 0 != specialChars[0]) {
			token.m_chars = (char[])specialChars;
			return token;
		}
		char[] chars = string.get();
		int len = string.length();
		token.m_chars = new char[len];
		System.arraycopy(chars, 0, token.m_chars, 0, len);
		token.m_prefixEnd = prefixEnd;
		
		return token;
	}
%} 

%class JSONLexer
%public
%type JSONToken
%unicode
%line
%column
%char
%state NAME_STRING, VALUE_STRING
%full
%table
%yylexthrow ParseException
/* 

*/
/* main character classes */
Exponent 	= e|e+|e-|E|E+|E-
NonZeroDigit= [1-9]
Digit 		= 0|[1-9]
Digits		= {NonZeroDigit}{Digit}*
Exp			= Exponent{Digits}
Fraction	= \.{Digits}
Integer		= [+|-]?{Digits}
Number		= {Integer}|{Integer}{Fraction}|{Integer}{Exp}|{Integer}{Fraction}{Exp}
HEX_D       = [a-fA-F0-9]
INT = [-]?[0-9]+
DOUBLE = {INT}((\.[0-9]+)?([eE][-+]?[0-9]+)?)

LineTerminator = \r|\n|\r\n

WhiteSpace = {LineTerminator} | [ \t\f]

/* string literals */
StringCharacter = [^\r\n\.\"\\]

%state STRING

%% 

<YYINITIAL> {
  
  /* string literal */
  \"                             { yybegin(STRING); string.setLength(0); prefixEnd = -1; }

  {INT}	{ return createToken(JSONTokenType.NUMBER); }  
  {DOUBLE}	{ return createToken(JSONTokenType.NUMBER); }  
  "," { return createToken(JSONTokenType.COMMA); }
  ":" { return createToken(JSONTokenType.COLON); }
/*
  "(" { return createToken(JSONTokenType.LPAR); }
  ")" { return createToken(JSONTokenType.RPAR); }
*/
  "[" { return createToken(JSONTokenType.LBLANKET); }
  "]" { return createToken(JSONTokenType.RBLANKET); }
  "{" { return createToken(JSONTokenType.LCURLY); }
  "}" { return createToken(JSONTokenType.RCURLY); }
  "true" { return createToken(JSONTokenType.TRUE); }
  "TRUE" { return createToken(JSONTokenType.TRUE); }
  "True" { return createToken(JSONTokenType.TRUE); }
  "false" { return createToken(JSONTokenType.FALSE); }
  "FALSE" { return createToken(JSONTokenType.FALSE); }
  "False" { return createToken(JSONTokenType.FALSE); }
  "null" { return createToken(JSONTokenType.NULL); }


  {WhiteSpace}	{ }
}

<STRING> {
  \"                             {  yybegin(YYINITIAL); 
  									return createToken(JSONTokenType.STRING);
  								 }
  
  {StringCharacter}+             { string.append( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead ); }
  "\."							 { prefixEnd = string.length(); string.append('.'); }
  /* escape sequences */
  "\\b"                          { string.append( '\b' ); }
  "\\t"                          { string.append( '\t' ); }
  "\\n"                          { string.append( '\n' ); }
  "\\f"                          { string.append( '\f' ); }
  "\\r"                          { string.append( '\r' ); }
  "\\\""                         { string.append( '\"' ); }
  "\\\\"                         { string.append( '\\' ); }
  "\\\/"                         { string.append( '/' ); }
  \\u{HEX_D}{HEX_D}{HEX_D}{HEX_D} {       String hexNumber = yytext();
  										  try{
                                             int ch=Integer.parseInt(hexNumber.substring(2),16);
                                             string.append((char)ch);
                                          } catch(Exception e){
  											throw new NumberFormatException("Invalid hex number: " + hexNumber);
                                          }
                        		}
  
  /* error cases */
  \\.                            { throw new ParseException(yytext(), yyline, yycolumn, "Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new ParseException(yytext(), yyline, yycolumn, "Unterminated string at end of line"); }
}

. {
	throw new ParseException(yytext(), yyline, yycolumn, "Illegal Character.");
}

