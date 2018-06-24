package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import com.typesafe.config.Config;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerUtil {
    private final static String METADATAFILE_PATH = "/conf/metadataFiles/";
    public static boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getHashValue(String value) {
        int hash = 7;
        for (int i = 0; i < value.length(); i++) {
            hash = hash*31 + value.charAt(i);
        }
        return hash;
    }

    private static ArrayList<String> splitCommaSeparatedString(String text) {
        ArrayList<String> values = new ArrayList<>();
        if(text == null)
            return null;
        while(text.contains(",")) {
            int endIndex = text.indexOf(",");
            values.add(text.substring(0, endIndex));
            text = text.substring(endIndex + 1);
        }
        return values;
    }

    public static ArrayList<String> splitSeparatedString(String text) {
        ArrayList<String> values = new ArrayList<>();
        if(text == null || text.equals(""))
            return null;
        String separator = text.contains(";") ? ";" : text.contains(",") ? "," : text.contains("|") ? "|" : text.contains(" ") ? " " : null;
        if(separator == null) {
            values.add(text);
            return values;
        }
        while(text.contains(separator)) {
            int endIndex = text.indexOf(separator);
            values.add(text.substring(0, endIndex));
            text = text.substring(endIndex + 1);
        }
        return values;
    }

    private static boolean isInAllowedOrigins(String origin, ArrayList<String> allowedOrigins) {
        boolean originIsOk = false;
        for(String value : allowedOrigins) {
            if(value.contains(origin))
                originIsOk = true;
        }
        return originIsOk;
    }

    public static void addHeadersToResponse(Config configuration, Http.Response response, Http.Request request){
        String origin = request.header("origin").orElse(null);
        if(origin == null)
            origin = request.header("Host").orElse(null);
        if(origin != null) {
            ArrayList<String> allowedOrigins = new ArrayList<>();
            allowedOrigins.addAll(splitCommaSeparatedString(configuration.getString("casClient.origin")));
            allowedOrigins.addAll(splitCommaSeparatedString(configuration.getString("casConsumer.origin")));
            if(isInAllowedOrigins(origin, allowedOrigins)) {
                response.setHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                response.setHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            } else {
                response.setHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            }
            response.setHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "X-Requested-With, Origin, Authorization, Accept, Content-Type, Consumer-ID, Access-Control-Allow-Origin");
            response.setHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "HEAD,GET,POST,PUT,DELETE,OPTIONS");
        }
    }

    public static JsonNode createErrorMessageNode(String message) {
        ObjectNode messageJson = Json.newObject();
        messageJson.put("message", message);
        return messageJson;
    }

    public static JsonNode createSuccessMessageNode(String message) {
        ObjectNode messageJson = Json.newObject();
        messageJson.put("message", message);
        return messageJson;
    }

    public static ArrayList<String> getStringArrayFromJsonTree(JsonNode jsonNode, String key) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            final JsonNode arrayNode = new ObjectMapper().readTree(jsonNode.toString()).get(key);
            if (arrayNode.isArray()) {
                for (final JsonNode objNode : arrayNode) {
                    if(objNode.isTextual()) {
                        arrayList.add(objNode.asText());
                    }
                }
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }

        return arrayList;
    }

    public static Long handleCompletionStageOptionalLong(CompletionStage<Optional<Long>> id){
        Optional<Long> optional = Optional.of(0L);
        CompletableFuture<Optional<Long>> completableFuture = id.toCompletableFuture();
        try{
            optional = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return optional.orElse(0L);
    }

    public static Long handleCompletionStageLong(CompletionStage<Long> id){
        CompletableFuture<Long> completableFuture = id.toCompletableFuture();
        try{
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static  String handleCompletionStageOptionalString(CompletionStage<Optional<String>> id){
        Optional<String> optional = Optional.of("");
        CompletableFuture<Optional<String>> completableFuture = id.toCompletableFuture();
        try{
            optional = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return optional.orElse("");
    }

    public static  String handleCompletionStageString(CompletionStage<String> id){
        CompletableFuture<String> completableFuture = id.toCompletableFuture();
        try{
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<String> extractPathFromMarkdown(String markdown) {
        if(markdown == null) {
            return null;
        }
        List<String> paths = new ArrayList<>();
        Matcher m = Pattern.compile("!\\[(?<text>[^\\]]*)\\]\\((?<link>[^\\)]*)\\)").matcher(markdown);
        while (m.find()) {
            paths.add(m.group(2));
        }
        return paths;
    }


    public static ArrayList<String> readMetadataFile(String fileName) {
        if(fileName == null || fileName.equals("")) {
            return null;
        }
        FileInputStream fis = null;
        ArrayList<String> arrayList = new ArrayList<>();
        String absolutePath = play.Environment.simple().rootPath().getAbsolutePath();
        if(absolutePath.indexOf("\\.idea") > 0){
            absolutePath = absolutePath.substring(0, absolutePath.indexOf("\\.idea"));
        }
        File f = new File(absolutePath + METADATAFILE_PATH + fileName);
        try{
            fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String validatedLine = validateLine(line);
                if(validatedLine != null && !validatedLine.equals(""))
                    arrayList.add(validatedLine);
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }

        return arrayList;
    }

    private static String validateLine(String line) {
        line = line.replace(";", "");
        line = line.replace(",", "");
        line = line.trim();
        if(line.charAt(0) == '#') {
            return null;
        }
        return line;
    }
}
