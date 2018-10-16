package com.zn.args.fourth;

public class ArgsException extends Exception {
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";

    private ErrorCode errorCode = ErrorCode.OK;

    public ArgsException(){}

    public ArgsException(String message){
        super(message);
    }

    public ArgsException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ArgsException(ErrorCode errorCode, char errorArgumentId, String errorParameter){
        this.errorCode = errorCode;
        this.errorArgumentId = errorArgumentId;
        this.errorParameter = errorParameter;
    }

    public void setErrorArgumentId(char errorArgumentId){
        this.errorArgumentId = errorArgumentId;
    }

    public String errorMessage() throws Exception{
        switch (errorCode){
            case MISSING_STRING:
                return String.format("Could not find string parameter for -%c.",errorArgumentId);
            case OK:
                throw new Exception("TILT: Should not get here.");
            case UNEXPECTED_ARGUMENT:
                return String.format("Argument -%c unexcepted.",errorArgumentId);
            case INVALID_FORMAT:
                return String.format("Argument: %c has invalid format: %s.",errorArgumentId,errorParameter);
        }
        return "";
    }
}
