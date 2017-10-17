import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends JFrame implements Runnable{

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    private int counter;

    public Server(int port){
        super( "Server" );

        Container container = getContentPane();

        // create enterField and register listener
        enterField = new JTextField();
        enterField.setEditable( false );
        enterField.addActionListener(
                new ActionListener() {

                    // send message to client
                    public void actionPerformed( ActionEvent event )
                    {
                        sendData( event.getActionCommand() );
                        enterField.setText( "" );
                    }
                }
        );

        container.add( enterField, BorderLayout.NORTH );

        // create displayArea
        displayArea = new JTextArea();
        container.add( new JScrollPane( displayArea ),
                BorderLayout.CENTER );

        setSize( 300, 150 );
        setVisible( true );
        this.serverPort = port;
    }

    // send message to client
    private void sendData( String message )
    {
        // send object to client
        try {
            output.writeObject( "SERVER>>> " + message );
            output.flush();
            displayMessage( "\nSERVER>>> " + message );
        }

        // process problems sending object
        catch ( IOException ioException ) {
            displayArea.append( "\nError writing object" );
        }
    }

    private void displayMessage( final String messageToDisplay )
    {
        // display message from event-dispatch thread of execution
        SwingUtilities.invokeLater(
                new Runnable() {  // inner class to ensure GUI updates properly

                    public void run() // updates displayArea
                    {
                        displayArea.append( messageToDisplay );
                        displayArea.setCaretPosition(
                                displayArea.getText().length() );
                    }

                }  // end inner class

        ); // end call to SwingUtilities.invokeLater
    }

    public void run(){

        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            counter ++;
            System.out.print(counter);
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            this.threadPool.execute(
                    new WorkerRunnable(clientSocket,
                            "Thread Pooled Server", counter));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
//        this.isStopped = true;
//        try {
//            this.serverSocket.close();
//        } catch (IOException e) {
//            throw new RuntimeException("Error closing server", e);
//        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

}