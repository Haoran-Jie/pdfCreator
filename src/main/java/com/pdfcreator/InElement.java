package com.pdfcreator;

public abstract class InElement {
    private String content;

    public InElement(String content){
        this.content=content;
    }

    public String getContent() {
        return content;
    }

    abstract boolean isCommand();
}
