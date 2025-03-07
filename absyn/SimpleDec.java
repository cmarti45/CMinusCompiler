package absyn;

public class SimpleDec extends VarDec{
    public SimpleDec(int row, int col, NameTy typ, String name){
        this.row = row;
        this.col = col;
        this.type = typ;
        this.name = name;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
