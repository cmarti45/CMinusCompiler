package absyn;

public class ArrayDec extends VarDec{
    public int size;
    public ArrayDec( int pos, NameTy typ, String name, int size){
        this.pos = pos;
        this.type = typ;
        this.name = name;
        this.size = size;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
