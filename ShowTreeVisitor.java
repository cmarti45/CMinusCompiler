import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
  }

  public void visit( ExpList expList, int level ) {
    while( expList != null ) {
      expList.head.accept( this, level );
      expList = expList.tail;
    }
  }

  public void visit( DecList decList, int level ) {
    while( decList != null ) {
      decList.head.accept( this, level );
      decList = decList.tail;
    }
  }

  public void visit( ExpVarDecList dec, int level){
    VarDecList varDecList = dec.dec;
    while( varDecList != null ) {
      varDecList.head.accept( this, level );
      varDecList = varDecList.tail;
    }
  }

  public void visit( VarDecList varDecList, int level ) {
  }

  public void visit( ArgList argList, int level ) {
    while( argList != null ) {
      argList.head.accept( this, level );
      argList = argList.tail;
    }
  }

  public void visit( AssignExp exp, int level ) {
    indent( level );
    System.out.println( "AssignExp:" );
    level++;
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );
  }

  public void visit(NameTy ty, int level){
    indent(level);
    System.out.print(" type: ");
    if (ty.type == NameTy.VOID){
      System.out.println( "void" );
    }
    if (ty.type == NameTy.INT){
      System.out.println( "int" );
    }
    if (ty.type == NameTy.BOOL){
      System.out.println( "bool" );
    }
  }

  public void visit( SimpleDec dec, int level ) {
    indent( level );
    System.out.println( "Declare Var:" );
    level++;
    dec.type.accept(this, level);
    indent(level);
    System.out.println(" name: " + dec.name);
  }

  public void visit( ArrayDec dec, int level ) {
    indent( level );
    System.out.println( "Declare Var:" );
    level++;
    dec.type.accept(this, level);
    indent(level);
    System.out.println(" name: " + dec.name);
    indent(level);
    System.out.println(" size: " + dec.size);
  }

  public void visit(ArgsExp exp, int level ) {
    indent( level );
    System.out.println( "ArgsExp:" );
    level++;
    exp.args.accept(this, level);
  }

  public void visit(CallExp exp, int level ) {
    indent( level );
    System.out.println( "CallExp: " + exp.id);
    level++;
    exp.args.accept(this, level);
  }

  public void visit(NilExp exp, int level ) {
    indent( level );
    System.out.println("NilExp");
  }

  public void visit( IfExp exp, int level ) {
    indent( level );
    System.out.println( "IfExp:" );
    level++;
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
      exp.elsepart.accept( this, level );
  }

  public void visit( IntExp exp, int level ) {
    indent( level );
    System.out.println( "IntExp: " + exp.value );
  }

  public void visit(BoolExp exp, int level ) {
    indent( level );
    System.out.println( "TruthExp: " + exp.value );
  }

  public void visit( OpExp exp, int level ) {
    indent( level );
    System.out.print( "OpExp:" );
    switch( exp.op ) {
      case OpExp.PLUS:
        System.out.println( " + " );
        break;
      case OpExp.MINUS:
        System.out.println( " - " );
        break;
      case OpExp.TIMES:
        System.out.println( " * " );
        break;
      case OpExp.OVER:
        System.out.println( " / " );
        break;
      case OpExp.EQ:
        System.out.println( " = " );
        break;
      case OpExp.LT:
        System.out.println( " < " );
        break;
      case OpExp.GT:
        System.out.println( " > " );
        break;
      case OpExp.UMINUS:
        System.out.println( " - " );
        break;
      case OpExp.NEQ:
        System.out.println( " != " );
        break;
      case OpExp.LEQ:
        System.out.println( " <= " );
        break;
      case OpExp.GTE:
        System.out.println( " >= " );
        break;
      case OpExp.EEQ:
        System.out.println( " == " );
        break;
      case OpExp.NOT:
        System.out.println( " ~ " );
        break;
      case OpExp.AND:
        System.out.println( " && " );
        break;
      case OpExp.OR:
        System.out.println( " || " );
        break;
      default:
        System.out.println( "Unrecognized operator at line " + exp.pos);
    }
    level++;
    if (exp.left != null)
      exp.left.accept( this, level );
    if (exp.right != null)
      exp.right.accept( this, level );
  }

  public void visit( ReadExp exp, int level ) {
    indent( level );
    System.out.println( "ReadExp:" );
    exp.input.accept( this, ++level );
  }

  public void visit( SimpleVar var, int level ) {
    indent( level );
    System.out.println("name: " + var.name);
  }

  public void visit( IndexVar var, int level ) {
    indent( level );
    System.out.println("name: " + var.name);
    System.out.println("index: ");
    level++;
    var.index.accept( this, ++level );
  }

  public void visit( RepeatExp exp, int level ) {
    indent( level );
    System.out.println( "RepeatExp:" );
    level++;
    exp.exps.accept( this, level );
    exp.test.accept( this, level );
  }

  public void visit( VarExp exp, int level ) {
    indent( level );
    System.out.println( "VarExp:" );
    level++;
    exp.var.accept(this, level);
  }

  public void visit( WriteExp exp, int level ) {
    indent( level );
    System.out.println( "WriteExp:" );
    if (exp.output != null)
      exp.output.accept( this, ++level );
  }

  public void visit(CompoundExp exp, int level){
    VarDecList vars = exp.vars;
    ExpList exps = exp.exps;
    indent (level );
    System.out.println( "CompoundExp: ");
    level++;
    while( vars != null ) {
      vars.head.accept( this, level );
      vars = vars.tail;
    }
    while( exps != null ) {
      exps.head.accept( this, level );
      exps = exps.tail;
    }
  }

  public void visit(FunctionDec dec, int level){
    indent(level);
    System.out.println( "FunctionDec: ");
    level++;
    dec.result.accept(this, level);
    indent(level);
    System.out.println( "func: " + dec.func);
    //todo: print paramlist
    dec.body.accept(this, level);
  }

}
