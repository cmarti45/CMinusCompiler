package absyn;

public class RepeatExp extends Exp {
  public ExpList exps;
  public Exp test;

  public RepeatExp( ExpList exps, Exp test ) {
    this.exps = exps;
    this.test = test;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
