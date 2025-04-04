JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-cp /Users/coreymartin/Downloads/java-cup-bin-11b-20160615
CUP=$(JAVA) $(CLASSPATH) java_cup.Main

all: Main.class

Main.class: absyn/*.java asm/*.java symb/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java Main.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: tiny.flex
	$(JFLEX) tiny.flex

parser.java: tiny.cup
	#$(CUP) -dump -expect 3 tiny.cup
	$(CUP) -expect 3 tiny.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class asm/*.class absyn/*.class symb/*.class *~
