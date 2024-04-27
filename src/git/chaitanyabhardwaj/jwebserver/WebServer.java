package git.chaitanyabhardwaj.jwebserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WebServer {

    final public static Map<String, String> MEDIA_TYPE = new HashMap<>();
    public void start(int port) {
        try {
            init();
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket;
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Got a new connection!");
                new Thread(new HTTPHandler(socket)).start();
            }
        }catch(IOException ex) {
            System.out.println("git.chaitanyabhardwaj.jwebserver.WebServer>start()> error: " + ex.getMessage());
        }
    }

    public static void init() throws IOException {
        List<String> configLines = Files.readAllLines(Paths.get(HTTPConstants.MEDIA_CONFIG));
        String mediaType = "";
        for(String l : configLines) {
            if(l.startsWith("#")) mediaType = l.substring(1);
            else MEDIA_TYPE.put(l, mediaType);
        }
    }

    /*
    - Read request
    - Parse request
    - Build response
    - Send response
     */
    public static class HTTPHandler implements Runnable {

        final private Socket CLIENT;

        public HTTPHandler(Socket socket) {
            CLIENT = socket;
        }

        @Override
        public void run() {
            //read request
            try {
                DataOutputStream writer = new DataOutputStream(CLIENT.getOutputStream());
                System.out.println("Reading request - ");
                Map<String, String> requestData = HTTPUtility.capture(CLIENT);
                System.out.println("Req data - Thread: " + requestData);
                if(requestData != null) {
                    //extract parameters
                    System.out.println("Extracting parameters");
                    String filePath = requestData.getOrDefault(HTTPConstants.REQ_URL, "");
                    String mediaFormat = filePath.substring(filePath.lastIndexOf(".") + 1).strip();
                    String contentType = MEDIA_TYPE.getOrDefault(mediaFormat, "");
                    if(contentType.isEmpty() || contentType.isBlank())
                        contentType = requestData.getOrDefault(HTTPConstants.HEADER_ACCEPT, "");
                    else
                        contentType += "/" + mediaFormat;
                    System.out.println("File path: " + filePath);
                    System.out.println("Content type: " + contentType);
                    //build response
                    System.out.println("Building response");
                    if(contentType.contains(","))
                        contentType = contentType.substring(0, contentType.indexOf(",")).strip();
                    File file = new File("." + filePath);
                    StringBuilder responseBuilder = new StringBuilder(requestData.getOrDefault(HTTPConstants.REQ_HTTP_VERSION, HTTPConstants.HTTP_VERSION_1_1));
                    responseBuilder.append(" ");
                    byte[] fileData = null;
                    if(!file.exists()) {
                        responseBuilder.append(HTTPConstants.STATUS_404);
                        responseBuilder.append("\r\n\r\n");
                    }
                    else {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        fileData = new byte[(int) fileInputStream.available()];
                        fileInputStream.read(fileData);
                        fileInputStream.close();
                        Map<String, String> resHeaders = new LinkedHashMap<>();
                        resHeaders.put(HTTPConstants.HEADER_CONTENT_TYPE, contentType);
                        resHeaders.put(HTTPConstants.HEADER_CONTENT_LENGTH, fileData.length + "");
                        responseBuilder.append(HTTPConstants.STATUS_200);
                        responseBuilder.append("\r\n");
                        responseBuilder.append(HTTPUtility.buildHeaders(resHeaders));
                        responseBuilder.append("\r\n");
                    }
                    String response = responseBuilder.toString();
                    System.out.println("Response headers: " + response);
                    //send response
                    writer.writeBytes(response);
                    if(fileData != null)
                        writer.write(fileData);
                    System.out.println("Sent!");
                }
                writer.close();
                CLIENT.close();
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("git.chaitanyabhardwaj.jwebserver.WebServer>HTTPHandler>run()> error: " + e.getMessage());
            }
        }

    }

}
