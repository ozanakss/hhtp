
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;




public class Server_Main {
    static final int port = 4569;
    private Socket socket;

    private nachrichtspeichern nachrichtspeichern;


    public static void main(String[] args) {
        System.out.println("the connection between server and client is established");
        System.out.println("server on port "+port);
        new Server_Main(port);
    }




    public Server_Main (int port){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);

            this.nachrichtspeichern = new nachrichtspeichern();

            while (true){
                this.socket = serverSocket.accept();
                anfragerespon();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void anfragerespon(){
        try{
            System.out.println("*******************************started*******************************");
            Request rq = new Request(this.socket);
            HTTP_REQUEST_RESPONSE rp = new HTTP_REQUEST_RESPONSE( rq.getURLCODIEREN(), rq.getCmdHttpRequest(), rq.getOutput(), this.nachrichtspeichern, rq.getPayload());
            this.nachrichtspeichern = rp.getNachrichtspeichern();
            this.socket.close();
            System.out.println("*******************************closed*******************************");
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Ein Fehler ist aufgetreten");
        }
    }
}
