/**
 * @description: a concurrent picture crawler
 * @author: Zhizhou Qiu
 * @create: 04-20-2019
 **/

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyPictureCrawler {

    public static void main(String[] args) throws Exception {
        //Download path
        String downloadPath = "/Users/zhizhouqiu/Downloads/pics/bands/chaos";
        // input by command line
        System.out.println("Input keywords:（with space, comma to seperate keywords）: ");
        Scanner KeyWord = new Scanner(System.in);
        String Word = KeyWord.nextLine();

        //input by read files
//        String Word = getList(args[0]);

        List<String> list = nameList(Word);
        getPictures(list, 300, downloadPath); // max means the maxinmum pages to be downloaded
    }

    public static void getPictures(List<String> keywordList, int max, String downloadPath) throws Exception { // max is the pages to crawl
        String gsm = Integer.toHexString(max) + "";
        String tempPath = "";

        ExecutorService pool = Executors.newCachedThreadPool();


        for (String keyword : keywordList) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    concurrentProcess(keyword, max, downloadPath);
                }
            };
            pool.execute(runnable);
        }
        sop("Download Finished");
        delMultyFile(downloadPath);
        sop("Blank pages are deleted");
    }

    private static void concurrentProcess(String keyword, int max, String downloadPath){
        String tempPath = downloadPath;
        if (!tempPath.endsWith("\\")) {
            tempPath = downloadPath + "\\";
        }
        tempPath = tempPath + keyword + "\\";
        File f = new File(tempPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        int picCount = 1;
        String finalURL = "";
        for (int page = 0; page <= max; page++) {
            sop("Downloading" + page);
            Document document = null;
            try {
                String url = "http://image.baidu.com/search/avatarjson?tn=resultjsonavatarnew&ie=utf-8&word=" + keyword + "&cg=star&pn=" + page * 30 + "&rn=30&itg=0&z=0&fr=&width=&height=&lm=-1&ic=0&s=0&st=-1&gsm=" + Integer.toHexString(page * 30);
                sop(url);
                document = Jsoup.connect(url).data("query", "Java")
                        .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")//setting urer-agent  get();
                        .timeout(5000)
                        .get();
                String xmlSource = document.toString();
                xmlSource = StringEscapeUtils.unescapeHtml3(xmlSource);
                sop(xmlSource);
                String reg = "objURL\":\"http://.+?\\.jpg";
                Pattern pattern = Pattern.compile(reg);
                Matcher m = pattern.matcher(xmlSource);
                while (m.find()) {
                    finalURL = m.group().substring(9);
                    sop(keyword + picCount++ + ":" + finalURL);
                    download(finalURL, tempPath);
                    sop("             Download successfully");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void delMultyFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("File \"" + path + "\" NotFound when excute the method of delMultyFile()....");
        }
        File[] fileList = file.listFiles();
        File tempFile = null;
        for (File f : fileList) {
            if (f.isDirectory()) {
                {
                    delMultyFile(f.getAbsolutePath());
                }
            } else {
                if (f.length() == 0) {
                    sop(f.delete() + "---" + f.getName());
                }
            }
        }
    }

    private static List<String> nameList(String nameList) {
        List<String> arr = new ArrayList<String>();
        String[] list;
        if (nameList.contains(",")) {
            list = nameList.split(",");
        }
        else if (nameList.contains("、")) {
            list = nameList.split("、");
        }
        else if (nameList.contains(" ")) {
            list = nameList.split(" ");
        }
        else {
            arr.add(nameList);
            return arr;
        }
        for (String s : list) {
            if (s.equals(" ")) continue;
            s = s.replaceAll(",","");
            arr.add(s);
        }
        return arr;
    }

    private static void sop(Object obj) {
        System.out.println(obj);
    }

    //download pictures accoring to url
    private static void download(String url, String path) {
        //path = path.substring(0,path.length()-2);
        File file = null;
        File dirFile = null;
        FileOutputStream fos = null;
        HttpURLConnection httpCon = null;
        URLConnection con = null;
        URL urlObj = null;
        InputStream in = null;
        byte[] size = new byte[1024];
        int num = 0;
        try {
            String downloadName = url.substring(url.lastIndexOf("/") + 1);
            dirFile = new File(path);
            if (!dirFile.exists() && path.length() > 0) {
                if (dirFile.mkdir()) {
                    sop("creat document file \"" + path.substring(0, path.length() - 1) + "\" success...\n");
                }
            } else {
                file = new File(path + downloadName);
                fos = new FileOutputStream(file);
                if (url.startsWith("http")) {
                    urlObj = new URL(url);
                    con = urlObj.openConnection();
                    httpCon = (HttpURLConnection) con;
                    in = httpCon.getInputStream();
                    while ((num = in.read(size)) != -1) {
                        for (int i = 0; i < num; i++) {
                            fos.write(size[i]);
                        }
                    }
                }
            }
        } catch (FileNotFoundException notFoundE) {
            sop("cannot find this picture....");
        } catch (NullPointerException nullPointerE) {
            sop("picture not found....");
        } catch (IOException ioE) {
            sop("IO Blocking....");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getList(String path) throws IOException{
        Scanner scanner = new Scanner(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            s = s.replaceAll(" ", "");
            sb.append(s);
            sb.append(",");
        }
        scanner.close();
        return sb.toString();
    }

}
