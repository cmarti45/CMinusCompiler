package absyn;

public class VarExp extends Exp {
  public String name;
  public Var var;

  public VarExp( int pos, Var var ) {
    this.pos = pos;
    this.var = var;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
