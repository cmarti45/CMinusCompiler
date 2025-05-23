package absyn;

public class OpExp extends Exp {
  public final static int PLUS   = 0;
  public final static int MINUS  = 1;
  public final static int TIMES  = 2;
  public final static int OVER   = 3;
  public final static int EQ     = 4;
  public final static int LT     = 5;
  public final static int GT     = 6;
  public final static int UMINUS = 7;
  public final static int GTE = 8;
  public final static int LEQ = 9;
  public final static int EEQ = 10;
  public final static int NEQ = 11;
  public final static int NOT = 12;
  public final static int AND = 13;
  public final static int OR = 14;

  public Exp left;
  public int op;
  public Exp right;

  public OpExp( int pos, Exp left, int op, Exp right ) {
    this.pos = pos;
    this.left = left;
    this.op = op;
    this.right = right;
  }

  public int type(){
    if (this.op >= PLUS && this.op <= OVER){
      return NameTy.INT;
    } else if (this.op >= EQ && this.op <= OR){
      return NameTy.BOOL;
    } else {
      return NameTy.ERROR;
    }
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
