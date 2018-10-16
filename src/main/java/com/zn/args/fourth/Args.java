package com.zn.args.fourth;

import java.util.*;

/**
 * 第四版：
 * 去掉setArgument里的类型转换
 * 把currentArgument异常相关的都封装到异常类里去
 *
 * 总结：整体感觉意义不大，因为日常工作中，就已经养成习惯，一开始写就不会把异常写在一个类中，也不会写内部类。
 * 可取的地方就是，当解析的数据类型增加的时候，采用了抽象类的形式实现，接口也可以，感觉一般用接口更多，好扩展。
 * 感觉能学到就是最后把args[]数组去掉，使用List代替，把Iterator当做参数传递让我很吃惊，原来还可以这样，世界之大，真是神奇。
 *
 * 值得思考的地方是，去掉setArgument里的类型转换，怎么就用List实现了？
 *
 * @author zhangna12
 * @date 2018-10-10
 */
public class Args {
    private String schema;
    private boolean valid = true;

    private Map<Character,ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();
    private Set<Character> argsFound = new HashSet<Character>();
    private Iterator<String> currentArgument;
    private List<String> argsList;

    public Args(String schema, String[] args) throws ArgsException {
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
        validateSchemaElementId(elementId);
        if(elementTail.length() == 0){
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        }else if(elementTail.equals("*")){
            marshalers.put(elementId, new StringArgumentMarshaler());
        }else{
            throw new ArgsException(ErrorCode.INVALID_FORMAT,elementId,elementTail);
        }
    }

    private void validateSchemaElementId(char elementId) throws ArgsException{
        if(!Character.isLetter(elementId)){
            throw  new ArgsException(ErrorCode.INVALID_ARGUMENT_NAME,elementId,null);
        }
    }

    private void parseArguments() throws ArgsException{
        for(currentArgument =argsList.iterator(); currentArgument.hasNext();){
            String arg = currentArgument.next();
            parseArgument(arg);
        }
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
            throw new ArgsException(ErrorCode.UNEXPECTED_ARGUMENT,argChar,null);
        }
    }

    private boolean setArgument(char argChar) throws ArgsException{
        ArgumentMarshaler m = marshalers.get(argChar);
        if(m == null){
            return false;
        }
        try{
            m.set(currentArgument);
            return true;
        }catch (ArgsException e){
            e.setErrorArgumentId(argChar);
            throw e;
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
