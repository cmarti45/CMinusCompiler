package absyn;

public class CompoundExp extends Exp {
    public VarDecList vars;
    public ExpList exps;
    public CompoundExp( int pos, VarDecList vars, ExpList exps ){
        this.pos = pos;
        this.vars = vars;
        this.exps = exps;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
