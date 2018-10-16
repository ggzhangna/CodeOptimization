package com.zn.args.fourth;

import java.util.Iterator;

public class BooleanArgumentMarshaler extends ArgumentMarshaler{
    private boolean booleanValue = false;
    @Override
    public void set(Iterator<String> currentArgument){
        booleanValue = true;
    }

    @Override
    public Object get(){
        return booleanValue;
    }
}
