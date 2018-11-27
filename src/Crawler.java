import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Crawler extends Thread{
    String name;
    List<String> sourceUrlList = new ArrayList<>();
    int count=0,selected=0;

    public Crawler(String name,int selected){
        this.name=name;
        this.selected=selected;
    }
    public List<String>  getUrls() throws Exception{
        String size;
        //   0=>任何大小 , 4mp=>大於400萬 , 2mp=>大於200萬 , xga=>大於1024*768 , svga=>大於800*600 , vga=>大於640*480 , qsvga=>大於400*300
        switch (selected){
            case  1:
                size="4mp";
                break;
            case 2:
                size="2mp";
                break;
            case  3:
                size="xga";
                break;
            case 4:
                size="svga";
                break;
            case  5:
                size="vga";
                break;
            case 6:
                size="qsvga";
                break;
            default:
                size="0";
        }

        String url="https://www.google.com/search?q="+name+"&source=lnms&tbm=isch&tbs=isz:lt,islt:"+size;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".rg_meta.notranslate");
        for (Element link : elements) {
            String temp = link.text();
            Object j = new JSONObject(temp).get("ou");
            String tt=j.toString();
            //System.out.println("src : "+tt);
            sourceUrlList.add(tt);
        }
        return sourceUrlList;
    }

    public void saveImges () throws Exception {

        getUrls();
        UI.showMsg(UI.tea,"\nStart!\n",12);
        File file = new File("D:\\pic");
        if (!file.exists()) {
            file.mkdir();
            String cmd = "cmd.exe /c start \"\" "+"\""+file.getPath()+"\"";
            Runtime.getRuntime().exec(cmd);
            UI.showMsg(UI.tea, "Size : "+sourceUrlList.size()+" Quality :"+selected, 12);
        }
        else{
            String cmd = "cmd.exe /c start \"\" "+"\""+file.getPath()+"\"";
            Runtime.getRuntime().exec(cmd);
           // UI.showMsg(UI.tea, "Size : "+sourceUrlList.size()+" Quality :"+selected, 12);
        }
        for (String url : sourceUrlList) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new Thread(new Runnable() {
                @Override
                synchronized public void run() {
                    try {

                        //System.out.println(url);
                        URL oneurl = new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) oneurl.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(10 * 1000);
                        InputStream inStream = conn.getInputStream();
                        byte[] data = readInputStream(inStream);
                        String filename = "D:\\pic\\" + new Date().getTime() + ".jpg";
                        File imageFile = new File(filename);
                        FileOutputStream outStream = new FileOutputStream(imageFile);
                        outStream.write(data);


                       // UI.showMsg(UI.tea, url, 12);
                       // UI.showMsg(UI.tea, count + " : " + filename + "  -OK", 12);
                        count++;
                        inStream.close();
                        outStream.close();


                    } catch (Exception e) {
                        //UI.showMsg(UI.tea, "error : " + e.toString(), 12);
                        count++;
                    }
                }
            }).start();

        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (count == sourceUrlList.size()) {
                        count=0;
                        sourceUrlList=null;
                        //UI.showMsg(UI.tea, "\n----------------------------DONE----------------------------\n", 12);
                        UI.processButton2.setEnabled(true);
                        break;
                    }
                }
            }
        }).start();
    }

    public  byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        outStream.close();
        return outStream.toByteArray();
    }

    @Override
    public void run() {
        try {
            saveImges();

        } catch (Exception e) {
            UI.showMsg(UI.tea,"\n"+e.toString()+"\n",12);
        }

    }
}
