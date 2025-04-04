package symb;

import absyn.*;

import java.util.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class SymbolTable {
    public FunctionDec getPARENTFUNCTION(){return PARENTFUNCTION;}
    public FunctionDec PARENTFUNCTION;
    private final Queue<Runnable> methodQueue = new LinkedList<>();
    private HashMap<Integer,String> errors;
    public static boolean VERBOSE = false;
    public static boolean DISPLAY = true;
    public boolean display = true;
    HashMap<String, ArrayList<NodeType>> table;
    private Stack<ArrayList<String>> localDec;
    private ArrayList<String> tableStack = new ArrayList<>();
    private final int SPACES = 4;
    public int temp = 0;
    private boolean valid;
    private int currentOffset = 0;
    private int globalOffset = 0;
    private void printIndent(String s ) {
        //for( int i = 0; i < scope * SPACES; i++ ) System.out.print( " " );
        //System.out.println(s);
    }
    public int getGlobalOffset(){
        return this.globalOffset;
    }

    public int getLocalOffset(){
        return this.currentOffset;
    }
    private void printTable(){
        for (String key: table.keySet()){
            System.out.printf("%-15s:", key);
            for (NodeType s: table.get(key)){
                System.out.printf("\t%-25s", s + " (" + s.def.type + ")");
            }
            System.out.print("\n");
        }
    }
    private void printIndentTemp(String s ) {
        if (!DISPLAY) return;
        if (!display) return;
        for( int i = 0; i < scope * SPACES; i++ ) System.out.print( " " );
        System.out.println(s);
    }
    private int scope;
    public void newScope(){
        if (VERBOSE)printTable();
        localDec.push(new ArrayList<>());
        scope++;
    }
    public int getScope(){
        return scope;
    }

    public void closeScope(){
        ArrayList<String> decs = new ArrayList<>(localDec.peek());
        currentOffset += localDec.peek().size() -1;
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

    public boolean insert(String s, NodeType n){
        NodeType collision = null;
        localDec.peek().add(s);
        ArrayList<NodeType> nodelist;
        if (table.containsKey(s)){
            nodelist = table.get(s);
        } else {
            nodelist = new ArrayList<>();
        }
        if (!nodelist.isEmpty()){
            if (nodelist.get(nodelist.size()-1).name.equals(s)){
                collision = nodelist.get(nodelist.size()-1);
            }
        }
        if (collision != null&&n.def instanceof FunctionDec){
            FunctionDec f = (FunctionDec) n.def;
            FunctionDec c = (FunctionDec) collision.def;
            if ((f.body == null) == (c.body == null)){
                duplicateFunctionError(f,c);
                localDec.peek().remove(s);
                return false;
            }
        } else if (collision != null&&n.level == collision.level){
            duplicateVarError(n,collision);
            localDec.peek().remove(s);
            return false;
        }
        if (n.def instanceof FunctionDec){
            currentOffset = 0;
        }
        if (n.def instanceof VarDec){
            ((VarDec) n.def).nestLevel = (scope > 1) ? 1 : 0;
            if (((VarDec) n.def).nestLevel == 0){
                ((VarDec) n.def).offset = globalOffset--;
            } else if (n.def instanceof SimpleDec) {
                ((SimpleDec) n.def).offset = currentOffset;
            } else if (n.def instanceof ArrayDec) {
                this.currentOffset -= ((ArrayDec) n.def).size - 1;
                ((ArrayDec) n.def).offset = currentOffset;
            }
        }
        nodelist.add(n);
        table.put(s, nodelist);
        if (!(n.def instanceof FunctionDec)){
            this.currentOffset--;
        }
        System.out.println(s + " " + currentOffset);
        return true;
    }


    private NodeType lookup(String s){
        ArrayList<NodeType> nodeList = table.get(s);
        if (nodeList == null){
            return null;
        }
        return nodeList.remove(nodeList.size()-1);
    }

    public NodeType peek(String s){
        ArrayList<NodeType> nodeList = table.get(s);
        if (nodeList == null){
            return null;
        }
        try {
            return nodeList.get(nodeList.size()-1);
        } catch (Exception e){
            System.out.println(s);
            System.out.println(table);
            throw e;
        }
    }

    public SymbolTable(){
        this.errors = new HashMap<>();
        this.valid = true;
        this.table = new HashMap<>();
        this.localDec = new Stack<>();
        scope = 0;
    }

    public void showTable(DecList tree){
        printIndentTemp("Entering the global scope:");
        newScope();

        //Add input and output
        showTable(new FunctionDec(0, 4, new NameTy(0, NameTy.INT), "input", new VarDecList(null, null), null));
        showTable(new FunctionDec(0, 7, new NameTy(0, NameTy.VOID), "output",
                  new VarDecList(SimpleDec.tInt(new NilExp(0)), null), null));

        for (int i= 0; i < 2; i++) {
            DecList t = tree;
            while (t != null) {
                if (i == 0) {
                    if (t.head instanceof VarDec) {
                        showTable(t.head);
                    }
                    if (t.head instanceof FunctionDec) {
                        FunctionDec f = (FunctionDec) t.head;
                        if (f.body == null) {
                            showTable(t.head);
                        }
                    }
                } else {
                    if (t.head instanceof FunctionDec) {
                        FunctionDec f = (FunctionDec) t.head;
                        if (f.body != null){
                            showTable(f);
                        }
                    }
                }
                t = t.tail;
            }
        }
        closeScope();
        printIndentTemp("Leaving the global scope");
        System.err.println(errors.size() + " errors found");
        ArrayList<Integer> eLine = new ArrayList<>(errors.keySet());
        Collections.sort(eLine);
        for (Integer line : eLine){
            System.err.println(errors.get(line));
        }
    }

    public void showTable(SimpleDec tree){
        insert(tree.name, new NodeType(tree.name, tree, scope));
        printIndent(tree.toString());
    }

    public void showTable(ArrayDec tree){
        insert(tree.name, new NodeType(tree.name, tree, scope));
        printIndent(tree.toString());
    }

    public void showTable(FunctionDec tree){

        if (tree.body == null){
            insert(tree.func, new NodeType(tree.func, tree, scope));
            return;
        }
        if (!insert(tree.func, new NodeType(tree.func, tree, scope)))
            return;

        printIndentTemp("Entering the scope for function " + tree.func);
        newScope();
        VarDecList v = tree.params;
        if (v.head != null) {
            while (v != null) {
                showTable(v.head);
                v = v.tail;
            }
        }
        PARENTFUNCTION = tree;
        showTable((CompoundExp) tree.body);
        closeScope();
        printIndentTemp("Exiting the function scope");
    }

    public void showTable(Exp tree){
        if (tree instanceof CompoundExp){
             showTable((CompoundExp) tree);
        } else if (tree instanceof WhileExp){
             showTable((WhileExp) tree);
        } else if (tree instanceof IfExp){
             showTable((IfExp) tree);
        } else if (tree instanceof IntExp){
             showTable((IntExp) tree);
        } else if (tree instanceof BoolExp){
             showTable((BoolExp) tree);
        } else if (tree instanceof OpExp){
             showTable((OpExp) tree);
        } else if (tree instanceof AssignExp){
             showTable((AssignExp) tree);
        } else if (tree instanceof CallExp){
            showTable((CallExp) tree);
        } else if (tree instanceof VarExp){
            showTable((VarExp) tree);
        } else if (tree instanceof ReturnExp){
            showTable((ReturnExp) tree);
        } else if (tree instanceof NilExp){
            showTable((NilExp) tree);
        }
    }

    public void showTable(WhileExp tree){
        showTable(tree.test);
        if (!tree.test.dtype.type.isBool()&&!tree.test.dtype.type.isInt()){
            invalidTestError(tree);
        }
        printIndentTemp("Entering a new block: ");
        newScope();
         showTable(tree.body);
        closeScope();
        printIndentTemp("Leaving the block");
    }

    public void showTable(IfExp tree){
        showTable(tree.test);
        if (!tree.test.dtype.type.isBool()&&!tree.test.dtype.type.isInt()){
            invalidTestError(tree);
        }
        printIndentTemp("Entering a new block: ");
        newScope();
         showTable(tree.thenpart);
        closeScope();
        printIndentTemp("Leaving the block");
        if (!(tree.elsepart instanceof NilExp)){
            printIndentTemp("Entering a new block: ");
            newScope();
            showTable(tree.elsepart);
            closeScope();
            printIndentTemp("Leaving the block");
        }
    }

    public void showTable(CompoundExp tree){
        showTable(tree.vars);
        showTable(tree.exps);
    }

    public void showTable(ReturnExp tree){
        showTable(tree.exp);
        tree.dtype = tree.exp.dtype;
        if (!tree.dtype.type.equals(PARENTFUNCTION.type)){
            invalidReturnTypeError(PARENTFUNCTION, tree);
        }
    }

    public void showTable(VarDecList tree){
        if (tree.head == null){
            printIndent("No variables defined");
            return;
        }
        while (tree != null && tree.head!=null){
             showTable(tree.head);
            tree = tree.tail;
        }
    }

    public void showTable(VarDec tree){
        if (tree instanceof SimpleDec){
             showTable((SimpleDec) tree);
        } else if (tree instanceof  ArrayDec){
             showTable((ArrayDec) tree);
        }
    }

    public void showTable(Dec tree){
        if (tree instanceof VarDec) {
             showTable((VarDec) tree);
        } else if (tree instanceof FunctionDec){
             showTable((FunctionDec) tree);
        }
    }

    public void showTable(ExpList tree){
        while (tree != null){
             showTable(tree.head);
            tree = tree.tail;
        }
    }

    public SimpleDec newTemp(){
        String name = "_t" + ++temp;
        SimpleDec dec = SimpleDec.tInt(new NilExp(0));
        dec.nestLevel = 1;
        NodeType temp = new NodeType(name, dec, scope);
        this.insert(name, temp);
        return (SimpleDec) temp.def;
    }

    public ArrayDec newTempArray(NameTy type){
        String name = "_t" + ++temp;
        ArrayDec dec = ArrayDec.type(new NilExp(0), type, 0);
        dec.nestLevel = 1;
        NodeType temp = new NodeType(name, dec, scope);
        this.insert(name, temp);
        return (ArrayDec) temp.def;
    }

    public void showTable(IntExp tree){
        tree.dtype = new SimpleDec(tree.pos, 0, 0, new NameTy(tree.pos, NameTy.INT), "");
    }

    public void showTable(BoolExp tree){
        tree.dtype = new SimpleDec(tree.pos, 0, 0, new NameTy(tree.pos, NameTy.BOOL), "");
    }

    public void showTable(OpExp tree){
         showTable(tree.left);
         showTable(tree.right);
        int type = tree.type();
        if (type == -1){
            System.err.println("Error: Invalid Operator: " + (tree.pos + 1));
        }
        tree.dtype = new SimpleDec(tree.pos, 0, 0, new NameTy(tree.pos, type), "");
        if (!tree.left.dtype.type.equals(tree.right.dtype.type)){
            invalidOperatorError(tree);
        }
    }


    public void showTable(VarExp tree){
        if (tree.var instanceof IndexVar){
            IndexVar i = (IndexVar) tree.var;
            showTable(i.index);
            if (!i.index.dtype.type.isInt()){
                invalidIndexError(tree);
            }
        }
        NodeType n1 = peek(tree.var.name);
        if (n1 == null) {
            undeclaredVarError(tree.var.name, tree, tree.pos);
            tree.dtype = SimpleDec.tError(tree);
        }else {
            tree.dtype = SimpleDec.type(tree, n1.def.type);
        }
    }

    public void showTable(AssignExp tree){
        showTable(tree.rhs);
        if (tree.rhs instanceof VarExp){
            NodeType n1 = peek(((VarExp) tree.rhs).var.name);
            if (n1 == null) {
                undeclaredVarError(((VarExp) tree.rhs).var.name, tree, tree.pos);
                tree.dtype = SimpleDec.tError(tree);
                return;
            }
            tree.rhs.dtype = SimpleDec.type(tree, n1.def.type);
        }
        NodeType n2 = peek(tree.lhs.var.name);
        if (n2 == null){
            undeclaredVarError(tree.lhs.var.name, tree, tree.pos);

            tree.dtype = SimpleDec.tError(tree);
            return;
        }
        tree.lhs.dtype = SimpleDec.type(tree, n2.def.type);
        tree.dtype = SimpleDec.type(tree, tree.lhs.dtype.type);
        showTable(tree.lhs);

        if (!tree.lhs.dtype.type.equals(tree.rhs.dtype.type)){
            tree.dtype = SimpleDec.tError(tree);
            mismatchedTypeError(tree.lhs, tree.rhs, tree.pos);
        } else {
            tree.dtype = SimpleDec.type(tree, tree.lhs.dtype.type);
        }

    }

    public void showTable(NilExp tree){
        tree.dtype = SimpleDec.tVoid(tree);
    }



    public void showTable(CallExp tree){
        ArrayList<String> params = new ArrayList<>();

        NodeType n = (peek(tree.id));
        if (n == null){
            undeclaredFuncError(tree.id, tree);
            tree.dtype = SimpleDec.tError(tree);
            return;
        }
        FunctionDec n1 = (FunctionDec) n.def;
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
                showTable(list.head);
                params.add(list.head.dtype.type.toString().toLowerCase());
            } else {
                NodeType nt = peek(((VarExp)list.head).var.name);
                if (nt == null){
                    undeclaredVarError(((VarExp)list.head).var.name, list.head, list.head.pos);
                    tree.dtype = SimpleDec.tError(tree);
                    mismatchedArgsError(n1, tree.args, params, tree);
                    return;
                }
                if (nt.def instanceof ArrayDec){
                    list.head.dtype = ArrayDec.type(tree, nt.def.type, ((ArrayDec)nt.def).size);
                    if (((VarExp) list.head).var instanceof IndexVar){
                        params.add(nt.def.type.toString().toLowerCase());
                    } else {
                        params.add(nt.def.type.toString().toLowerCase() + "*");

                    }
                } else {
                    list.head.dtype = SimpleDec.type(tree, nt.def.type);
                    params.add(nt.def.type.toString().toLowerCase());
                }
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

    private void duplicateVarError(NodeType n, NodeType collision) {
        printError(collision.def.pos, "Duplicated variable '" + collision.name + "'. Already declared at line" + (n.def.pos + 1));
    }

    private void duplicateFunctionError(FunctionDec f, FunctionDec c) {
        printError(c.pos, "Duplicated function '" + f.func + "'. Already declared at line " + (f.pos + 1 ));
    }

    private void mismatchedArgsError(FunctionDec f, ExpList e, ArrayList<String> a2, Exp exp){
        printError(exp.pos, "Function expected '" + f.paramList + "' "
                + "but argument is of type '" + a2 + "'");
    }

    private void invalidOperatorError(OpExp tree){
        printError(tree.pos, "Invalid Operator '(" + tree.op  + ")'");
    }

    private void invalidTestError(WhileExp tree){
        printError(tree.pos, "Invalid while-statement test expression type '(" + tree.test.dtype.type + ")'");
    }

    private void invalidTestError(IfExp tree){
        printError(tree.pos, "Invalid if-statement test expression type '(" + tree.test.dtype.type + ")'");
    }

    public void undeclaredVarError(String name, Exp d2, int pos){
        printError(pos, "Var '" + name + "' is not declared in this scope");
    }

    public void undeclaredFuncError(String name, Exp tree){
        printError(tree.pos, "Func '" + name + "' is not declared in this scope");
    }

    public void mismatchedTypeError(VarExp d1, Exp d2, int pos) {
        printError(pos, "Type mismatch (" + d1.var.name + ":" + d1.dtype.type + " cannot be used with " + d2.dtype.type + ").");
    }

    public void invalidReturnTypeError(FunctionDec tree, ReturnExp returnExp) {
        printError(returnExp.pos, "Return type mismatch (" + tree.func + ":" + tree.type + " cannot return " + returnExp.dtype + ").");
    }

    public void invalidIndexError(VarExp tree){
        IndexVar v = (IndexVar) tree.var;
        printError(tree.pos, "Invalid index type used to access array '" + tree.var.name + "': " + v.index.dtype.toString());
    }

    public void printError(int pos, String s){
        this.valid = false;
        errors.put(pos, "Error at line " + (pos + 1) + ": " + s);
    }

}