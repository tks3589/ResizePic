import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResizePic extends Thread {
    //do resize
    //return msg function

    String sourcePath;
    int scaleSize=0;
    List<String> sourcePathList = new ArrayList<>();

    ResizePic(String sourcePath,int scaleSize){
        this.sourcePath = sourcePath;
        this.scaleSize = scaleSize;
    }

    public void getFiles(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            for (String fileName : file.list()) {
                getFiles(path+"\\"+fileName);
            }
        }
        else{
            sourcePathList.add(file.getPath());
        }
    }


    public void resize () throws Exception{

        //get resize data to list
        getFiles(sourcePath);

        //new directory
        String tmp = sourcePath.substring(0,sourcePath.lastIndexOf("\\")+1);
        String tmp2=tmp+"Output\\";
        File oldFile = new File(tmp2);
        if(!oldFile.exists())
            oldFile.mkdir();

        //resize
        for (String path : sourcePathList ) {

            //new directory and save resized data
            String newPath = tmp2+path.substring(path.indexOf(tmp)+tmp.length());
            File newPath2 = new File(newPath.substring(0,newPath.lastIndexOf("\\")));
            if(!newPath2.exists())
                newPath2.mkdirs();

            String format = path.substring(path.lastIndexOf(".")+1);
            if(format.equals("jpg")||format.equals("jpeg")||format.equals("JPG")
                ||format.equals("JPEG")||format.equals("png")||format.equals("PNG")
                ||format.equals("bmp")||format.equals("BMP")) {

                File srcFile = new File(path);
                try{
                    Image srcImg = ImageIO.read(srcFile);
                    BufferedImage buffImg = new BufferedImage(scaleSize, scaleSize, BufferedImage.TYPE_INT_RGB);
                    buffImg.getGraphics().drawImage(srcImg.getScaledInstance(scaleSize, scaleSize, Image.SCALE_SMOOTH), 0, 0, null);
                    File newFile = new File(newPath);
                    switch (format) {
                        case "jpg":
                        case "jpeg":
                        case "JPG":
                        case "JPEG":
                            ImageIO.write(buffImg, "JPG", newFile);
                            break;
                        case "png":
                        case "PNG":
                            ImageIO.write(buffImg, "PNG", newFile);
                            break;
                        case "bmp":
                        case "BMP":
                            ImageIO.write(buffImg, "BMP", newFile);
                            break;
                    }
                   // UI.showMsg(UI.tea,newPath+"  -----------------------> OK! ",12);

                }catch(Exception ee){
                    //UI.showMsg(UI.tea,"\n"+ee.toString()+" ---------------> "+path+"\n",12);
                    continue;
                }

                //tea.setForeground(Color.GREEN);
               // tea.append(newPath + " -----------------------> OK! " + "\n");
                //tea.setCaretPosition(tea.getText().length());

            }
            else{
                //tea.setForeground(Color.RED);
                //tea.append(newPath + " -----------------------> FAIL! " + "\n");
                //tea.setCaretPosition(tea.getText().length());
                UI.showMsg(UI.tea,"\n"+newPath+"  -----------------------------------------------------> FAIL! \n",12);
            }

        }

        //finish
        processFinish(tmp2);


    }

    public void processFinish(String savePath) throws Exception{
        sourcePathList.clear();
        UI.showMsg(UI.tea,"\n"+"  ----------------------------DONE----------------------------\n",12);
        String cmd = "cmd.exe /c start \"\" "+"\""+savePath+"\"";
        //tea.append(cmd+"\n");
        Runtime.getRuntime().exec(cmd);
        UI.processButton.setEnabled(true);
    }

    @Override
    public void run() {
        try {  //這裡出錯UI是否抓的到??
            resize();
        } catch (Exception e) {
            UI.showMsg(UI.tea,"\n"+e.toString()+"\n",12);
            UI.processButton.setEnabled(true);
        }

    }
}
