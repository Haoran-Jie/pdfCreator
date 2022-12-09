package com.pdfcreator;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PdfCreator {
    public static PdfFont NORMAL;
    public static PdfFont BOLD;
    public static PdfFont ITALIC;
    public static PdfFont BOLD_ITALIC;
    public Integer nowIndent;

    private File orig;
    private File dest;
    public PdfCreator(String dest, String orig) throws IOException {
        this.dest = new File(dest);
        this.orig = new File(orig);
        NORMAL = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        BOLD = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
        ITALIC = PdfFontFactory.createFont(FontConstants.TIMES_ITALIC);
        BOLD_ITALIC = PdfFontFactory.createFont(FontConstants.TIMES_BOLDITALIC);
        nowIndent=0;
    }
    public static void main(String[] args) throws IOException {
        PdfCreator thisCreator = new PdfCreator("Output/result.pdf","Input/filein.txt");
        thisCreator.createPDF();
    }
    public void createPDF() throws IOException {
        ArrayList<InElement> InContent = new ArrayList<>();
        readin(InContent);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        render(InContent,document);
        document.close();
    }

    public void readin(ArrayList<InElement> InContent) throws FileNotFoundException {
        Scanner sca = new Scanner(this.orig);
        while(sca.hasNext()){
            String line = sca.nextLine();
            if(line.startsWith(".")){
                InContent.add(new InCommand(line.substring(1)));
            }
            else {
                InContent.add(new InText(line.endsWith(" ")? line : line+" "));
            }
        }
    }

    public void render(ArrayList<InElement> InContent, Document document){
        int count=0;
        while(count<InContent.size()){
            Paragraph para = new Paragraph();
            while(count<InContent.size()){
                int nextcount=findNextText(count,InContent);
                Text text = new Text(InContent.get(nextcount).getContent()).setFont(NORMAL);
                for(int i=count;i<nextcount;++i){
                    solveCommand(InContent.get(i),text,para);
                }
                para.add(text);
                count=nextcount+1;
                if(count>=InContent.size()){
                    break;
                }
                if(InContent.get(count).isCommand() && InContent.get(count).getContent().equals("paragraph")){
                    break;
                }
            }
            document.add(para);
        }
    }

    public int findNextText(int nowcount,ArrayList<InElement> elements){
        for(int i=nowcount;i< elements.size();++i){
            if(!elements.get(i).isCommand()){
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }
    public int findNextPara(int nowcount, ArrayList<InElement> elements){
        for(int i=nowcount;i<elements.size();++i){
            if(elements.get(i).isCommand() && elements.get(i).getContent().equals("paragraph")){
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public void solveCommand(InElement command, Text text,Paragraph paragraph){
        String content = command.getContent();
        if(content.equals("large")){
            text.setFontSize(16);
        }
        else if(content.equals("normal")){
            text.setFont(NORMAL);
        }
        else if(content.equals("italics")){
            text.setItalic();
        }
        else if(content.equals("bold")){
            text.setBold();
        }
        else if(content.equals("fill")){
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
        }
        else if(content.equals("nofill")){
            paragraph.setTextAlignment(TextAlignment.LEFT);
        }
        else if(content.equals("regular")){
            text.setFont(NORMAL);
        }
        else if(content.startsWith("indent")){
            Integer indent = Integer.parseInt(content.substring(8));
            if(content.charAt(7)=='+'){
                nowIndent+=indent;
            }
            else {
                nowIndent-=indent;
            }
            paragraph.setMarginLeft(nowIndent*10);
        }
    }
}