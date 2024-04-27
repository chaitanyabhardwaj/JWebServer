import git.chaitanyabhardwaj.jwebserver.WebServer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException, Exception {
        System.out.print("Run server on port: ");
        Scanner sc = new Scanner(System.in);
        runServer(sc.nextInt());
    }

    public static void runServer(int port) {
        new WebServer().start(port);
    }
}