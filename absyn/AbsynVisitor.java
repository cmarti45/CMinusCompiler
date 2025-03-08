package absyn;

public interface AbsynVisitor {

  public void visit( ExpList exp, int level );

  public void visit( ArgList exp, int level );

  public void visit( AssignExp exp, int level );

  public void visit(ArgsExp exp, int level );

  public void visit(CallExp exp, int level );

  public void visit(NilExp exp, int level );

  public void visit( IfExp exp, int level );

  public void visit( IntExp exp, int level );

  public void visit(BoolExp exp, int level );

  public void visit( OpExp exp, int level );

  public void visit( ReadExp exp, int level );

  public void visit( RepeatExp exp, int level );

  public void visit( VarExp exp, int level );

  public void visit( WriteExp exp, int level );

  public void visit( SimpleVar var, int level );

  public void visit( IndexVar var, int level );

  public void visit ( SimpleDec dec, int level);

  public void visit ( ArrayDec dec, int level);

  public void visit ( DecList dec, int level);

  public void visit ( VarDecList dec, int level);

  public void visit ( NameTy ty, int level);

  public void visit ( CompoundExp dec, int level);

  public void visit ( FunctionDec dec, int level);

  public void visit ( WhileExp exp, int level);

  public void visit ( ReturnExp exp, int level);

}
