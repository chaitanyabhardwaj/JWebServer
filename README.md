# JWebServer
A simple implementation of a web server, in Java. This application can host websites, static content, scripts, etc...

## How to run it?
1. Clone the repository in your local.
2. Complie the java source files.<br>
   `javac -d src/target src/**/*.java`
3. Create a jar out of the complied files.<br>
   `jar cvf JWebServer.jar src/target/**/*.class`
4. Run the jar file by specifying the main class.<br>
   `java -cp JWebServer.jar:src/target git.chaitanyabhardwaj.jwebserver.Main`
5. This should start the java application. Enter the desired port number.
6. Open a web client(any web broswer or postman) and hit _http://127.0.0.1:5000_ or _http://localhost:5000_ <br>
Congratulations🎉, your web server is now running!
