package github.hotstu.rihino;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;

import java.io.*;
import java.util.HashMap;

public class App extends NanoHTTPD {

    private final Gson g;

    public App() throws IOException {
        super(8080);
        g = new Gson();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
        ScriptRunner.getInstance().start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        final String uri = session.getUri();
        System.out.println(uri);
        if (uri.matches("^/run$")) {
            HashMap<String, String> files = new HashMap<>();
            try {
                session.parseBody(files);
                final String postData = files.get("postData");
                if (postData != null && !"".equals(postData)) {
                    System.out.println(postData);
                    System.out.println("执行脚本");
                    ScirptTask task = new ScirptTask(postData);
                    ScriptRunner.getInstance().addTask(task);
                    task.await(30 * 1000);
                    HashMap<String, Object> ret = new HashMap<>();
                    ret.put("success", task.err == null);
                    ret.put("data", task.output);
                    ret.put("err", task.err);
                    return newFixedLengthResponse(Response.Status.OK, "application/json", g.toJson(ret));

                }
            } catch (IOException | ResponseException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"success\": true}");
        } else if (uri.matches("^/$")) {
            System.out.println("显示页面");
            String msg = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Rihono Play Groud</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div>\n" +
                    "    <div>\n" +
                    "        <textarea class=\"article-input\" id=\"article-input\" style=\"width: 90%\" type=\"text\" rows=\"25\">\n" +
                    "const frame = new Packages.javax.swing.JFrame();\n" +
                    "const label = new Packages.java.awt.TextField();\n" +
                    "label.text = \"hello,world\"\n" +
                    "frame.add(label);\n" +
                    "frame.setSize(400, 400);\n" +
                    "frame.visible = true;\n" +
                    "        </textarea>\n" +
                    "        <button id=\"send\">发送</button>\n" +
                    "    </div>\n" +
                    "    <div>\n" +
                    "        <button id=\"clear\" style=\"display: block\">清除</button>\n" +
                    "        <ul></ul>\n" +
                    "    </div>\n" +
                    "\n" +
                    "</div>\n" +
                    "\n" +
                    "<script>\n" +
                    "    var $ = document.querySelector.bind(document);\n" +
                    "\n" +
                    "    async function postData(url = '', data = \"\") {\n" +
                    "        // Default options are marked with *\n" +
                    "        const response = await fetch(url, {\n" +
                    "            method: 'POST', // *GET, POST, PUT, DELETE, etc.\n" +
                    "            mode: 'cors', // no-cors, *cors, same-origin\n" +
                    "            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached\n" +
                    "            credentials: 'same-origin', // include, *same-origin, omit\n" +
                    "            headers: {\n" +
                    "                'Content-Type': 'text/plain'\n" +
                    "            },\n" +
                    "            redirect: 'follow', // manual, *follow, error\n" +
                    "            referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url\n" +
                    "            body: data // body data type must match \"Content-Type\" header\n" +
                    "        });\n" +
                    "        return response.json(); // parses JSON response into native JavaScript objects\n" +
                    "    }\n" +
                    "    const createProperty = function (obj) {\n" +
                    "        var label = document.createElement(\"li\");\n" +
                    "        label.innerText = obj.success ? obj.data : obj.err;\n" +
                    "        if (!obj.success) {\n" +
                    "            label.className = 'err';\n" +
                    "        }\n" +
                    "        return label\n" +
                    "    }\n" +
                    "    var ul1 = $(\"ul\");\n" +
                    "    const addResponse = (obj) => {\n" +
                    "        ul1.appendChild(createProperty(obj))\n" +
                    "    }\n" +
                    "\n" +
                    "    $(\"#send\").addEventListener('click', ev => {\n" +
                    "        let innerText = $(\"#article-input\").value;\n" +
                    "        console.log($(\"#article-input\"))\n" +
                    "        const task = async () => {\n" +
                    "            let message = await postData('/run', innerText);\n" +
                    "            console.log(message)\n" +
                    "            addResponse(message)\n" +
                    "        }\n" +
                    "        task()\n" +
                    "    })\n" +
                    "    $(\"#clear\").addEventListener('click', ev => {\n" +
                    "        ul1.innerHTML = ''\n" +
                    "    })\n" +
                    "</script>\n" +
                    "</body>\n" +
                    "</html>\n";
            return newFixedLengthResponse(msg);
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "Page Not Found");
        }


    }
}