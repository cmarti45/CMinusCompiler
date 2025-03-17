/*
  Created by: Fei Song
  File Name: Main.java
  To Build: 
  After the Scanner.java, tiny.flex, and tiny.cup have been processed, do:
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny

  where gcd.tiny is an test input file for the tiny language.
*/
   
import java.io.*;
import java.util.Arrays;
import java.util.List;

import absyn.*;
import java_cup.runtime.*;
import symb.SymbolTable;

class Main {
  public final static boolean SHOW_TREE = true;
  static public void main(String argv[]) {    
    /* Start the parser */
    try {
      Lexer lexer = new Lexer(new FileReader(argv[0]));
      List<String> argList = Arrays.asList(argv);
                if (argList.contains("-S")) {
                    Scanner scanner = new Scanner(lexer);
                    Symbol tok = null;
                    while ((tok = scanner.getNextToken()) != null) {
                        System.out.print(sym.terminalNames[tok.sym]);
                        System.out.println();
                    }
                    return;
                }
                if (!argList.contains("-s")){
                    SymbolTable.DISPLAY = false;
                }
                lexer.yyreset(new FileReader(argv[0]));
                parser p = new parser(lexer);
                Absyn result = (Absyn) (p.parse().value);
                if (argList.contains("-a")){
                    System.out.println("The abstract syntax tree is:");
                    AbsynVisitor visitor = new ShowTreeVisitor();
                    result.accept(visitor, 0);
                }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}
