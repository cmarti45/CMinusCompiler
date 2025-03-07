package absyn;

public class ArrayDec extends VarDec{
    public int size;
    public ArrayDec(int row, int col, NameTy typ, String name, int size){
        this.row = row;
        this.col = col;
        this.type = typ;
        this.name = name;
        this.size = size;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
