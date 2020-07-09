package github.hotstu.rhino;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;

public class TestJfram {
    @Test
    public void test1() throws InterruptedException {
        JFrame jFrame = new JFrame();
        jFrame.setSize(400, 400);
        final TextField textField = new TextField();
        textField.setText("hello,world");
        jFrame.add(textField);
        jFrame.setVisible(true);
        Thread.sleep(5000);
    }
}
