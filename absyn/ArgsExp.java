package absyn;

public class ArgsExp extends Exp {
    public ArgList args;

    public ArgsExp( int pos, ArgList args ){
        this.pos = pos;  // Position info from CUP
        this.args = args;
    }


    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
