# JWebServer
A simple implementation of a web server, in Java. This application can host websites, static content, scripts, etc...

<img width="550" alt="Screenshot 2024-04-28 at 6 54 17 AM" src="https://github.com/chaitanyabhardwaj/JWebServer/assets/17910338/a81cd183-5297-4ca6-8159-587302865bc9">


## How to run it?
1. Clone the repository in your local.
2. Open a terminal/powershell in JWebServer directory 
3. Complie the java source files.<br>
   `javac -d src/target src/**/*.java`
4. Create a jar out of the complied files.<br>
   `jar cvf JWebServer.jar src/target/**/*.class`
5. Run the jar file by specifying the main class.<br>
   `java -cp JWebServer.jar:src/target git.chaitanyabhardwaj.jwebserver.Main`
6. This should start the application. Enter a desired port number (say, 5000).
7. Open a web client(any web broswer or postman) and hit _http://127.0.0.1:5000_ or _http://localhost:5000_ <br>
Congratulations🎉, your web server is now running!
