import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class HTTP_REQUEST_RESPONSE {   // Respons klasse :D

    String       URLCODIEREN;
    nachrichtspeichern  nachrichtspeichern;
    String       CmdHttpRequest ;
    StringBuilder payloadBuilder;
     PrintStream Output;


    public HTTP_REQUEST_RESPONSE(String URLCODIEREN, String CmdHttpRequest, PrintStream Output, nachrichtspeichern nachrichtspeichern, String payload){
        this.CmdHttpRequest = CmdHttpRequest;
        this.Output = Output;
        this.nachrichtspeichern = nachrichtspeichern;
        this.URLCODIEREN = URLCODIEREN;
        this.payloadBuilder = new StringBuilder();
        System.out.println(CmdHttpRequest);
        if (this.URLCODIEREN != null) {
            // GET METHODE +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            if (this.CmdHttpRequest.equals("GET")) {
                if (this.URLCODIEREN.startsWith("/messages")) {
                    String header_ = this.URLCODIEREN.substring(this.URLCODIEREN.lastIndexOf('/') + 1);
                    System.out.println("-------->"+ header_+"<--------");
                    System.out.println();

                    if(header_.equals("messages")){
                        messageauflisten();
                        System.out.println("Die Nachrichten werden aufgelistet");
                    }else{
                        String message = nachrichtspeichern.getNachricht_(Integer.parseInt(header_)).getnachricht();
                        System.out.println("die Nachricht, die  aufgerufen wurde "+message);
                        if(message == null){
                            sendError("404");
                            System.out.println(" 404 Die angeforderte Ressource wurde nicht gefunden.Not Found");
                        }else {
                            sendResponse(message, "200");
                            System.out.println(" 200 OK.Die Anfrage wurde erfolgreich bearbeitet und das Ergebnis der Anfrage wird in der Antwort übertragen.");
                        }
                    }
                } else if (this.URLCODIEREN.startsWith("/")) {
                    KEINURL_();
                }
                // POST METHODE +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            }else if (this.CmdHttpRequest.equals("POST")){
                if (this.URLCODIEREN.startsWith("/messages")) {
                    sendResponse(nachrichtspeichern.hinzufugen(payload) + ""+payload, "201");
                    System.out.println(" 201 Created.Die Anfrage wurde erfolgreich bearbeitet. Die angeforderte Ressource wurde vor dem Senden der Antwort erstellt. ");
                }
                // PUT METHODE +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

            }else if (this.CmdHttpRequest.equals("PUT")){
                if (this.URLCODIEREN.startsWith("/messages")) {
                    String lastBit = this.URLCODIEREN.substring(this.URLCODIEREN.lastIndexOf('/') + 1);
                    System.out.println("Payload: " + payload);
                    System.out.println("Die Nachricht mit dieser ID "+ lastBit +"wurde geändert: ");
                    String message = nachrichtspeichern.bearbeit(Integer.parseInt(lastBit), payload);
                    if(message == null){
                        sendError("404");
                        System.out.println(" 404 Not Found.Die angeforderte Ressource wurde nicht gefunden.");

                    }else {
                        sendResponse("","200");
                        System.out.println(" 200OK.Die Anfrage wurde erfolgreich bearbeitet und das Ergebnis der Anfrage wird in der Antwort übertragen.");
                    }
                }
                // DELETE METHODE +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            }else if (this.CmdHttpRequest.equals("DELETE")){
                if (this.URLCODIEREN.startsWith("/messages")) {
                    String lastBit = this.URLCODIEREN.substring(this.URLCODIEREN.lastIndexOf('/') + 1);
                    String message = nachrichtspeichern.losch(Integer.parseInt(lastBit));
                    if(message == null){
                        sendError("404");
                        System.out.println("404 Not Found.Die angeforderte Ressource wurde nicht gefunden.");
                    }else {
                        sendResponse("Ok 200 ", "200");
                        System.out.println("200 OK.Die Anfrage wurde erfolgreich bearbeitet und das Ergebnis der Anfrage wird in der Antwort übertragen.");

                    }
                }
            }else{
                sendError("405");
                System.out.println("405 Method Not Allowed.Die Anfrage darf nur mit anderen HTTP-Methoden (zum Beispiel GET statt POST) gestellt werden.");
            }
        }
    }



    private void sendError(String errorCode) {
        Output.print("HTTP/1.0 "+errorCode+"\r\n");
        Output.print("Content-Type: text/plain\r\n");
        Output.print("Content-Length: 1\r\n");
        //out.print(responseText);
    }

    private void KEINURL_() {
        sendResponse("Du musst eine Anfragemethode(GET POST usw.)auswählen ", "200");
    }

    private void messageauflisten() {
        sendResponse(nachrichtspeichern.AlleNachrichtenget(), "200");
    }


    private void sendResponse(String responseText, String code){
        Output.print("HTTP/1.0 "+code+"\r\n");
        Output.print("Server: localhost\r\n");
        Output.print("Content-Type: text/plain\r\n");
        Output.print("Content-Length: "+responseText.length()+"\r\n");
        Output.print("\r\n");
        Output.print(responseText);
    }


    public nachrichtspeichern getNachrichtspeichern() {
        return this.nachrichtspeichern;
    }

}


 class Request {

    private final Socket _socket_;
    private PrintStream Output;
    private String       CmdHttpRequest;
    private String       URLCODIEREN;
    private final StringBuilder payloadBuilder;
    private String thepayload;



    public Request(Socket _socket_) throws IOException {
        this. _socket_ = _socket_;
        this.payloadBuilder = new StringBuilder();

        this.Output = new PrintStream(this. _socket_.getOutputStream());
        BufferedReader _bufferedReader_ = new BufferedReader(new InputStreamReader(_socket_.getInputStream()));
        String line =  _bufferedReader_.readLine();
        while (!line.isBlank()) {
            payloadBuilder.append(line + "\r\n");
            line =  _bufferedReader_.readLine();
            System.out.println(line);
        }
        String request = payloadBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];


        StringBuilder thepayload = new StringBuilder();
        while( _bufferedReader_.ready()){
            thepayload.append((char)  _bufferedReader_.read());
        }
        System.out.println("Payload: " + thepayload.toString());
        this.thepayload = thepayload.toString();

        this.URLCODIEREN = path;
        this.CmdHttpRequest = method;

        List<String> allheader = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            allheader.add(header);
        }

        String accessLog = String.format("Client %s, methode %s, pfad %s, dieversion %s,",
                _socket_.toString(), method, path, version, host, allheader.toString());
        System.out.println(accessLog);
    }




    public PrintStream getOutput() {
        return this.Output;
    }

    public String getCmdHttpRequest() {
        return this.CmdHttpRequest;
    }


    public String getURLCODIEREN() {
        return this.URLCODIEREN;
    }


    public String getPayload() {
        return this.thepayload;
    }

}

 class Nachricht_ {
    private int id;
    private String nachricht;



    public Nachricht_(int id, String nachricht){
        this.id = id;
        this.nachricht = nachricht;
    }


    //**GETTER**/

    public int getId() {
        return this.id;
    }

    public String getnachricht() {
        return this.nachricht;
    }

}
 class nachrichtspeichern {
    private HashMap<Integer , String> msgMap;
    private int letzteidnummer = 0; // beginnt mit 0


    public nachrichtspeichern(){
        msgMap = new HashMap<Integer, String>();


    }

    private int nachste(){
        return this.letzteidnummer + 1;
    }

    public int hinzufugen(String msg){
        int id = nachste();
        msgMap.put(id, msg);
        this.letzteidnummer = id;
        return id;
    }

    public String losch(int id){
        return msgMap.remove(id);
    }

    public String bearbeit(int id, String msg){
        return msgMap.replace(id, msg);
    }

    public Nachricht_ getNachricht_(int id){
        return new Nachricht_(id, msgMap.get(id));
    }

    public String AlleNachrichtenget(){
        String returnStr = "";
        // Print keys and values
        for (Integer i : msgMap.keySet()) {
            String item =  i + "Nachricht: " + " : " + msgMap.get(i) + "\n";
            returnStr += item;
        }
        System.out.println(returnStr);
        return returnStr;
    }
}
