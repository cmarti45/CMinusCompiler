package absyn;

public class SimpleVar extends Var{
    public SimpleVar( int pos, String name){
        this.pos = pos;
        this.name = name;
    }

    public static VarExp var( int pos, String name){
        return new VarExp(pos, new SimpleVar( pos, name));
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
