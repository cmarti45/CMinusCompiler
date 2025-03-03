package absyn;

public class ArgsExp extends Exp {
    public ArgList args;

    public ArgsExp(int row, int col, ArgList args) {
        this.row = row;  // Position info from CUP
        this.col = col;
        this.args = args;
    }


    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
