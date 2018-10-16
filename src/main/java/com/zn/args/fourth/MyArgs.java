package com.zn.args.fourth;

import java.util.*;

/**
 * 附加版：
 * 简洁的方式看不懂代码，又堆到一起看，发现这种看的懂，但是真的丑
 * 但是，明白了为啥用ArgsList代替了args[]
 * 用数组，还需要一个计数的变量，list直接用next()就可以了，简洁
 *
 * 至于把Iterator当做参数传递，完全是把大方法拆成小方法时的产物
 *
 * 总体来说，整个优化就是一个抽象，一个大方法分成多个小方法（异常已经get了就不算收获了）
 * 还有最重要的单元测试，在测试类里写了一个
 *
 * @author zhangna12
 * @date 2018-10-10
 */
public class MyArgs {
    private String schema;
    private boolean valid = true;

    private Map<Character,ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();
    private Set<Character> argsFound = new HashSet<Character>();
    private Iterator<String> currentArgument;
    private List<String> argsList;

    public MyArgs(String schema, String[] args) throws ArgsException {
        this.schema = schema;
        argsList = Arrays.asList(args);
        parse();
    }

    private void parse() throws ArgsException{
        parseSchema();
        parseArguments();
    }

    private boolean parseSchema() throws ArgsException{
        for(String element : schema.split(",")){
            if(element.length() > 0){
                parseSchemaElement(element.trim());
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ArgsException{
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);

        if(!Character.isLetter(elementId)){
            throw  new ArgsException(ErrorCode.INVALID_ARGUMENT_NAME,elementId,null);
        }

        if(elementTail.length() == 0){
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        }else if(elementTail.equals("*")){
            marshalers.put(elementId, new StringArgumentMarshaler());
        }else{
            throw new ArgsException(ErrorCode.INVALID_FORMAT,elementId,elementTail);
        }
    }

    private void parseArguments() throws ArgsException{
        for(currentArgument =argsList.iterator(); currentArgument.hasNext();){
            String arg = currentArgument.next();
            if(arg.startsWith("-")){
                for(int i = 1; i < arg.length(); i++){
                    ArgumentMarshaler m = marshalers.get(arg.charAt(i));
                    if(m == null){
                        throw new ArgsException(ErrorCode.UNEXPECTED_ARGUMENT,arg.charAt(i),null);
                    }
                    try{
                        m.set(currentArgument);
                    }catch (ArgsException e){
                        e.setErrorArgumentId(arg.charAt(i));
                        throw e;
                    }
                    argsFound.add(arg.charAt(i));
                }
            }
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

    public boolean getBoolean(char arg){
        ArgumentMarshaler am = marshalers.get(arg);
        boolean b = false;
        try{
            b = am != null && (Boolean) am.get();
        }catch (ClassCastException e ){
            b = false;
        }
        return b;
    }

    public String getString(char arg){
        ArgumentMarshaler am = marshalers.get(arg);
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

}
