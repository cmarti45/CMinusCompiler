package absyn;

public class VarExp extends Exp {
  public String name;
  public final static int VOID   = 0;
  public final static int INT  = 1;
  public final static int BOOL  = 2;
  public int type;
  public String size;
  public Boolean isArray;

  public VarExp( int row, int col, int type, String name ) {
    this.row = row;
    this.col = col;
    this.type = type;
    this.name = name;
    this.isArray = false;
  }

  public VarExp( int row, int col, Object type, String name ) {
    switch (type.toString()) {
      case "void" -> this.type = VOID;
      case "int" -> this.type = INT;
      case "bool" -> this.type = BOOL;
    }
    this.row = row;
    this.col = col;
    this.name = name;
    this.isArray = false;
  }

  public VarExp( int row, int col, Object type, String name, String size ) {
    switch (type.toString()) {
      case "void" -> this.type = VOID;
      case "int" -> this.type = INT;
      case "bool" -> this.type = BOOL;
    }
    this.row = row;
    this.col = col;
    this.name = name;
    this.size = size;
    this.isArray = true;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
