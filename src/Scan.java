/**
 * Interpreter project
 * Scanner class?
 * @author: Jacob Verdesi jxv3386@rit.edu()
 */

import java.io.*;
import java.util.*;

public class Scan {
    static private final String DELIMITER = "((?<=%1$s)|(?=%1$s))";
    /**
     * Takes in file name reads line by line
     * @param filename
     * @return list of lines
     */
    private static List<String> readFile(String filename){
        List<String> lines = new ArrayList<String>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null){
                lines.add(line);
            }
            reader.close();
            return lines;
        }
        catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.\n", filename);
            e.printStackTrace();
            return null;
        }
    }
    private static void error(int line , int column,char curr){
        System.err.format("Invalid syntax line: %s column: %s character: %s\n", line, column,curr);
        System.exit(0);
    }

    private static List<String> dfaTokenizer(List<String> lines){
        int lineNum=0;
        DFAstate state=new DFAstate();
        for(String line: lines){
            char[] chars=line.toCharArray();
            for (int i=0; i < chars.length;i++){
                if(chars[i]=='/' && chars[i+1]=='/') break; // if find comment end line
                if(state.getState()==0 && (chars[i]==' '|| chars[i]=='\n' || chars[i]=='\t')) continue;

                state.setCurr(chars[i]);
                if(i<chars.length-1) state.setNext(chars[i + 1]);
                char curr=state.getCurr();
                char next=state.getNext();
                //System.out.println("i: "+i+" current: "+state.getCurr()+" next: "+state.getNext()+ " state: "+state.getState());
                switch (state.getState()){
                    case 0:
                        if (Character.isLetter(curr)){
                            state.setState(1);
                            if(!Character.isLetter(next) && !Character.isDigit(next)) state.reset();
                        }
                        else if (curr == '.') state.setState(2);
                        else if (Character.isDigit(curr)){
                            state.setState(3);
                            if(next!='.' && !Character.isDigit(next)) state.reset();
                        }
                        else if ("=();+-*^,".contains(""+curr)) state.reset();
                        else if (curr == '\"') state.setState(14);
                        else error(lineNum,i,curr);

                        break;
                    case 1:
                        if (Character.isLetter(curr) || Character.isDigit(curr)){
                            if(!Character.isLetter(next) && !Character.isDigit(next)) state.reset();
                            continue;
                        }
                        else error(lineNum,i,curr);

                    case 2:
                        if(Character.isDigit(curr)) state.setState(4);
                        else error(lineNum,i,curr);
                        break;
                    case 3:
                        if(Character.isDigit(curr)){
                            if(next!='.' && !Character.isDigit(next)) state.reset();
                            continue;
                        }
                        else if (curr=='.'){
                            state.setState(4);
                            if(!Character.isDigit(next))state.reset();
                        }
                        else error(lineNum,i,curr);
                        break;
                    case 4:
                        if(Character.isDigit(curr)){
                            if(!Character.isDigit(next))state.reset();
                            continue;
                        }
                        else error(lineNum,i,curr);
                        break;
                    case 14:
                        if(Character.isDigit(curr) || Character.isLetter(curr) || curr== ' ') continue;
                        else if (curr=='\"') state.reset();
                        else error(lineNum,i,curr);
                        break;
                }
            }
            lineNum++;
        }
        return state.getTokens();
    }

    /**
     * A pretty print to see the tokens
     * @param tokens
     */
    private static void printList(List<String> tokens){
        for (String token:tokens) {
            if (token.equals(";")) {
                System.out.println("["+token+"]");
            } else {
                System.out.print("["+token+"] ");
            }
        }
    }

    public static void main(String[] args){
        List<String> a = readFile("src/program1.j");
        List<String> tokens=dfaTokenizer(a);
        printList(tokens);
    }
}
