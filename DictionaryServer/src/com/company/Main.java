//1.Ermiyas Gezahegn… ATR /5552/09 ermiyas10080@gmail.com
// 2.Fasil Beshiwork … ATR/9359/09 …. fasilbeshiwork17@gmail.com
// 3. Feysel Mubarek … ATR/5064/09…feyselmubarek@gmail.com
// 4.Habte Assefa… ATR/0081/09…. habteasefa726@gmail.com
// 5. Hana Tesfaye.…. ATR/4224/09…. hanatesfaye223@gmail.com

package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5056);

        FileUtilities fileUtilities = new FileUtilities();
        fileUtilities.populateTable();

        while (true) {
            Socket s = null;

            try {
                System.out.println("Waiting for new Client");
                s = ss.accept();
                System.out.println("A new client has connected");

                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                Thread t = new ClientHandler(s, dis, dos, fileUtilities);
                t.start();

            } catch (Exception e) {
                s.close();
                ss.close();
                e.printStackTrace();
            }
        }
    }
}

class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private String data;
    private FileUtilities fileUtilities;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, FileUtilities fileUtilities) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.fileUtilities = fileUtilities;
    }

    @Override
    public void run() {
        String received;
        String toreturn;

        try {
            data = fileUtilities.getDictionaryData();
            dos.writeUTF(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                received = dis.readUTF();

                String d[] = received.split("`");
                String operation = d[0];

                if (received.equals("Exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                switch (operation) {
                    case "1":
                        toreturn = fileUtilities.addWord(d[1], d[2]);
                        dos.writeUTF(toreturn);
                        break;
                    case "2":
                        toreturn = fileUtilities.addDefinition(d[1], d[2]);
                        dos.writeUTF(toreturn);
                        break;
                    case "3":
                        toreturn = fileUtilities.removeWord(d[1]);
                        dos.writeUTF(toreturn);
                        break;
                    case "4":
                        toreturn = fileUtilities.get(d[1]);
                        dos.writeUTF(toreturn);
                        break;
                    case "g":
                        toreturn = fileUtilities.getDictionaryData();
                        dos.writeUTF(toreturn);
                        break;
                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {

            this.dis.close();
            this.dos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
