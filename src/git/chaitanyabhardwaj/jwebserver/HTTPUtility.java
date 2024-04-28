package git.chaitanyabhardwaj.jwebserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

/*
Used to:
- Parse incoming HTTP requests
- Provide helper methods for building HTTP headers for outgoing req/res
 */
public class HTTPUtility {

    public static Map<String, String> parseHeaders(String req)throws IllegalHTTPRequestException {
        return parseHeaders(req, ":");
    }

    public static Map<String, String> parseHeaders(String req, String delimiter)throws IllegalHTTPRequestException {
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>parseHeaders()>start");
        Map<String, String> map = new LinkedHashMap<>();
        String[] lines = req.split("\\r?\\n");
        map.put(HTTPConstants.HEADER_LINE_ONE, lines[0]);
        String[] line1Parts = lines[0].split("\\s+");
        if(line1Parts.length < 3) throw new IllegalHTTPRequestException("Error parsing HTTP Request line 1: " + Arrays.stream(line1Parts).toList());
        map.put(HTTPConstants.REQ_METHOD, line1Parts[0]);
        map.put(HTTPConstants.REQ_URL, line1Parts[1].equals("/")?HTTPConstants.ROOT_FILE:line1Parts[1]);
        map.put(HTTPConstants.REQ_HTTP_VERSION, line1Parts[2]);
        for(int i = 1; i < lines.length; i++) {
            String l = lines[i];
            if(l == null || l.isBlank() || l.isEmpty() || !l.contains(delimiter)) continue;
            String key = l.substring(0, l.indexOf(delimiter)).strip();
            String value = l.substring(l.indexOf(delimiter) + 1).strip();
            map.put(key,value);
        }
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>parseHeaders()>end");
        return map;
    }

    public static Map<String, String> capture(Socket client)throws IllegalHTTPRequestException {
        /*HTTP Request parts
        Headers
        - line1 (method, url, http version)
        - rest of the lines (parameter: data)
        - \r\n extra line specifying end of headers
        Body (if POST, PUT, etc.) read till content length
        */
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>capture()>start");
        Map<String, String> req = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            req = captureHeaders(br);
            if(req == null) return null;
            if (req.containsKey(HTTPConstants.HEADER_CONTENT_LENGTH)) {
                int contentLen = Integer.parseInt(req.get(HTTPConstants.HEADER_CONTENT_LENGTH));
                String body = captureBody(br, contentLen);
                if(!body.isBlank() || !body.isEmpty())
                    req.put(HTTPConstants.REQ_BODY, body);
            }
        } catch (IOException e) {
            System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>capture()> error: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new IllegalHTTPRequestException("git.chaitanyabhardwaj.jwebserver.HTTPUtility>capture()> error: " + e.getMessage());
        }
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>capture()>end");
        return req;
    }

    public static Map<String, String> captureHeaders(BufferedReader br)throws IllegalHTTPRequestException, IOException {
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>captureHeaders()>start");
        String read;
        StringBuilder reqBuilder = new StringBuilder();
        while(true) {
            read = br.readLine();
            if(read == null || read.isEmpty() || read.isBlank() || read.equals("\\r?\\n")) break;
            reqBuilder.append(read);
            reqBuilder.append("\r\n");
        }
        String req = reqBuilder.toString();
        if(req.isBlank() || req.isEmpty()) {
            br.close();
            return null;
        }
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>captureHeaders()>end");
        return parseHeaders(req);
    }

    public static String captureBody(BufferedReader br, int len)throws IOException {
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>captureBody()>start");
        String read;
        StringBuilder bodyBuilder = new StringBuilder();
        while(len > 0) {
            read = br.readLine();
            if(read == null) break;
            bodyBuilder.append(read);
            bodyBuilder.append("\r\n");
            len -= (read.length() + 1);
        }
        //br.close();
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>captureBody()>end");
        return bodyBuilder.toString();
    }

    public static String buildHeaders(Map<String, String> req) {
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>buildHeaders()>start");
        String headers = req.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\r\n"));
        //end of headers
        headers += "\r\n";
        System.out.println("git.chaitanyabhardwaj.jwebserver.HTTPUtility>buildHeaders()>end");
        return headers;
    }

}
