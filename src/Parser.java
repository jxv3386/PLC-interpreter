import java.util.*;

public class Parser {
    private static List<Map.Entry<String,Object>> ids= new ArrayList<>();

    public static void printTable (List<List<String>> matrix){
        for(List<String> line: matrix){
            for(Object state:line){
                System.out.printf("[%10s] ",  state);
            }
            System.out.println();
        }
    }

    public static List<List<String>> parseTable(List<String> lines){
        List<List<String>> matrix = new ArrayList<>();
        List<String> prim= Arrays.asList(lines.get(0).strip().split("\t"));
        matrix.add(prim);
        for(int i=1;i<lines.size();i++){
            String line=lines.get(i).strip();
            List<String> states=new ArrayList<String>(Collections.nCopies(prim.size(), ""));
            List<String>temp=Arrays.asList(line.split("\t"));
            for(int j=0;j<temp.size();j++){
                states.set(j,temp.get(j));
            }
            matrix.add(states);
        }
        return matrix;
    }
    private static int stateIndex(Stack stack) {
        return (int)stack.get(2 * ((stack.size() - 1) >> 1))+1;
    }
    public static char actionElement(String action){
        return (action.charAt(0)=='r'||action.charAt(0)=='s') ? action.charAt(0) : ' ';
    }
    private static int actionIndex(String action){
        if(actionElement(action)==' '){
            return Integer.parseInt(action);
        }
        else if(actionElement(action)=='s'){
            return Integer.parseInt(action.substring(1));
        }
        else return -1;
    }
    private static TERMINAL handleID(TERMINAL currToken,String idName,String identifier){
        boolean found = false;
        for (Map.Entry<String,Object> id:ids) {
            if (id.getKey().equals(idName)) {
                if (id.getValue().equals("Integer")){
                    currToken = TERMINAL.I_ID;
                } else if (id.getValue().equals("Double")) {
                    currToken = TERMINAL.D_ID;
                } else if (id.getValue().equals("String")) {
                    currToken = TERMINAL.S_ID;
                }
                found=true;
            }
        }
        if (!found){
            if(identifier.equals("Integer") || identifier.equals("Double") || identifier.equals("String")) {
                Map.Entry<String, Object> id = new AbstractMap.SimpleEntry<>(idName, identifier);
                ids.add(id);
            }
            else{
                return null;
            }
        }
        return currToken;
    }
    public static TreeNode parseTree(List<String> rules,List<List<String>> parseTable, List<List<Map.Entry<String, TERMINAL>>> tokens) throws SyntaxError {
        List<Map.Entry<String, TERMINAL>> tokenList=new ArrayList<>();
        for (List<Map.Entry<String,TERMINAL>> t:tokens) {
            tokenList.addAll(t);
            tokenList.add(new AbstractMap.SimpleEntry<>("NewLine",TERMINAL.NEWLINE));
        }
        Map.Entry<String, TERMINAL> end=new AbstractMap.SimpleEntry<>("EOF",TERMINAL.$);

        tokenList.add(end);

        Stack stack=new Stack<>();
        stack.push(0);
        int line=1;
        int tokenIndex=0;
        TERMINAL currToken= tokenList.get(tokenIndex).getValue();
        while (currToken==TERMINAL.NEWLINE){
            tokenIndex++;
            line++;
            currToken= tokenList.get(tokenIndex).getValue();
        }
        if (currToken==TERMINAL.ID){
            String idName=tokenList.get(tokenIndex).getKey();
            String identifier = stack.get(stack.size() - 2).toString();
            TERMINAL temp2=currToken;
            currToken=handleID(currToken,idName,identifier);
            if(currToken==null) {
                ids.clear();
                throw new SyntaxError("Undefined variable: "+temp2,line);
            }
        }
        List<String> state=parseTable.get(stateIndex(stack));
        String action=state.get(parseTable.get(0).indexOf(currToken.toString()));
        char actionType=actionElement(action);
        int actionIndex = actionIndex(action);
        //System.out.printf("[%2d] %-60s %-70s [%3s]\n",line,stack,tokenList.subList(tokenIndex,tokenList.size()),action);

        while (!action.equals("acc") ){

            if(actionType=='s'){
                stack.push(tokenList.get(tokenIndex).getKey());
                stack.push(actionIndex);
                tokenIndex++;
            }
            else if (actionType=='r'){
                String[] rightSide=rules.get(Integer.parseInt(action.substring(1))).substring(rules.get(Integer.parseInt(action.substring(1))).indexOf("->")+3).split(" ");
                List removed=new ArrayList();
                for (String s : rightSide) {
                    if (!s.equals("\'\'")) {
                        stack.pop();
                        removed.add(stack.pop());
                    }
                }
                String rule=rules.get(Integer.parseInt(action.substring(1))).split(" ")[0];

                TreeNode node = new TreeNode<>(rule,line);

                for(int i=0;i<removed.size();i++){
                    if(removed.get(i) instanceof TreeNode){
                        TreeNode child=(TreeNode)removed.get(i);
                        node.addChild(child,child.lineNum);
                    }
                    else {
                        node.addChild(removed.get(i).toString(),line);
                    }
                }
                stack.push(node);
            }
            else {
                stack.push(actionIndex);
            }

            state=parseTable.get(stateIndex(stack));
            currToken= tokenList.get(tokenIndex).getValue();
            while (currToken==TERMINAL.NEWLINE){
                tokenIndex++;
                line++;
                currToken= tokenList.get(tokenIndex).getValue();
            }
            if (currToken==TERMINAL.ID) {
                String idName = tokenList.get(tokenIndex).getKey();
                String identifier = stack.get(stack.size() - 2).toString();
                TERMINAL temp2 = currToken;
                currToken = handleID(currToken,idName, identifier);
                if (currToken == null) {
                    ids.clear();
                    throw new SyntaxError("Undefined variable: "+temp2,line);
                }
            }

            if(actionIndex==-1) action=state.get(parseTable.get(0).indexOf(stack.peek().toString()));
            else action=state.get(parseTable.get(0).indexOf(currToken.toString()));
            //System.out.printf("[%2d] %-60s %-70s [%3s]\n",line,stack,tokenList.subList(tokenIndex,tokenList.size()),action);

            if(action.equals("")) {
                TERMINAL token=tokenList.get(tokenIndex).getValue();
                ids.clear();
                throw new SyntaxError("Expected something else got "+currToken,line);
            }
            if (!action.equals("acc")) {
                actionType = actionElement(action);
                actionIndex = actionIndex(action);
            }
            //System.out.printf("[%2d] %-60s %-70s [%3s]\n",line,stack,tokenList.subList(tokenIndex,tokenList.size()),action);
        }
        stack.pop();
        Object tree=stack.pop();
        ids.clear();
        TreeNode.reverseTree((TreeNode) tree);
        return  (TreeNode) tree;
    }
}
