package github.hotstu.rhino;

import org.junit.Test;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestJfram {
    @Test
    public void test1() throws InterruptedException {
        JFrame jFrame = new JFrame();
        jFrame.setSize(400, 400);
        JLabel label = new JLabel("hello,world", JLabel.CENTER);
        jFrame.add(label);
        jFrame.setVisible(true);
        Thread.sleep(5000);
    }

    @Test
    public void test2() {
        Pattern pattern = Pattern.compile("^/static/([\\s|\\S]*)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher("/static/hello/he.jpg");
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                System.out.println(matcher.group(i+1));
            }
        }


    }
}
