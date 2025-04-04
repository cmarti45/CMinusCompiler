package asm;

import absyn.*;
import symb.NodeType;
import symb.SymbolTable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Asm {
    private String input = "";
    private StringBuilder asm = new StringBuilder(); //string output to .tm file
    private SymbolTable symbolTable = new SymbolTable();
    private int address = 0;
    static private final int PC = 7;
    static private final int GP = 6;
    static private final int FP = 5;
    static private final int AC = 0;
    static private final int AC1 = 1;

    private enum Operations {
        HALT, IN, OUT, ADD, SUB, MUL, DIV, LD, ST,
        LDA, LDC, JLT, JLE, JGT, JGE, JEQ, JNE;
    }

    private List<Operations> registerOnly = Arrays.asList(
            Operations.HALT, Operations.IN, Operations.OUT,
            Operations.ADD, Operations.SUB, Operations.MUL,
            Operations.DIV
    );

    private void header(String filename) {
        asm.append("* C-Minus Compilation to TM Code\n");
        String out = filename.substring(0, filename.lastIndexOf(".")) + ".tm";
        asm.append("* File: " + out + "\n");
    }

    private void prelude() {
        asm.append("* Standard prelude:\n");
        asm.append("  0:     LD  6,0(0) \t\n");
        asm.append("  1:    LDA  5,0(6) \t\n");
        asm.append("  2:     ST  0,0(0) \t\n");
        asm.append("* Jump around i/o routines here\n");
        asm.append("* code for input routine\n");
        asm.append("  4:     ST  0,-1(5) \t\n");
        asm.append("  5:     IN  0,0,0 \t\n");
        asm.append("  6:     LD  7,-1(5) \t\n");
        asm.append("* code for output routine\n");
        asm.append("  7:     ST  0,-1(5) \t\n");
        asm.append("  8:     LD  0,-2(5) \t\n");
        asm.append("  9:    OUT  0,0,0 \t\n");
        asm.append(" 10:     LD  7,-1(5) \t\n");
        asm.append("  3:    LDA  7,7(7) \t\n");
        asm.append("* End of standard prelude.\n");
        address += 10;
    }

    public void generateAssembly(String filename, DecList tree) {
        this.symbolTable.display = false;
        try {
            this.symbolTable.newScope();
            header(filename);
            prelude();
            this.genCode(tree);
            end();
        } catch (Exception e1){
            String write = filename.substring(0, filename.lastIndexOf('.')) + ".tm";
            /* export assembly to external file */
            try {
                File file = new File(write);
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(asm.toString());
                bw.close();
                System.out.println("Compile complete, saved to " + write);
            } catch(IOException e) {
                System.out.println("Error: failed to write to " + write);
            }
            throw e1;
        }

//        if(this.symbolTable.error) {
//            return;
//        }
        /* output file name with path and .tm file type */
        String write = filename.substring(0, filename.lastIndexOf('.')) + ".tm";
        /* export assembly to external file */
        try {
            File file = new File(write);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(asm.toString());
            bw.close();
            System.out.println("Compile complete, saved to " + write);
        } catch(IOException e) {
            System.out.println("Error: failed to write to " + write);
        }
    }

    public void genCode(DecList tree) {
        DecList t = tree;
        this.symbolTable.showTable(new FunctionDec(0, 4, new NameTy(0, NameTy.INT), "input", new VarDecList(null, null), null));
        this.symbolTable.showTable(new FunctionDec(0, 7, new NameTy(0, NameTy.VOID), "output",
                new VarDecList(SimpleDec.tInt(new NilExp(0)), null), null));
        while(tree != null) {
            if (tree.head instanceof FunctionDec){
                FunctionDec f = (FunctionDec) tree.head;
                if (f.body == null) {
                    genCode(tree.head);
                }
            }
            tree = tree.tail;
        }
        while(t != null) {
            if (t.head instanceof FunctionDec){
                FunctionDec f = (FunctionDec) t.head;
                if (f.body == null) {
                    t = t.tail;
                    continue;
                }
            }
            if (t.head instanceof VarDec){
                this.emitComment("allocating global var: " + ((VarDec)t.head).name);
            }
            genCode(t.head);
            t = t.tail;
        }
    }

    public void genCode(Dec tree) {
        if(tree instanceof VarDec)
            genCode((VarDec)tree);
        else if(tree instanceof FunctionDec) {
            genCode((FunctionDec)tree);
        }
    }

    private void genCode(FunctionDec tree) {
        this.address++;
        int jmpAround = this.address;
        tree.funaddr = ++this.address;
        NodeType s = new NodeType(tree.func, tree, this.symbolTable.getScope());
        if(!this.symbolTable.insert(tree.func, s)){
            // Todo: error
            //this.symbolTable.error("Function redefinition error of function " + tree.name + " on line: " + tree.pos);
        }
        this.symbolTable.newScope();
        NodeType ofp = new NodeType("_ofp", SimpleDec.tInt(new NilExp(0)), 0);
        ((SimpleDec) ofp.def).nestLevel = 1;
        this.symbolTable.insert("_ofp", ofp);
        NodeType ret = new NodeType("_ret", SimpleDec.tInt(new NilExp(0)), 0);
        ((SimpleDec) ret.def).nestLevel = 1;
        this.symbolTable.insert("_ret", ret);

        this.emitComment("processing function: " + tree.func);
        this.emitCode(this.address, Operations.ST, 0, -1, 5);
        genCodeParam(tree.params);
        if (tree.body != null) {
            symbolTable.PARENTFUNCTION = tree;
            genCode(tree.body);
        }
        this.emitCode(++this.address, Operations.LD, PC, -1, FP);
        this.emitCode(jmpAround, Operations.LDA, PC, this.address - jmpAround, PC, "jump around " + tree.func + " body");
        this.symbolTable.closeScope();
    }
    private void genCodeParam(VarDecList tree){
        if (tree.head != null) {
            while (tree != null) {
                genCodeParam(tree.head);
                tree = tree.tail;
            }
        }
    }
    private void genCodeParam(VarDec tree) {
        this.emitComment("allocating parameter: " + tree.name);
        if (tree instanceof ArrayDec) {
            tree.nestLevel = (this.symbolTable.getScope() > 1) ? 1 : 0;
            NodeType param = new NodeType(tree.name, tree, this.symbolTable.getScope());
            if(!this.symbolTable.insert(tree.name, param)){
//                this.symbolTable.error("Parameter redefinition error");
            }
        }
        else {
            tree.nestLevel = (this.symbolTable.getScope() > 1) ? 1 : 0;
            NodeType param = new NodeType(tree.name, tree,  this.symbolTable.getScope());
            if(!this.symbolTable.insert(tree.name, param)){
//                this.symbolTable.error("Parameter redefinition error");
            }
        }
    }

    private void genCode(Exp tree){
        if (tree instanceof CompoundExp){
            this.symbolTable.newScope();
            System.out.println("NewScope");
            genCode((CompoundExp) tree);
            this.symbolTable.closeScope();
            System.out.println("CloseSCope");
        } else if (tree instanceof AssignExp){
            genCode((AssignExp) tree);
        } else if (tree instanceof IntExp){
            genCode((IntExp) tree);
        } else if (tree instanceof  VarExp){
            genCode((VarExp) tree, true);
        } else if (tree instanceof IfExp){
            genCode((IfExp) tree);
        } else if (tree instanceof WhileExp){
            genCode((WhileExp) tree);
        } else if (tree instanceof OpExp){
            genCode((OpExp) tree);
        } else if (tree instanceof ReturnExp){
            genCode((ReturnExp) tree);
        } else if (tree instanceof CallExp){
            genCode((CallExp) tree);
        }
    }

    private void genCode(ReturnExp tree) {
        if (tree.exp != null) {
            genCode(tree.exp);
        }
        this.emitCode(++this.address, Operations.LD, PC, -1, FP, "return to caller");
    }

    private void genCode(IfExp tree) {
//        if (tree.test instanceof ExpCall){
//            this.symbolTable.checkType((ExpCall)tree.test);
//        }
        this.emitComment("-> if");
        genCode(tree.test);
        this.address++;
        int jmpAround = this.address;
        genCode(tree.thenpart);
        this.emitCode(jmpAround, Operations.JEQ, AC, this.address + 1 - jmpAround, PC, "if: jmp to else");
        if (!(tree.elsepart instanceof NilExp)) {
            jmpAround = ++this.address;
            genCode(tree.elsepart);
            this.emitCode(jmpAround, Operations.LDA, PC, this.address - jmpAround, PC, "if: jmp to end of else");
        }
        this.emitComment("<- if");
    }

    private void genCode(WhileExp tree) {
//        if (tree.test instanceof ExpCall){
//            this.symbolTable.checkType((ExpCall)tree.test);
//        }
        this.emitComment("-> while");
        int test = this.address;
        genCode(tree.test);
        this.address++;
        int jmpAround = this.address;
        genCode(tree.body);
        this.emitCode(++this.address, Operations.LDA, PC, test - this.address, PC, "while: unconditional jmp to start");
        this.emitCode(jmpAround, Operations.JEQ, AC, this.address - jmpAround, PC, "while: jmp around on false");
        this.emitComment("<- while");
    }

    private void genCode(AssignExp tree){
        SimpleDec temp = this.symbolTable.newTemp();
        genCode(tree.lhs, false);
        this.emitCode(++this.address, Operations.ST, AC, temp.offset, FP, "push left");
//        if(tree.rhs instanceof ExpCall){
//            this.symbolTable.checkType((ExpCall)tree.rhs);
//        }
        genCode(tree.rhs);
        this.emitCode(++this.address, Operations.LD, AC1, temp.offset, FP);
        this.emitCode(++this.address, Operations.ST, AC, 0, AC1, "assign: store value");
    }

    private void genCode(IntExp tree) {
        this.emitCode(++this.address, Operations.LDC, AC, Integer.parseInt(tree.value), 0, "load constant");
    }

    private void genCode(VarExp tree, boolean value) {
        Operations load = value ? Operations.LD : Operations.LDA;
        NodeType match = this.symbolTable.peek(tree.var.name);
        if(match.def instanceof SimpleDec) { //normal variable
            SimpleDec var = (SimpleDec) match.def;
            this.emitComment("Looking up id: " + tree.var.name);
            if (var.nestLevel == 0){
                this.emitCode(++this.address, load, AC, var.offset, GP, "load id");
            } else {
                this.emitCode(++this.address, load, AC, var.offset, FP, "load id");
            }
        } else { //array variable
            ArrayDec var = (ArrayDec) match.def;
            this.emitComment("Looking up id: " + tree.var.name);
            this.emitComment("generating index");
            if (tree.var instanceof IndexVar) {
                genCode(((IndexVar)tree.var).index);
                this.emitCode(++this.address, Operations.LD, AC1, var.offset, FP, "top of array");
                this.emitCode(++this.address, Operations.ADD, AC, AC1, AC);
                this.emitCode(++this.address, load, AC, 0, AC, "load id");
            } else {
}

        }
    }
    private void genCode(CallExp tree) {
        NodeType n = symbolTable.peek(tree.id);
        FunctionDec func = (FunctionDec) n.def;
        genCode(tree.args, func);
        this.emitComment("call to function: " + tree.id);
        this.emitCode(++this.address, Operations.ST, FP, symbolTable.getLocalOffset(), FP, "push ofp");
        this.emitCode(++this.address, Operations.LDA, FP, symbolTable.getLocalOffset(), FP, "push frame");
        this.emitCode(++this.address, Operations.LDA, AC, 1, PC, "load ac with ret ptr");
        this.emitCode(++this.address, Operations.LDA, PC, func.funaddr - this.address - 1, PC, "jump to " + func.func + " loc");
        this.emitCode(++this.address, Operations.LD, FP, 0, FP, "pop frame");
    }


    public void genCode(ExpList tree, FunctionDec func) {
        List<Integer> argAddresses = new LinkedList<>();
        VarDecList params = func.params;
        if (tree.head instanceof NilExp) return;
        while(tree != null&&params.head != null) {
            genCode(tree.head);
            if (params.head instanceof ArrayDec) {
                ArrayDec temp = symbolTable.newTempArray(params.head.type);
                this.emitCode(++this.address, Operations.ST, AC, temp.offset, FP, "store arg val");
                argAddresses.add(temp.offset);
                tree = tree.tail;
            } else {
                SimpleDec temp = symbolTable.newTemp();
                this.emitCode(++this.address, Operations.ST, AC, temp.offset, FP, "store arg val");
                argAddresses.add(temp.offset);
                tree = tree.tail;
            }
        }
        int i = 0;
        for(Integer ad : argAddresses) {
            this.emitCode(++this.address, Operations.LD, AC, ad, FP, "load arg val");
            this.emitCode(++this.address, Operations.ST, AC, symbolTable.getLocalOffset() - (2 + i), FP, "store arg val in next frame");
            i++;
        }

    }

    private void genCode(OpExp tree) {
        SimpleDec temp;
//        if (tree.left instanceof ExpCall){
//            this.symbolTable.checkType((ExpCall)tree.left);
//        }
        genCode(tree.left);
        temp = this.symbolTable.newTemp();
        this.emitCode(++this.address, Operations.ST, AC, temp.offset, FP, "push left");
//        if (tree.right instanceof ExpCall){
//            this.symbolTable.checkType((ExpCall)tree.right);
//        }
        genCode(tree.right);
        this.emitCode(++this.address, Operations.LD, AC1, temp.offset, FP, "load left" + tree.op);
        switch(tree.op) {
            case OpExp.PLUS:
                this.emitCode(++this.address, Operations.ADD, AC, AC1, AC);
                break;
            case OpExp.MINUS:
                this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
                break;
            case OpExp.TIMES:
                this.emitCode(++this.address, Operations.MUL, AC, AC1, AC);
                break;
            case OpExp.OVER:
                this.emitCode(++this.address, Operations.DIV, AC, AC1, AC);
                break;
            case OpExp.LT:
                this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
                this.emitCode(++this.address, Operations.JLT, AC, 2, PC, "br if true");
                this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
                this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
                this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
                break;
            case OpExp.LEQ:
                this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
                this.emitCode(++this.address, Operations.JLE, AC, 2, PC, "br if true");
                this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
                this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
                this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
                break;
            case OpExp.GT:
                this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
                this.emitCode(++this.address, Operations.JGT, AC, 2, PC, "br if true");
                this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
                this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
                this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
                break;
            case OpExp.GTE:
                this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
                this.emitCode(++this.address, Operations.JGE, AC, 2, PC, "br if true");
                this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
                this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
                this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
                break;
            case OpExp.EEQ:
                this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
                this.emitCode(++this.address, Operations.JEQ, AC, 2, PC, "br if true");
                this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
                this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
                this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
                break;
            case OpExp.NEQ:
                this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
                this.emitCode(++this.address, Operations.JNE, AC, 2, PC, "br if true");
                this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
                this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
                this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
                break;
            default:
                System.out.println("Unrecognized operator at line " + (tree.pos + 1));
        }
    }

    private void genCode(CompoundExp tree){
        genCode(tree.vars);
        genCode(tree.exps);
    }

    private void genCode(ExpList tree){
        while (tree != null){
            genCode(tree.head);
            tree = tree.tail;
        }
    }

    private void genCode(VarDecList tree){
        if (tree.head == null){
            return;
        }
        while(tree != null) {
            genCode(tree.head);
            this.emitComment("allocating local var: " + tree.head.name + " " + symbolTable.getScope() + "  " +  tree.head.offset);
            tree = tree.tail;
        }
    }

    private void genCode(VarDec tree) {
        if(tree == null) {
            return;
        }
        if (tree instanceof ArrayDec){
            symbolTable.insert(tree.name, new NodeType(tree.name, tree, symbolTable.getScope()));
        }
        else{
            symbolTable.insert(tree.name, new NodeType(tree.name, tree, symbolTable.getScope()));
        }
    }


    private void end() {

        NodeType match = this.symbolTable.peek("main");
        FunctionDec mainFunc = (FunctionDec) match.def;
        this.emitCode(++this.address, Operations.ST, FP, symbolTable.getGlobalOffset(), FP, "push ofp");
        this.emitCode(++this.address, Operations.LDA, FP, symbolTable.getGlobalOffset(), FP, "push frame");
        this.emitCode(++this.address, Operations.LDA, AC, 1, PC, "load ac with ret ptr");
        this.emitCode(++this.address, Operations.LDA, PC, mainFunc.funaddr - this.address - 1, PC, "jump to " + mainFunc.func + " loc");
        this.emitCode(++this.address, Operations.LD, FP, 0, FP, "pop frame");
        asm.append("* End of execution:\n");
        asm.append(String.format("%1$3s", Integer.toString(++address)));
        asm.append(":   HALT  0,0,0 \t\n");
    }

    private void emitCode(int address, Operations oper, int r, int s, int t) {
        this.emitCode(address, oper, r, s, t, "");
    }

    private void emitCode(int address, Operations oper, int r, int s, int t, String comment) {
        String addr = String.format("%1$3s", Integer.toString(address));
        String op = String.format("%1$6s", oper.name());
        if(registerOnly.contains(oper)) {
            asm.append(addr + ": " + op + "  " + r + "," + s + "," + t + " \t" + comment + "\n");
        }
        else {
            asm.append(addr + ": " + op + "  " + r + "," + s + "(" + t + ")" + " \t"  +  comment + "\n");
        }
    }

    private void emitComment(String s) {
        asm.append("* " + s + "\n");
    }
}
