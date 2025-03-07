package absyn;

public class IndexVar extends Var{
    public Exp index;

    public IndexVar( int pos, String name, Exp index){
        this.pos = pos;
        this.name = name;
        this.index = index;
    }

    public static VarExp var( int pos, String name, Exp index){
        return new VarExp(pos, new IndexVar(pos, name, index));
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
