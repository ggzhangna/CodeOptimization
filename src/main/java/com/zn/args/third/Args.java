package com.zn.args.third;

import java.text.ParseException;
import java.util.*;

/**
 * 第三版：用抽象类封装相似的行为
 *
 * @author zhangna12
 * @date 2018-10-09
 */
public class Args {
    private String schema;
    private String[] args;
    private boolean valid = true;
    private Set<Character> unexceptedArguments = new TreeSet<Character>();

    private Map<Character,ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();

    private Set<Character> argsFound = new HashSet<Character>();
    private int currentArgument;
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";

    enum ErrorCode{
        OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT
    }

    private ErrorCode errorCode = ErrorCode.OK;

    public Args(String schema, String[] args) throws ParseException {
        this.schema = schema;
        this.args = args;
        valid = parse();
    }

    private boolean parse() throws ParseException{
        if(schema.length() == 0 && args.length == 0){
            return true;
        }
        parseSchema();
        try{
            parseArguments();
        }catch (ArgsException e){
        }
        return valid;
    }

    private boolean parseSchema() throws ParseException{
        for(String element : schema.split(",")){
            if(element.length() > 0){
                String trimedElemnt = element.trim();
                parseSchemaElement(trimedElemnt);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ParseException{
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if(isBooleanSchemaElement(elementTail)){
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        }else if(isStringSchemaElement(elementTail)){
            marshalers.put(elementId, new StringArgumentMarshaler());
        }else{
            throw new ParseException(String.format("Argument: %c has invalid format: %s.",elementId,elementTail),0);
        }
    }

    private void validateSchemaElementId(char elementId) throws ParseException{
        if(!Character.isLetter(elementId)){
            throw  new ParseException("Bad character:" + elementId + "in Args format: " + schema, 0);
        }
    }

    private boolean isBooleanSchemaElement(String elementTail){
        return elementTail.length() == 0;
    }

    private boolean isStringSchemaElement(String elementTail){
        return elementTail.equals("*");
    }

    private boolean parseArguments() throws ArgsException{
        for(currentArgument =0; currentArgument<args.length; currentArgument++){
            String arg = args[currentArgument];
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg) throws ArgsException{
        if(arg.startsWith("-")){
            parseElements(arg);
        }
    }

    private void parseElements(String arg) throws ArgsException{
        for(int i = 1; i < arg.length(); i++){
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) throws ArgsException{
        if(setArgument(argChar)){
            argsFound.add(argChar);
        }else{
            unexceptedArguments.add(argChar);
            errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
            valid = false;
        }
    }

    private boolean setArgument(char argChar) throws ArgsException{
        ArgumentMarshaler m = marshalers.get(argChar);
        try{
            if(m instanceof BooleanArgumentMarshaler){
                setBooleanArg(m);
            }else if(m instanceof StringArgumentMarshaler){
                setStringArg(m);
            }else{
                return false;
            }
        }catch (ArgsException e){
            valid = false;
            errorArgumentId = argChar;
            throw e;
        }
        return true;
    }

    private void setBooleanArg(ArgumentMarshaler m){
        try{
            m.set("true");
        }catch (ArgsException e){
        }
    }

    private void setStringArg(ArgumentMarshaler m) throws ArgsException{
        currentArgument++;
        try{
            m.set(args[currentArgument]);
        }catch(ArrayIndexOutOfBoundsException e){
            errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }
    }

    public int cardinality(){
        return argsFound.size();
    }

    public String usage(){
        if(schema.length() > 0){
            return "-[" + schema + "]";
        }else {
            return "";
        }
    }

    public String errorMessage() throws Exception{
        if(unexceptedArguments.size() > 0){
            return unexceptedArgumentMessage();
        }else {
            switch (errorCode){
                case MISSING_STRING:
                    return String.format("Could not find string parameter for -%c.",errorArgumentId);
                case OK:
                    throw new Exception("TILT: Should not get here.");
                case UNEXPECTED_ARGUMENT:
                    return unexceptedArgumentMessage();
            }
        }
        return "";
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
        Args.ArgumentMarshaler am = marshalers.get(arg);
        boolean b = false;
        try{
            b = am != null && (Boolean) am.get();
        }catch (ClassCastException e ){
            b = false;
        }
        return b;
    }

    public String getString(char arg){
        Args.ArgumentMarshaler am = marshalers.get(arg);
        try{
            return am == null? "":(String) am.get();
        }catch (ClassCastException e ){
            return "";
        }
    }

    public boolean isValid(){
        return valid;
    }

    public boolean has(char arg){
        return argsFound.contains(arg);
    }

    private class ArgsException extends Exception{}

    private abstract class ArgumentMarshaler {
        public abstract void set(String s) throws ArgsException;
        public abstract Object get();
    }

    private class BooleanArgumentMarshaler extends ArgumentMarshaler{
        private boolean booleanValue = false;
        @Override
        public void set(String s){
            booleanValue = true;
        }

        @Override
        public Object get(){
            return booleanValue;
        }
    }

    private class StringArgumentMarshaler extends ArgumentMarshaler{
        private String stringValue = "";
        @Override
        public void set(String s){
            stringValue = s;
        }

        @Override
        public Object get(){
            return stringValue;
        }
    }


}
