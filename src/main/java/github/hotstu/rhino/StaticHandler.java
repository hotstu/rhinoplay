package github.hotstu.rhino;

import fi.iki.elonen.NanoHTTPD;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticHandler {

    Pattern pattern = Pattern.compile("^/static/([\\s|\\S]*)$", Pattern.CASE_INSENSITIVE);

    public boolean isHandle(NanoHTTPD.IHTTPSession session) {
        final String uri = session.getUri();
        final Matcher matcher = pattern.matcher(uri);
        return matcher.find();
    }

    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        final String uri = session.getUri();
        final Matcher matcher = pattern.matcher(uri);
        if (matcher.matches()) {
            final String subPath = matcher.group(1);
            //TODO 文件访问控制
            String mimeType = "application/octet-stream";
            if (subPath.endsWith("js")) {
                mimeType = "text/javascript";
            } else if (subPath.endsWith("css")) {
                mimeType = "text/css";
            } else if (subPath.endsWith("html")) {
                mimeType = "text/html";
            }
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(subPath);
            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeType, inputStream);
        }
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NO_CONTENT,"application/octet-stream","");
    }
}
