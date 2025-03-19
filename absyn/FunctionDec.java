package absyn;

import java.util.ArrayList;

public class FunctionDec extends Dec {
    public String func;
    public VarDecList params;
    public Exp body;
    public ArrayList<String> paramList;
    public FunctionDec( int pos, NameTy result, String func, VarDecList params, Exp body){
        this.pos = pos;
        this.type = result;
        this.func = func;
        this.body = body;
        this.params = params;
        paramList = new ArrayList<>();
        VarDecList p = this.params;
        while (p!=null&&p.head!=null) {
            if (p.head instanceof ArrayDec) {
                paramList.add(p.head.type.toString().toLowerCase() + "*");
            } else {
                paramList.add(p.head.type.toString().toLowerCase());
            }
            p = p.tail;
        }
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

    @Override
    public String toString(){
        if (paramList.size() == 1){
            return func + ": (" + paramList.getFirst() + ") -> " + type.toString().toLowerCase();
        }
            else if (paramList.size() >1 ){
            return func + ": (" + String.join(",", paramList) + ") -> " + type.toString().toLowerCase();
        }
        else
            return func + ": (void) -> " + type.toString().toLowerCase();
    }
}
