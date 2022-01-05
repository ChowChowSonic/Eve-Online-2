package my.gdx.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import javax.swing.JTextArea;

public class LogStream extends PrintStream{
    private JTextArea area;
    private File f;  
    public LogStream(JTextArea area, File f) throws IOException {
        super(new File("core\\assets\\entities.txt"));//I need this here and I hate that fact
        //Just assume that the above line clears entities.txt. Technically it's unsafe to set it as that but I really have no other choice. 
        out.close();//closes the underlying outputstream, because I don't need it anymore. 
        this.area = area; 
        this.f = f;
    }
    
    public void writeToFile(String s){
        if(s.equals("") || s.equals("\n")) return; //Logging nothing isn't worthwhile. Just return

        //Format the time in an easily readable way. 
        LocalDateTime LDT = LocalDateTime.now();
        String time = LDT.getMonth().name().substring(0, 3)+"/"+String.format("%02d", LDT.getDayOfMonth())+"/"+LDT.getYear()+" "+
        String.format("%02d:%02d:%02d", LDT.getHour(),LDT.getMinute(),LDT.getSecond()); 

        //Write to the file
        try (FileWriter logger = new FileWriter(f, true)) {
            logger.append("[" + time + "]\t" + s).flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //<--------------WRITING METHODS. FOR SOME REASON THIS SETUP MAKES EVERYTHING WORK AS OF 1/5/2022 AND I DON'T KNOW WHY SO JUST DONT TOUCH IT------------------------------------>
    @Override
    public PrintStream append(CharSequence seq, int start, int end){
        area.append(seq.subSequence(start, end).toString()); 
        writeToFile(seq.toString());
        return this;
    }
    @Override
    public PrintStream append(CharSequence seq){
        area.append(seq.toString()); 
        writeToFile(seq.toString());
        return this;
    }
    @Override
    public PrintStream append(char seq){
        area.append(seq+""); 
        writeToFile(seq+"");
        return this;
    }
    
    // <--------------------PRINTLN() FUNCTIONS------------------------->
    @Override
    public void println(Object s){
        String t = ""+s.toString(); 
        area.append(t+"\n");
        writeToFile(t+"\n");
    }
    @Override
    public void println(boolean s){
        String t = ""+s; 
        area.append(t+"\n");
        writeToFile(t+"\n");
    }
    @Override
    public void println(long s){
        String t = ""+s; 
        area.append(t+"\n");
        writeToFile(t+"\n");
    }
    @Override
    public void println(char s){
        String t = ""+s; 
        area.append(t+"\n");
        writeToFile(t+"\n");
    }
    @Override
    public void println(int s){
        String t = ""+s; 
        area.append(t+"\n");
        writeToFile(t+"\n");
    }
    @Override
    public void println(String s){
        area.append(s+"\n");
        writeToFile(s+"\n");
    }
    // <--------------------------PRINT() FUNCTIONS-------------------------------->
    @Override
    public void print(Object s){
        area.append(s.toString());
        writeToFile(s+"");
    }
    @Override
    public void print(boolean s){
        area.append(s+"");
        writeToFile(s+"");
    }
    @Override
    public void print(long s){
        area.append(s+"");
        writeToFile(s+"");
    }
    @Override
    public void print(char s){
        area.append(s+"");
        writeToFile(s+"");
    }
    @Override
    public void print(int s){
        area.append(s+"");
        writeToFile(s+"");
    }
    @Override
    public void print(String s){
        area.append(s);
        writeToFile(s);
    }
}
