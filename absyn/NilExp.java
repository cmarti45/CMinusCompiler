package absyn;

public class NilExp extends Exp {
    public String value;

    public NilExp( int pos ) {
        this.pos = pos;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
