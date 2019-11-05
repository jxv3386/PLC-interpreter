
import com.sun.source.tree.Tree;

import java.util.List;
import java.util.Map;

public class Jott {
    public static void testProgs(List<Integer> progs, List<String> rules, List<List<String>> matrix){
        for(int i: progs){
            List<String> prog = FileReader.readFile("tests/prog"+i+".j");
            System.out.println("\nprog"+i+".j tree:");
            System.out.println("================");
            List<List<Map.Entry<String, TERMINAL>>> tokens=Tokenizer.dfaTokenizer(prog);
            TreeNode tree=Parser.parseTree(rules,matrix,tokens);
            TreeNode.printTree(tree);

        }
    }

    public static void main(String[] args){
        List<String> prog = FileReader.readFile(args[0]);
        String s="";
        List<String> table = FileReader.readFile("LALR(1) parse table");
        List<String> rules = FileReader.readFile("Grammar2");
        List<List<String>> matrix = Parser.parseTable(table);
        List<Integer> progs=List.of(0,1,2,4,5,6,10,11,12,13,14,15,16,17);
        testProgs(progs,rules,matrix);
        //List<List<Map.Entry<String, TERMINAL>>> tokens=Tokenizer.dfaTokenizer(prog);

        //System.out.println(tokens);
//        Tokenizer.printTokens(tokens);
        //Tokenizer.printTerminals(tokens);
//       //Parser.printTable(matrix);
//
       // List<String> prog = FileReader.readFile("tests/prog8.j");
//        System.out.println("\nProgram Tree: \n");
//        TreeNode tree=Parser.parseTree(rules,matrix,tokens);
//        TreeNode.printTree(tree);
//
//        //TreeNode.printTree(tree);
//        System.out.println("\nOutput: \n");
//
//        TreeInterpreter.runTree(tree);
    }
}
