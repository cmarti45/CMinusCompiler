package absyn;

public class Symbol extends java_cup.runtime.Symbol {
    public int pos;

    public Symbol(int sym_num){
        super(sym_num);
    }

    public Symbol(int id, Integer pos){
        super(id);
        this.pos = pos;
    }

    public Symbol(int id, int pos, Object value) {
        super(id, value);
        this.pos = pos;
    }

    public Symbol(int id, Object value){
        super(id, value);
    }

}