package absyn;

public class SimpleDec extends VarDec{
    public SimpleDec( int pos, NameTy typ, String name){
        this.pos = pos;
        this.type = typ;
        this.name = name;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
