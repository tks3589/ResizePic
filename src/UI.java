import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


public class UI extends JFrame implements ActionListener, MenuListener {
    Container c;
    JLabel sourceTitle,sourceText, pixelsLengthTitle,crawlerTitle;
    static JButton sourceButton,processButton,processButton2;
    static JTextArea tea;
    JScrollPane jsp;
    JPanel panel,panel2,panel3;
    JMenu about,crawler,resize;
    JMenuBar menubar;
    JTextField pixelsLength,crawlerName;
    JComboBox imgQuality;
    String[] qualityOpt = new String[]{"任何大小","大於四百萬像素","大於兩百萬像素","大於1024*768","大於800*600","大於640*480","大於400*300"};
    String sourcePath;
    int scaleSize=0;
    Robot robot;
    UI() {
        super("圖片處理工具");
        c=getContentPane();
        //c.setLayout(new GridLayout(6,4));
        c.setLayout(new BorderLayout());

        //c.setLayout(new FlowLayout());
        sourceTitle = new JLabel("來源 :");
        pixelsLengthTitle = new JLabel("解析度 :");
        sourceText = new JLabel();
        sourceButton = new JButton("選擇");
        processButton = new JButton("處理");
        processButton2 = new JButton("處理");
        tea=new JTextArea(20,50);
        tea.setFont(new Font("Serif",Font.PLAIN,12));
        tea.setBackground(Color.BLACK);
        jsp = new JScrollPane(tea);
        panel = new JPanel();
        panel2 = new JPanel();
        about = new JMenu("關於");
        crawler = new JMenu("爬蟲");
        resize = new JMenu("調整");
        menubar = new JMenuBar();
        pixelsLength = new JTextField(5);

        crawlerTitle = new JLabel("名稱 : ");
        crawlerName = new JTextField(20);
        imgQuality = new JComboBox(qualityOpt);
        panel3 = new JPanel();

        addObj();
        setSize(640,480);
        setLocation(400,300);



        setVisible(true);
    }

    public void addObj(){

        menubar.add(about);
        menubar.add(crawler);
        menubar.add(resize);

        panel.add(sourceTitle);
        panel.add(sourceText);
        panel.add(sourceButton);

        panel2.add(pixelsLengthTitle);
        panel2.add(pixelsLength);
        panel2.add(processButton);

        panel3.add(crawlerTitle);
        panel3.add(crawlerName);
        panel3.add(processButton2);
        panel3.add(imgQuality);
        panel3.setVisible(false);

        c.add(menubar,BorderLayout.NORTH);
        c.add(panel,BorderLayout.CENTER);
        c.add(panel3,BorderLayout.WEST);
        c.add(panel2,BorderLayout.EAST);
        c.add(jsp,BorderLayout.SOUTH);



        about.addMenuListener(this);
        crawler.addMenuListener(this);
        resize.addMenuListener(this);
        sourceButton.addActionListener(this);
        processButton.addActionListener(this);
        processButton2.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sourceButton){
            JFileChooser jfc = new JFileChooser("./");
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int	rst = jfc.showOpenDialog(this);
            if(rst==JFileChooser.APPROVE_OPTION)
                sourcePath = jfc.getSelectedFile().toString();
            sourceText.setText(sourcePath);
        }
        else if(e.getSource() == processButton){

            try {
                processButton.setEnabled(false);
                tea.setText("");
                scaleSize = Integer.parseInt(pixelsLength.getText());
                new ResizePic(sourcePath,scaleSize).start();

            }catch(Exception ee){
               showMsg(tea,"\n"+ee.toString()+"\n",12);
                processButton.setEnabled(true);
            }

        }
        else if(e.getSource() == processButton2){

            try {
                //processButton.setEnabled(false);
                tea.setText("");
                String name = crawlerName.getText().trim();

                if(name.equals("")){
                   JOptionPane.showMessageDialog(null,"  搜尋名稱不得為空!! ", "訊息",JOptionPane.ERROR_MESSAGE);
                }
                else{
                    int result=JOptionPane.showConfirmDialog(null,
                            "確定關鍵字 \" "+name+" \" 嗎?",
                            "圖片爬蟲",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (result==JOptionPane.YES_OPTION) {
                        processButton2.setEnabled(false);
                        int selected = imgQuality.getSelectedIndex();
                        new Crawler(name,selected).start();
                    }
                }


            }catch(Exception ee){
                showMsg(tea,"\n"+ee.toString()+"\n",12);
                processButton2.setEnabled(true);
            }

        }


    }

    public static void showMsg(JTextArea tea,String msg,int textSize){
        tea.setForeground(Color.WHITE);
        tea.setFont(new Font("Serif",Font.PLAIN,textSize));
        tea.append(msg+"\n");
        tea.setCaretPosition(tea.getText().length());

    }

    @Override
    public void menuSelected(MenuEvent e) {
        if(processButton.isEnabled()&&processButton2.isEnabled()) {
            tea.setText("");
            if (e.getSource() == about) {
                //JOptionPane.showConfirmDialog(null,"© 2018 Aaron Chen (E508 LAB)", "Ver 1.0",JOptionPane.PLAIN_MESSAGE);
                // javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
                String aboutMsg = "Ver 2.0_____© 2018 Aaron Chen (E508 LAB)" +
                        "\n\nSource :   https://github.com/tks3589/ResizePic";
                showMsg(tea, "\n" + aboutMsg + "\n", 12);
                //about.setSelected(false);

            } else if (e.getSource() == crawler) {
                //JOptionPane.showMessageDialog(null, "爬圖片", "Crawler",JOptionPane.PLAIN_MESSAGE);
                //crawler.setSelected(false);
                panel.setVisible(false);
                panel2.setVisible(false);
                panel3.setVisible(true);
            } else if (e.getSource() == resize) {
                panel3.setVisible(false);
                panel.setVisible(true);
                panel2.setVisible(true);
            }
            //javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
            //showMsg(tea,"\n"+e.getSource().toString()+"\n",12);

        }
        //robot -> menubar bug
        try {
            Point point = java.awt.MouseInfo.getPointerInfo().getLocation();
            robot = new Robot();
            robot.mouseMove(point.x, point.y + 30);
            robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
        } catch (Exception ee) {
            showMsg(tea, "\n" + ee.toString() + "\n", 12);
        }
    }

    @Override
    public void menuDeselected(MenuEvent e) {
        //showMsg(tea,"\n"+"menuDeselected_"+opt+"\n",12);
        //showMsg(tea,"\n"+e.getSource().toString()+"\n",12);
    }

    @Override
    public void menuCanceled(MenuEvent e) {
        //showMsg(tea,"\n"+"menuCanceled_"+opt+"\n",12);
        //showMsg(tea,"\n"+e.getSource().toString()+"\n",12);
    }


}

