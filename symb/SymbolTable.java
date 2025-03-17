package symb;

import absyn.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    public static boolean DISPLAY = true;
    HashMap<String, ArrayList<NodeType>> table;
    private Stack<ArrayList<String>> localDec;
    private ArrayList<String> tableStack = new ArrayList<>();
    private final int SPACES = 4;
    private void printIndent(String s, int level ) {
        //for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
        //System.out.println(s);
    }
    private void printIndentTemp(String s ) {
        if (!DISPLAY) return;
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
            System.out.println(n.level + " " + collision.level);
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

    private NodeType peek(String s){
        ArrayList<NodeType> nodeList = table.get(s);
        if (nodeList == null){
            return null;
        }
        return nodeList.getLast();
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
        printIndentTemp("Entering the scope for function " + tree.func);
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
        } else if (tree instanceof IntExp){
            showTable((IntExp) tree, level);
        } else if (tree instanceof BoolExp){
            showTable((BoolExp) tree, level);
        } else if (tree instanceof OpExp){
            showTable((OpExp) tree, level);
        } else if (tree instanceof AssignExp){
            showTable((AssignExp) tree, level);
        } else if (tree instanceof CallExp){
            showTable((CallExp) tree, level);
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
        closeScope();
        printIndentTemp("Leaving the block");
        printIndentTemp("Entering a new block: ");
        newScope();
        showTable(tree.elsepart, level);
        closeScope();
        printIndentTemp("Leaving the block");
    }

    public void showTable(CompoundExp tree, int level){
        showTable(tree.vars, level);
        showTable(tree.exps, level);
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

    public void showTable(IntExp tree, int level){
        tree.dtype = new SimpleDec(tree.pos, new NameTy(tree.pos, NameTy.INT), "");
    }

    public void showTable(BoolExp tree, int level){
        tree.dtype = new SimpleDec(tree.pos, new NameTy(tree.pos, NameTy.BOOL), "");
    }

    public void showTable(OpExp tree, int level){
        showTable(tree.left, level);
        showTable(tree.right, level);
        int type = tree.type();
        if (type == -1){
            System.err.println("Error: Invalid Operator: " + tree.pos);
        }
        tree.dtype = new SimpleDec(tree.pos, new NameTy(tree.pos, type), "");
        if (!tree.left.dtype.type.equals(tree.right.dtype.type)){
            System.err.println("Error: Invalid Operator: " + tree.pos);
        }
    }

    public void showTable(AssignExp tree, int level){
        if (tree.rhs instanceof VarExp){
            NodeType n1 = peek(tree.lhs.var.name);
            tree.rhs.dtype = SimpleDec.type(tree, n1.def.type);
        } else {
            showTable(tree.rhs, level);
        }
        NodeType n2 = peek(tree.lhs.var.name);
        tree.lhs.dtype = SimpleDec.type(tree, n2.def.type);
        tree.dtype = SimpleDec.type(tree, tree.lhs.dtype.type);

        if (!tree.lhs.dtype.type.equals(tree.rhs.dtype.type)){
            tree.dtype = SimpleDec.tError(tree);
            mismatchedTypeError(tree.lhs, tree.rhs, tree.pos);
        } else {
            tree.dtype = SimpleDec.type(tree, tree.lhs.dtype.type);
        }

    }

    public void mismatchedTypeError(VarExp d1, Exp d2, int pos){
        System.err.println("Error: Type mismatch at " + pos + " "
                + d1.var.name + ":" + d1.dtype.type.toString().toLowerCase() + " "
                + d2.dtype.type);
    }

    public void showTable(CallExp tree, int level){
        ArrayList<String> params = new ArrayList<>();
        System.out.println(tree.args.head.getClass());
        System.out.println(tree.id);
        FunctionDec n1 = (FunctionDec) (peek(tree.id).def);
        ExpList list = tree.args;
        if (list.head instanceof NilExp){
            if (n1.paramList.isEmpty()){
                tree.dtype = SimpleDec.type(tree, n1.type);
            } else {
                tree.dtype = SimpleDec.tError(tree);
                mismatchedArgsError(n1, tree.args, params, tree);
            }
            return;
        }
        while (list!=null&&list.head != null) {
            if (!(list.head instanceof VarExp)) {
                showTable(list.head, level);
                params.add(list.head.dtype.type.toString().toLowerCase());
            } else {
                NodeType nt = peek(((VarExp)list.head).var.name);
                list.head.dtype = SimpleDec.type(tree, nt.def.type);
                params.add(nt.def.type.toString().toLowerCase());
            }
            if (list.head instanceof VarExp) {
                System.out.println(((VarExp)list.head).var.name + " " + list.head.dtype.type);
            } else {
                System.out.println(list.head.dtype.type);
            }
            list = list.tail;
        }

        if (params.equals(n1.paramList)){
            tree.dtype = SimpleDec.type(tree, n1.type);
        } else {
            tree.dtype = SimpleDec.tError(tree);
            mismatchedArgsError(n1, tree.args, params, tree);
        }
    }


    public void mismatchedArgsError(FunctionDec f, ExpList e, ArrayList<String> a2, Exp exp){
        System.err.println("Error: Function expected " + f.paramList + " "
                + "but argument is of type \'" + a2 + "\'");
    }

}
