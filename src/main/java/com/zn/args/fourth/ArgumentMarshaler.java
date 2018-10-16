package com.zn.args.fourth;

import java.util.Iterator;

public abstract class ArgumentMarshaler {
    public abstract void set(Iterator<String> currentArgument) throws ArgsException;
    public abstract Object get();
}
