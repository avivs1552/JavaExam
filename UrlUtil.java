import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;

public class UrlUtil {
    private static final String URL_REGEX = "((http://|https://)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(/([a-zA-Z-_/.0-9#:?=&;,]*)?)?)";
    private static final String PREFIX_PATH = System.getProperty("user.dir").concat("\\");
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    public static boolean validateUrl(String url) {
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    public static String getUrlContent(String pageUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(pageUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }


    public static String CleanUrl(String Urlpage) {
        String s2 = null;
        String postCleandUrl = ".html";
        if (Urlpage.startsWith(HTTPS_PREFIX)) {
            s2 = Urlpage.replace(HTTPS_PREFIX, ".");
            s2 = s2.substring(1);
        }
        if (Urlpage.startsWith(HTTP_PREFIX)) {
            s2 = Urlpage.replace(HTTP_PREFIX, ".");
            s2 = s2.substring(1);
        }
        assert s2 != null;
        s2 = s2.replace(".", "_");
        s2 = s2.replace("/", "_");
        if(s2.contains("_html")) {
            s2 = s2.replace("_html", ".html");
        }
        if(!s2.contains(".html")) {
            s2 = s2 + postCleandUrl;
        }
        return s2;
    }


    public static String getUrlFromLine(String line) {
        List<String> list = new ArrayList<>(List.of());
        String noUrl = "no url";
        if(line.contains(".jpeg") || line.contains(".png")) {
            return noUrl; }
        String actualUrl = "";
        String regex
                = "\\b((?:https?|ftp|file):"
                + "//[-a-zA-Z0-9+&@#/%?="
                + "~_|!:, .;]*[-a-zA-Z0-9+"
                + "&@#/%=~_|])";
        Pattern p = Pattern.compile(
                regex,
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(line);
        while (m.find()) {
            actualUrl = line.substring(m.start(0), m.end(0));
            list.add(actualUrl);
        }
        if (list.size() == 0) {
            return noUrl;
        }
        return actualUrl;
    }


    private static void createFolderIfNecessary(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()){
            boolean success = file.mkdirs();
            if (!success) {
                throw new RuntimeException(String.format("Failed to create folder named: %s", folderPath));
            }
        }
    }


    private static int checkUniqueness(File folderpath) {
        for(File f: folderpath.listFiles())
        {

        }

        return 1;
    }

    private static void saveContent(String content, String fileName, int depth) {
        String folderName = Integer.toString(depth);
        String folderPath = PREFIX_PATH.concat(folderName);
        createFolderIfNecessary(folderName);
        createFile(content, fileName, folderPath);
    }

    private static void createFile(String content, String fileName, String folderPath) {
        String filePath = folderPath.concat("\\").concat(fileName);

        File file = new File(filePath);
        try {
            boolean success = file.createNewFile();
            //throw new IOException(String.format("Failed creating file: %s", filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!file.exists()) {
            throw new RuntimeException(String.format("Failed to create file called: %s", filePath));
        }
        writeContentToFile(content, filePath);
    }


    private static void writeContentToFile(String content, String filePath) {
        try {
            FileWriter filerWriter = new FileWriter(filePath);
            filerWriter.write(content);
            filerWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleUrl(String url, int numberOfUrls, int depth, int maxDepth, boolean uniquenessFlag) {
        /*
         1- Get current url content.
         2- Save the content (including create a folder and an HTML file).
         3- Get the next urls.
         4- Continue the loop for each url (depending on the depth).
         */
        String urlContent = UrlUtil.getUrlContent(url);
        String cleanedUrl = CleanUrl(url);
        saveContent(urlContent, cleanedUrl, depth);
        List<String> urls = getUrlsFromContent(numberOfUrls, uniquenessFlag, urlContent);
        while (maxDepth > depth) {
            int currentDepth = depth + 1;
            urls.forEach(currentUrl -> UrlUtil.handleUrl(currentUrl, numberOfUrls, currentDepth, maxDepth, uniquenessFlag));
            ++depth;
        }
    }

    private static boolean checkUrl(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }



    private static List<String> getUrlsFromContent(int numberOfUrls, boolean uniquenessFlag, String urlContent) {
        String[] lines = urlContent.split("\n");
        List<String> urls = new ArrayList<>();
        if(uniquenessFlag) {
            for(String l: lines) {
                String potentionalUrl = getUrlFromLine(l);
                if(!Objects.equals(potentionalUrl, "no url")) {
                    if (urls.size() < numberOfUrls) {
                        if(!urls.contains(potentionalUrl)) {
                            urls.add(potentionalUrl);
                        }
                    }
                    else {
                        return urls;
                    }
                }
            }
        }
        else {
            for(String l: lines) {
                String potentionalUrl = getUrlFromLine(l);
                if(!Objects.equals(potentionalUrl, "no url")) {
                    if (urls.size() < numberOfUrls) {

                        urls.add(potentionalUrl);

                    }
                    else {
                        return urls;
                    }
                }
            }
        }
        return urls;
    }
}
