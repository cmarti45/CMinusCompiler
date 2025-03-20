import absyn.Absyn;
import absyn.AbsynVisitor;
import absyn.ArrayDec;
import absyn.DecList;
import org.junit.jupiter.api.*;
import symb.SymbolTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckpointTwoTests {

//    @BeforeEach
//    void setUp() {
//
//    }
//
//    @AfterEach
//    void tearDown() {
//    }

    @Test
    void test(){
        File fileDirectory = new File("SampleParser/test_files");
        System.out.println(Arrays.toString(fileDirectory.listFiles()));
        File[] files = fileDirectory.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            try {
                Lexer lexer = new Lexer(new FileReader(file));
                parser p = new parser(lexer);
                Absyn result = (Absyn) (p.parse().value);
                new SymbolTable().showTable((DecList) result);
            } catch (Exception e) {
                /* do cleanup here -- possibly rethrow e */
                e.printStackTrace();
            }
        }
    }
}