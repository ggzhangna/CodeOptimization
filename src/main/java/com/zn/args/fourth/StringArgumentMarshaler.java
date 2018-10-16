package com.zn.args.fourth;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringArgumentMarshaler extends ArgumentMarshaler{
    private String stringValue = "";
    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException{
        try{
            stringValue = currentArgument.next();
        }catch (NoSuchElementException e){
            throw new ArgsException(ErrorCode.MISSING_STRING);
        }
    }

    @Override
    public Object get(){
        return stringValue;
    }

}
