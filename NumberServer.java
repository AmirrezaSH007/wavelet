import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

interface URLHandler {
    String handleRequest(URI url);
}

class Handler implements URLHandler {
    // The one bit of state on the server: the chat log that gets added to by incoming requests.
    StringBuilder chatLog = new StringBuilder();

    public String handleRequest(URI url) {
        if ("/".equals(url.getPath())) {
            return chatLog.toString().isEmpty() ? "Chat is empty." : chatLog.toString();
        } else if ("/add-message".equals(url.getPath())) {
            Map<String, String> queryPairs = splitQuery(url);
            String user = queryPairs.get("user");
            String message = queryPairs.get("s");
            if (user != null && message != null) {
                chatLog.append(user).append(": ").append(message).append("\n");
                return chatLog.toString();
            } else {
                return "Invalid request";
            }
        } else {
            return "404 Not Found!";
        }
    }

    private Map<String, String> splitQuery(URI url) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = url.getQuery();
        if (query == null) {
            return queryPairs;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                               URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (IOException e) {
                throw new AssertionError("UTF-8 is unknown", e);
            }
        }
        return queryPairs;
    }
}

public class NumberServer {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }
        
        int port = Integer.parseInt(args[0]);
        Server.start(port, new Handler());
    }
}
