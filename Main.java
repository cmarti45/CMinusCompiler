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
import absyn.*;
import java_cup.runtime.*;
   
class Main {
  public final static boolean SHOW_TREE = true;
  static public void main(String argv[]) {    
    /* Start the parser */
    try {
      Lexer lexer = new Lexer(new FileReader(argv[0]));
            if (argv.length > 1 && argv[1].equals("S")) {
                Scanner scanner = new Scanner(lexer);
                Symbol tok = null;
                while ((tok = scanner.getNextToken()) != null) {
                    System.out.print(sym.terminalNames[tok.sym]);
                    System.out.println();
                }
            } else {

                lexer.yyreset(new FileReader(argv[0]));
                parser p = new parser(lexer);
                Absyn result = (Absyn) (p.parse().value);
                if (SHOW_TREE && result != null) {
                    System.out.println("The abstract syntax tree is:");
                    AbsynVisitor visitor = new ShowTreeVisitor();
                    result.accept(visitor, 0);
                }
            }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}
