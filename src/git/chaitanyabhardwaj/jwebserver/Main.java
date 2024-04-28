package git.chaitanyabhardwaj.jwebserver;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Run server on port: ");
        Scanner sc = new Scanner(System.in);
        runServer(sc.nextInt());
    }

    public static void runServer(int port) {
        new WebServer().start(port);
    }
}