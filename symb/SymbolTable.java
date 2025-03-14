package symb;

import absyn.*;
import org.w3c.dom.Node;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class SymbolTable {
    HashMap<String, ArrayList<NodeType>> table;
    private Stack<ArrayList<String>> localDec;
    private ArrayList<String> tableStack = new ArrayList<>();
    private final int SPACES = 4;
    private void printIndent(String s, int level ) {
        //for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
        //System.out.println(s);
    }
    private void printIndentTemp(String s ) {
        for( int i = 0; i < scope * SPACES; i++ ) System.out.print( " " );
        System.out.println(s);
    }
    private int scope;
    private void newScope(){
        localDec.push(new ArrayList<>());
        scope++;
    }

    private void closeScope(){
        ArrayList<String> collision = new ArrayList<>();
        ArrayList<String> decs = new ArrayList<>(localDec.peek());
        localDec.peek().forEach(s -> {
            NodeType n = lookup(s);
            decs.remove(s);
            if (decs.contains(s) && n.def instanceof FunctionDec){
                //Skip function prototype
            } else {
                printIndentTemp(n.def.toString());
            }

        });
        localDec.pop();
        scope--;
    }

    private boolean insert(String s, NodeType n){
        NodeType collision = null;
        localDec.peek().add(s);
        ArrayList<NodeType> nodelist;
        if (table.containsKey(s)){
            nodelist = table.get(s);
        } else {
            nodelist = new ArrayList<>();
        }
        if (!nodelist.isEmpty()){
            if (nodelist.getLast().name.equals(s)){
                collision = nodelist.getLast();
            }
        }
        if (collision != null&&n.def instanceof FunctionDec){
            FunctionDec f = (FunctionDec) n.def;
            FunctionDec c = (FunctionDec) collision.def;
            if ((f.body == null) == (c.body == null)){
                System.out.println("COLLISSION ERROR, DUPLICATE DECLARATION: " + n.def); //TODO: throw error
                localDec.peek().remove(s);
                return false;
            }
        } else if (collision != null){
            System.out.println("COLLISSION ERROR, DUPLICATE DECLARATION: " + n.def); //TODO: throw error
            localDec.peek().remove(s);
            return false;
        }
        nodelist.add(n);
        table.put(s, nodelist);
        return true;
    }

    private NodeType lookup(String s){
        ArrayList<NodeType> nodeList = table.get(s);
        if (nodeList == null){
            return null;
        }
        return nodeList.removeLast();
    }

    private void delete(String s){
        ArrayList<NodeType> nodeList = table.get(s);
        nodeList.removeLast();
        if (nodeList.isEmpty()){
            table.remove(s);
        }
    }

    public SymbolTable(){
        this.table = new HashMap<>();
        this.localDec = new Stack<>();
        scope = 0;
    }

    public void showTable(DecList tree, int level){
        printIndentTemp("Entering the global scope:");
        newScope();
        while (tree != null){
            showTable(tree.head, level);
            tree = tree.tail;
        }
        closeScope();
        printIndentTemp("Leaving the global scope");
    }

    public void showTable(SimpleDec tree, int level){
        insert(tree.name, new NodeType(tree.name, tree, level));
        printIndent(tree.toString(), level);
    }

    public void showTable(ArrayDec tree, int level){
        insert(tree.name, new NodeType(tree.name, tree, level));
        printIndent(tree.toString(), level);
    }

    public void showTable(FunctionDec tree, int level){
        if (!insert(tree.func, new NodeType(tree.func, tree, level)))
            return;
        if (tree.body == null)
            return;
        printIndentTemp("Entering the scope for function " + tree.func + ":" + level);
        newScope();
        showTable(tree.body, level);
        closeScope();
        printIndentTemp("Exiting the function scope");
    }

    public void showTable(Exp tree, int level){
        if (tree instanceof CompoundExp){
            showTable((CompoundExp) tree, level);
        } else if (tree instanceof WhileExp){
            showTable((WhileExp) tree, level);
        } else if (tree instanceof IfExp){
            showTable((IfExp) tree, level);
        }
    }

    public void showTable(WhileExp tree, int level){
        printIndentTemp("Entering a new block: ");
        newScope();
        showTable(tree.body, level);
        closeScope();
        printIndentTemp("Leaving the block");
    }

    public void showTable(IfExp tree, int level){
        printIndentTemp("Entering a new block: ");
        newScope();
        showTable(tree.thenpart, level);
        showTable(tree.elsepart, level);
        closeScope();
        printIndentTemp("Leaving the block");
    }

    public void showTable(CompoundExp tree, int level){
        showTable(tree.exps, level);
        showTable(tree.vars, level);
    }

    public void showTable(VarDecList tree, int level){
        if (tree.head == null){
            printIndent("No variables defined", level);
            return;
        }
        while (tree != null && tree.head!=null){
            showTable(tree.head, level);
            tree = tree.tail;
        }
    }

    public void showTable(VarDec tree, int level){
        if (tree instanceof SimpleDec){
            showTable((SimpleDec) tree, level);
        } else if (tree instanceof  ArrayDec){
            showTable((ArrayDec) tree, level);
        }
    }

    public void showTable(Dec tree, int level){
        if (tree instanceof VarDec) {
            showTable((VarDec) tree, level);
        } else if (tree instanceof FunctionDec){
            showTable((FunctionDec) tree, level);
        }
    }

    public void showTable(ExpList tree, int level){
        while (tree != null){
            showTable(tree.head, level);
            tree = tree.tail;
        }
    }
}
