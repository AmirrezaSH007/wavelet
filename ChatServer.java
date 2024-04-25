import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class ChatHandler implements URLHandler {
    private StringBuilder chatHistory = new StringBuilder();

    public String handleRequest(URI url) {
        if ("/add-message".equals(url.getPath())) {
            Map<String, String> params = queryToMap(url.getQuery());
            String user = params.get("user");
            String message = params.get("s");
            if (user != null && message != null) {
                chatHistory.append(user).append(": ").append(message).append("\n");
                return chatHistory.toString();
            }
            return "Invalid request";
        }
        return "404 Not Found!";
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1].replace("+", " "));
            }
        }
        return result;
    }
}

public class ChatServer {
    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ServerHttpHandler(new ChatHandler()));
        server.start();
        System.out.println("Server started on port " + port);
    }
}
