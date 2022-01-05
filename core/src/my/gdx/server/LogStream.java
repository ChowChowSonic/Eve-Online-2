package my.gdx.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.time.LocalDateTime;

import javax.swing.JTextArea;

public class LogStream extends PrintStream{
    JTextArea area; File f; 
    public LogStream(JTextArea area, File f) throws FileNotFoundException {
        super(f);
        this.area = area; 
        this.f = f; 
    }

    public void writeToFile(String s){
        try (FileWriter logger = new FileWriter(f, true);) {
            logger.append("[" + LocalDateTime.now() + "] " + s + "\n");
            logger.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
   
    @Override
    public PrintStream append(CharSequence seq, int start, int end){
        super.append(seq, start, end); 
        writeToFile(seq.toString());
        return this;
    }
    @Override
    public PrintStream append(CharSequence seq){
        super.append(seq); 
        writeToFile(seq.toString());
        return this;
    }
    @Override
    public PrintStream append(char seq){
        super.append(seq); 
        writeToFile(seq+"");
        return this;
    }

    @Override
    public void print(Object s){
        super.print(s);
        area.append(s.toString());
        writeToFile(s+"\n");
    }
    @Override
    public void print(boolean s){
        super.print(s);
        area.append(s+"\n");
        writeToFile(s+"\n");
    }
    @Override
    public void print(long s){
        super.print(s);
        area.append(s+"\n");
        writeToFile(s+"\n");
    }
    @Override
    public void print(char s){
        super.print(s);
        area.append(s+"\n");
        writeToFile(s+"\n");
    }
    @Override
    public void print(int s){
        super.print(s);
        writeToFile(s+"\n");
    }
    @Override
    public void print(String s){
        super.print(s);
        area.append(s+"\n");
        writeToFile(s+"\n");
    }
}
