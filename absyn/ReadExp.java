package absyn;

public class ReadExp extends Exp {
  public VarExp input;

  public ReadExp( int pos, VarExp input ) {
    this.pos = pos;
    this.input = input;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
