import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

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

  public void visit( VarDecList varDecList, int level ) {
    if (varDecList.head == null){
      indent(level);
      System.out.println("VOID");
      return;
    }
    while( varDecList != null ) {
      varDecList.head.accept( this, level );
      varDecList = varDecList.tail;
    }
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
    System.out.println(ty.toString());
  }

  public void visit( SimpleDec dec, int level ) {
    indent( level );
    System.out.println( "Declare Var: " + dec.type.toString() + " " + dec.name);
  }

  public void visit( ArrayDec dec, int level ) {
    indent( level );
    System.out.println( "Declare Var: " + dec.type.toString() + " " + dec.name + "[] ");
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

  public void visit( SimpleVar var, int level ) {
    System.out.println(var.name);
  }

  public void visit( IndexVar var, int level ) {
    System.out.println(var.name);
    level++;
    var.index.accept( this, level );
  }

  public void visit( VarExp exp, int level ) {
    indent( level );
    System.out.print( "VarExp: " );
    exp.var.accept(this, level);
  }

  public void visit(CompoundExp exp, int level){
    VarDecList vars = exp.vars;
    ExpList exps = exp.exps;
    indent (level );
    System.out.println( "CompoundExp: ");
    level++;
    while( vars != null && vars.head != null) {
      vars.head.accept( this, level );
      vars = vars.tail;
    }
    while( exps != null&&exps.head != null) {
      exps.head.accept( this, level );
      exps = exps.tail;
    }
  }

  public void visit(FunctionDec dec, int level){
    if (dec.body == null){
      indent(level);
      System.out.println( "FunctionPro: " + dec.result + " " + dec.func);
      indent(level);
      dec.params.accept(this, level);
      return;
    }
    indent(level);
    System.out.println( "FunctionDec: " + dec.result + " " + dec.func);
    level++;
    indent(level);
    System.out.println("Params:");
    level++;
    dec.params.accept(this, level);
    level--;
    dec.body.accept(this, level);
  }

  public void visit(ReturnExp exp, int level){
    indent(level);
    System.out.println( "ReturnExp: ");
    level++;
    exp.exp.accept(this, level);
  }

  public void visit(WhileExp exp, int level){
    indent(level);
    System.out.println( "WhileExp: ");
    level++;
    exp.test.accept(this,level);
    exp.body.accept(this,level);


  }

}
