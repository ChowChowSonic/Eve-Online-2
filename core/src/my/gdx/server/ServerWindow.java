package my.gdx.server;

import java.awt.Color;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class ServerWindow extends JFrame{
    private JTextArea area;
    private JScrollPane bar; 
    private LogStream logs; 
    
    public ServerWindow(File file) throws IOException{
        super(); 
        int x = 120*6, y= 80*6; 

        area = new JTextArea();
        area.setBackground(Color.BLACK);
        area.setForeground(Color.LIGHT_GRAY);
        area.setBounds(0, 0, x-20, y);
        area.setFont(new Font("Default", 0, 13));
        area.setLineWrap(true);

        bar = new JScrollPane(area);//adds the previously defined text area to the Jscrollpane; Now it will be included with the scrollpane when you say frame.add(bar); 
        bar.setOpaque(true);
        bar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        bar.setAlignmentX(x);
        bar.setAlignmentY(0);
        bar.setBackground(Color.BLACK);
        bar.setForeground(Color.LIGHT_GRAY);
        bar.setOpaque(true);
        bar.setAutoscrolls(true);

        logs = new LogStream(area, file); 
        System.setOut(logs);
        System.setErr(logs);
        
        this.add(bar);
        this.setResizable(false);
        this.setSize(x,y);
        this.setVisible(true);
        this.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
    }
    
    @Override
    public void dispose(){
        super.dispose();
        System.out.println("------END OF SERVER LOGS UNTIL NEXT RESTART------");
        System.exit(0);//Replace this with a way to shutdown all the servers later on
    }
    
}
