import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientSocket {
    static Scanner scanner;
    static Socket socket;
    static DataOutputStream dataOutputStream;
    static DataInputStream dataInputStream;

    public static void main(String[] args) {
        try {

            scanner = new Scanner(System.in);
            InetAddress ip = InetAddress.getLocalHost();
            int port = 6660;
            socket = new Socket(ip, port);

            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            System.out.println(dataInputStream.readUTF());

            outerLoop: while (true) {
                System.out.println("1- Register\n2- Login\n3- Exit");

                String choice = scanner.next();
                dataOutputStream.writeUTF(choice);
                switch (choice) {
                    case "1": {
                        boolean flag = false;
                        while (!flag) {
                            System.out.println("Registration\n-------------");

                            System.out.println("Please enter your name: ");
                            String name = scanner.next();
                            dataOutputStream.writeUTF(name);

                            System.out.println("Please enter your username: ");
                            String username = scanner.next();
                            dataOutputStream.writeUTF(username);

                            System.out.println("Please enter your password: ");
                            String password = scanner.next();
                            dataOutputStream.writeUTF(password);

                            System.out.println(dataInputStream.readUTF());

                            flag = dataInputStream.readBoolean();
                        }
                        break;
                    }
                    case "2": {
                        String menu = null;
                        int loop = 0;
                        while (loop != 1) {
                            System.out.println("Login\n------");

                            System.out.println("Please enter your username: ");
                            String username = scanner.next();
                            dataOutputStream.writeUTF(username);

                            System.out.println("Please enter your password: ");
                            String password = scanner.next();
                            dataOutputStream.writeUTF(password);

                            loop = dataInputStream.read();

                            System.out.print(dataInputStream.readUTF());

                            menu = dataInputStream.readUTF();

                            System.out.print(menu);
                        }
                        do {
                            String ch = scanner.next();
                            dataOutputStream.writeUTF(ch);
                            switch (ch) {
                                case "1": {                       //single
                                    String gameChar;
                                    boolean flag;
                                    do {
                                        System.out.println(dataInputStream.readUTF());
                                        System.out.println(dataInputStream.readUTF());
                                        System.out.print(dataInputStream.readUTF());
                                        gameChar = scanner.next();
                                        dataOutputStream.writeUTF(gameChar);
                                        flag = dataInputStream.readBoolean();
                                    } while (flag);
                                    boolean correctPhrase = dataInputStream.readBoolean();
                                    if (correctPhrase)
                                        System.out.println(dataInputStream.readUTF());
                                    else {
                                        System.out.println(dataInputStream.readUTF());
                                    }
                                    break;
                                }
                                case "2": {                                          //multiplayer
                                    String innerLoop = dataInputStream.readUTF();
                                    System.out.println(innerLoop);
                                    while (innerLoop != null) {
                                        String multiPlayerChoice = scanner.next();
                                        dataOutputStream.writeUTF(multiPlayerChoice);

                                        switch (multiPlayerChoice) {
                                            case "1": {    //invite
                                                System.out.print(dataInputStream.readUTF());
                                                System.out.print(dataInputStream.readUTF());
                                                int numPlayers = dataInputStream.readInt();
                                                for (int i = 0; i < numPlayers; i++) {
                                                    System.out.println(dataInputStream.readUTF());
                                                }
                                                String teamMate = scanner.next();
                                                dataOutputStream.writeUTF(teamMate);

                                                if (teamMate.equals("-"))
                                                    break;
                                                else {
                                                    System.out.println(dataInputStream.readUTF());
                                                    if (!dataInputStream.readBoolean())
                                                        break;

                                                    System.out.println(dataInputStream.readUTF()); // Game Started


                                                    boolean flag;
                                                    boolean myTurn;
                                                    do {
                                                        System.out.println(dataInputStream.readUTF());
                                                        System.out.println(dataInputStream.readUTF());
                                                        myTurn = dataInputStream.readBoolean();
                                                        if (myTurn) {
                                                            System.out.println(dataInputStream.readUTF());
                                                            System.out.print(dataInputStream.readUTF());
                                                            String gameChar = scanner.next();
                                                            dataOutputStream.writeUTF(gameChar);
                                                        } else {
                                                            System.out.println(dataInputStream.readUTF());
                                                        }
                                                        flag = dataInputStream.readBoolean();
                                                    } while (flag);
                                                    System.out.println(dataInputStream.readUTF());
                                                    innerLoop = null;
                                                    break;
                                                }
                                            }
                                            case "2": {
                                                System.out.print(dataInputStream.readUTF());
                                                System.out.print(dataInputStream.readUTF());
                                                int numInvites = dataInputStream.readInt();
                                                for (int i = 0; i < numInvites; i++) {
                                                    System.out.println(dataInputStream.readUTF());
                                                }
                                                String teamMate = scanner.next();
                                                if (teamMate.equals("-")) {
                                                    dataOutputStream.writeUTF(teamMate);
                                                    break;
                                                } else {
                                                    dataOutputStream.writeUTF(teamMate);
                                                    System.out.println(dataInputStream.readUTF());
                                                    if (!dataInputStream.readBoolean())
                                                        break;

                                                    System.out.println(dataInputStream.readUTF()); // Game Started

                                                    boolean flag;
                                                    boolean myTurn;
                                                    do {
                                                        System.out.println(dataInputStream.readUTF());
                                                        System.out.println(dataInputStream.readUTF());
                                                        myTurn = dataInputStream.readBoolean();
                                                        if (myTurn) {
                                                            System.out.println(dataInputStream.readUTF());
                                                            System.out.print(dataInputStream.readUTF());
                                                            String gameChar = scanner.next();
                                                            dataOutputStream.writeUTF(gameChar);
                                                        } else {
                                                            System.out.println(dataInputStream.readUTF());
                                                        }
                                                        flag = dataInputStream.readBoolean();
                                                    } while (flag);
                                                    System.out.println(dataInputStream.readUTF());
                                                    innerLoop = null;
                                                    break;
                                                }
                                            }
                                            case "3": {
                                                innerLoop = null;
                                                break;
                                            }
                                            default:{
                                                System.out.println("ERROR: Wrong Option!");
                                                break;
                                            }
                                        }
                                        if (innerLoop != null) {
                                            System.out.println(innerLoop);
                                        }
                                    }
                                }
                                case "3": {
                                    break outerLoop;
                                }
                                default:{
                                    System.out.println("ERROR: Wrong Option!");
                                    break;
                                }
                            }
                            if (menu != null){
                                System.out.println(menu);
                            }
                        } while (menu != null);
                        break;
                    }
                    case "3": {
                        break;
                    }
                    default: {
                        System.out.println("ERROR: Wrong Option!");
                        break;
                    }
                }

                if (choice.equals("3")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (dataOutputStream != null)
                dataOutputStream.close();
        } catch (IOException e) {
        }
        try {
            if (dataInputStream != null)
                dataInputStream.close();
        } catch (IOException e) {
        }
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
        }

    }
}