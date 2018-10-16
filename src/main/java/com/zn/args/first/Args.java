package com.zn.args.first;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 解析命令行参数
 *
 * 第一版：只支持boolean参数（优秀，精炼、简单、易于理解）
 * schema 格式化字符串
 * args 命令行参数
 *
 * @author zhangna12
 * @date 2018-10-09
 */
public class Args {
    private String schema;
    private String[] args;
    private boolean valid;
    private Set<Character> unexceptedArguments = new TreeSet<Character>();
    private Map<Character,Boolean> booleanArgs = new HashMap<Character, Boolean>();
    private int numberOfArguments = 0;

    public Args(String schema, String[] args){
        this.schema = schema;
        this.args = args;
        valid = parse();
    }

    private boolean parse(){
        if(schema.length() == 0 && args.length == 0){
            return true;
        }
        parseSchema();
        parseArguments();
        return unexceptedArguments.size() == 0;
    }

    private boolean parseSchema() {
        for(String element : schema.split(",")){
            parseSchemaElement(element);
        }
        return true;
    }

    private void parseSchemaElement(String element){
        if(element.length() == 1){
            parseBooleanSchemaElement(element);
        }
    }

    private void parseBooleanSchemaElement(String element){
        char c = element.charAt(0);
        if(Character.isLetter(c)){
            booleanArgs.put(c,false);
        }
    }

    private boolean parseArguments(){
        for(String arg : args){
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg){
        if(arg.startsWith("-")){
            parseElements(arg);
        }
    }

    private void parseElements(String arg){
        for(int i = 1; i < arg.length(); i++){
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar){
        if(isBoolean(argChar)){
            numberOfArguments++;
            setBooleanArg(argChar, true);
        }else{
            unexceptedArguments.add(argChar);
        }
    }

    private void setBooleanArg(char argChar, boolean value){
        booleanArgs.put(argChar, value);
    }

    private boolean isBoolean(char argChar){
        return booleanArgs.containsKey(argChar);
    }

    public int cardinality(){
        return numberOfArguments;
    }

    public String usage(){
        if(schema.length() > 0){
            return "-[" + schema + "]";
        }else {
            return "";
        }
    }

    public String errorMessage(){
        if(unexceptedArguments.size() > 0){
            return unexceptedArgumentMessage();
        }else {
            return "";
        }
    }

    private String unexceptedArgumentMessage(){
        StringBuffer message = new StringBuffer("Argument(s) -");
        for(char c : unexceptedArguments){
            message.append(c);
        }
        message.append(" unexcepted.");
        return message.toString();
    }

    public boolean getBoolean(char arg){
        return booleanArgs.get(arg);
    }

    public boolean isValid(){
        return valid;
    }
}
