package com.pdfcreator;

public class InCommand extends InElement{

    public InCommand(String content){
        super(content);
    }

    @Override
    public boolean isCommand() {
        return true;
    }
}
