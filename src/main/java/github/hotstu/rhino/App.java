package github.hotstu.rhino;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;

import java.io.*;
import java.util.HashMap;

public class App extends NanoHTTPD {

    private final Gson g;
    private final StaticHandler staticHandler;

    public App() throws IOException {
        super(8080);
        g = new Gson();
        staticHandler = new StaticHandler();
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
        } else if(uri.matches("^/reset$")){
            ScriptRunner.getInstance().resetScope();
            return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"success\": true}");
        } else if (uri.matches("^/$")) {
            System.out.println("显示页面");
            String msg = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Rhono Play Groud</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"./static/lib/codemirror.css\">\n" +
                    "    <link rel=\"stylesheet\" href=\"./static/theme/monokai.css\">\n" +
                    "    <script src=\"./static/lib/codemirror.js\"></script>\n" +
                    "    <script src=\"./static/mode/javascript/javascript.js\"></script>\n" +
                    "    <script src=\"./static/addon/selection/active-line.js\"></script>\n" +
                    "    <script src=\"./static/addon/edit/matchbrackets.js\"></script>\n" +
                    "    <style>\n" +
                    "        .CodeMirror {\n" +
                    "            height: 480px;\n" +
                    "        }\n" +
                    "        li.err {\n" +
                    "            color: tomato;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div>\n" +
                    "    <div>\n" +
                    "        <textarea id=\"code\" style=\"height: 480px\">\n" +
                    "        </textarea>\n" +
                    "    </div>\n" +
                    "    <div style=\"display: flex\">\n" +
                    "        <button id=\"send\" style=\"flex: 1\">发送</button>\n" +
                    "        <button id=\"clear\" style=\"flex: 1\">清除日志</button>\n" +
                    "        <button id=\"reset\" style=\"flex: 1\">重置scope</button>\n" +
                    "    </div>\n" +
                    "    <div style=\"height: 600px; overflow-y: auto\">\n" +
                    "        <ul></ul>\n" +
                    "    </div>\n" +
                    "\n" +
                    "</div>\n" +
                    "\n" +
                    "<script>\n" +
                    "    const $ = document.querySelector.bind(document);\n" +
                    "    const editor = CodeMirror.fromTextArea($(\"#code\"), {\n" +
                    "        lineNumbers: true,\n" +
                    "        styleActiveLine: true,\n" +
                    "        matchBrackets: true,\n" +
                    "        theme: 'monokai',\n" +
                    "        \"extraKeys\": {\n" +
                    "            'Ctrl-Enter': function () {\n" +
                    "                $(\"#send\").dispatchEvent(new Event('click'))\n" +
                    "            }\n" +
                    "        }\n" +
                    "    });\n" +
                    "    editor.setValue(\"importPackage(Packages.javax.swing);\\n\" +\n" +
                    "        \"const jFrame = new JFrame()\\n\" +\n" +
                    "        \"jFrame.setSize(400, 400)\\n\" +\n" +
                    "        \"const label = new JLabel(\\\"hello,world\\\", JLabel.CENTER)\\n\" +\n" +
                    "        \"jFrame.add(label)\\n\" +\n" +
                    "        \"jFrame.setVisible(true)\")\n" +
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
                    "\n" +
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
                    "        //console.log(editor)\n" +
                    "        const task = async () => {\n" +
                    "            let message = await postData('/run', editor.getValue());\n" +
                    "            console.log(message)\n" +
                    "            addResponse(message)\n" +
                    "        }\n" +
                    "        task()\n" +
                    "    })\n" +
                    "    $(\"#clear\").addEventListener('click', ev => {\n" +
                    "        ul1.innerHTML = ''\n" +
                    "    })\n" +
                    "    $(\"#reset\").addEventListener('click', ev => {\n" +
                    "        const task = async () => {\n" +
                    "            let message = await postData('/reset', {});\n" +
                    "            console.log(message)\n" +
                    "            addResponse(message)\n" +
                    "        }\n" +
                    "        task()\n" +
                    "    })\n" +
                    "</script>\n" +
                    "</body>\n" +
                    "</html>\n";
            return newFixedLengthResponse(msg);
        } else if (staticHandler.isHandle(session)) {
            return staticHandler.handle(session);
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "Page Not Found");
        }


    }
}