package com.pdfcreator;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
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
    public final float defaultIndent = 10;
    public final int defaultFontSize = 12;
    private final File orig;
    private final File dest;
    public Integer nowIndent;
    public PdfFont nowFont;
    public Integer nowFontSize;

    /**
     * @param dest Path of the output file
     * @param orig Path of the input file
     * @throws IOException
     */
    public PdfCreator(String dest, String orig) throws IOException {
        this.dest = new File(dest);
        this.orig = new File(orig);
        NORMAL = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        BOLD = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
        ITALIC = PdfFontFactory.createFont(FontConstants.TIMES_ITALIC);
        BOLD_ITALIC = PdfFontFactory.createFont(FontConstants.TIMES_BOLDITALIC);
        nowIndent = 0;
        nowFont = NORMAL;
        nowFontSize = defaultFontSize;
    }

    public static void main(String[] args) throws IOException {
        PdfCreator thisCreator = new PdfCreator("Output/result.pdf", "Input/filein.txt");
        thisCreator.createPDF();
    }

    /**
     * @throws IOException
     */
    public void createPDF() throws IOException {
        ArrayList<InElement> InContent = new ArrayList<>();
        readin(InContent);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        render(InContent, document);
        document.close();
    }

    /**
     * reads each line of the input file using a Scanner object. If the line starts with a ".", it is treated as a command and added to the list as an InCommand object. Otherwise, the line is treated as text and added to the list as an InText object.
     *
     * @param InContent The arraylist that stores all the input elements
     * @throws FileNotFoundException
     */
    public void readin(ArrayList<InElement> InContent) {
        try {
            Scanner sca = new Scanner(this.orig);
            while (sca.hasNext()) {
                String line = sca.nextLine();
                if (line.startsWith(".")) {
                    InContent.add(new InCommand(line.substring(1)));
                } else {
                    InContent.add(new InText(line.endsWith(" ") ? line : line + " "));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: unable to read input file.");
            e.printStackTrace();
        }
        // Add an .paragraph after every .indent (except for the first one)
        boolean notfirst = false;
        for (int i = 0; i < InContent.size(); ++i) {
            InElement nowElement = InContent.get(i);
            if (nowElement.isCommand() && nowElement.getContent().startsWith("indent")) {
                if (notfirst) {
                    InContent.add(i + 1, new InCommand("paragraph"));
                } else {
                    notfirst = true;
                }
            }
        }
        //change the sequence of .paragraph
        int count = 0;
        int nowText = findNextText(count, InContent);
        int nowPara = findNextPara(count, InContent);
        while (count < InContent.size()) {
            if (nowPara < nowText) {
                InElement temp = InContent.get(nowPara);
                InContent.set(nowPara, InContent.get(count));
                InContent.set(count, temp);
            }
            count = nowText + 1;
            if (count >= InContent.size()) {
                break;
            }
            if (InContent.get(count).isCommand() && InContent.get(count).getContent().equals("paragraph")) {
                count++;
                if (count >= InContent.size()) {
                    break;
                }
            }
            nowText = findNextText(count, InContent);
            nowPara = findNextPara(count, InContent);
        }

    }

    /**
     * Generate the PDF document based on InContent
     *
     * @param InContent The arraylist that stores all the input elements
     * @param document  The document of that we are editing
     */
    public void render(ArrayList<InElement> InContent, Document document) {
        int count = 0;
        while (count < InContent.size()) {
            Paragraph para = new Paragraph().setMarginLeft(defaultIndent).setMarginRight(defaultIndent);
            while (count < InContent.size()) {
                int nextcount = findNextText(count, InContent);
                Text text = new Text(InContent.get(nextcount).getContent()).setFont(nowFont).setFontSize(nowFontSize);
                for (int i = count; i < nextcount; ++i) {
                    solveCommand(InContent.get(i), text, para);
                }
                para.add(text);
                count = nextcount + 1;
                if (count >= InContent.size()) {
                    break;
                }
                if (InContent.get(count).isCommand() && InContent.get(count).getContent().equals("paragraph")) {
                    break;
                }
            }
            document.add(para);
        }
    }

    /**
     * @param nowcount the current count that serves as a base
     * @param elements the arraylist that stores InElement elements
     * @return the index position of the next InText found within the elements, starting from nowcount
     */
    public int findNextText(int nowcount, ArrayList<InElement> elements) {
        for (int i = nowcount; i < elements.size(); ++i) {
            if (!elements.get(i).isCommand()) {
                return i;
            }
        }
        return elements.size();
    }

    /**
     * @param nowcount the current count that serves as a base
     * @param elements the arraylist that stores InElement elements
     * @return the index position of the next .paragraph command found within the elements, starting from nowcount
     */
    public int findNextPara(int nowcount, ArrayList<InElement> elements) {
        for (int i = nowcount; i < elements.size(); ++i) {
            if (elements.get(i).isCommand() && elements.get(i).getContent().equals("paragraph")) {
                return i;
            }
        }
        return elements.size();
    }

    /**
     * @param command   The command that we are trying to solve
     * @param text      The current text, since it is an object and thus pass by reference, we could make adjustment to it based on the command
     * @param paragraph The current paragraph, since it is an object and thus pass by reference, we could make adjustment to it based on the command
     */
    public void solveCommand(InElement command, Text text, Paragraph paragraph) {
        String content = command.getContent();
        if (content.equals("large")) {
            text.setFontSize(16);
            nowFontSize = 16;
        } else if (content.equals("normal")) {
            nowFontSize = defaultFontSize;
            text.setFontSize(nowFontSize);
        } else if (content.equals("italics")) {
            text.setItalic();
            if (nowFont.equals(NORMAL)) {
                nowFont = ITALIC;
            }
            if (nowFont.equals(BOLD)) {
                nowFont = BOLD_ITALIC;
            }
        } else if (content.equals("bold")) {
            text.setBold();
            if (nowFont.equals(NORMAL)) {
                nowFont = BOLD;
            }
            if (nowFont.equals(ITALIC)) {
                nowFont = BOLD_ITALIC;
            }
        } else if (content.equals("fill")) {
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
        } else if (content.equals("nofill")) {
            paragraph.setTextAlignment(TextAlignment.LEFT);
        } else if (content.equals("regular")) {
            text.setFont(NORMAL);
            nowFont = NORMAL;
        } else if (content.startsWith("indent")) {
            int indent = Integer.parseInt(content.substring(8));
            if (content.charAt(7) == '+') {
                nowIndent += indent;
            } else {
                nowIndent -= indent;
            }
            paragraph.setMarginLeft(defaultIndent + nowIndent * 10);
            paragraph.setMarginRight(defaultIndent);
        }
    }
}