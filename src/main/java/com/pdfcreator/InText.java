package com.pdfcreator;

public class InText extends InElement{
    public InText(String content) {
        super(content);
    }

    @Override
    public boolean isCommand() {
        return false;
    }
}
