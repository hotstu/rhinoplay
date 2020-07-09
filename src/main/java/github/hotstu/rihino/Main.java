package github.hotstu.rihino;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }
}
