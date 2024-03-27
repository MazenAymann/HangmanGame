import java.net.*;
import java.io.*;
import java.util.ArrayList;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    int clientID;
    static int ID = 0;

    boolean inviteAccepted = false;

    boolean multiplayerTurn = false;

    ArrayList<String> inBox = new ArrayList<>();

    public ClientHandler(Socket socket) throws IOException {
        clientID = ++ID;
        this.clientSocket = socket;
        this.inputStream = new DataInputStream(clientSocket.getInputStream());
        this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void run() {
        try {
            outputStream.writeUTF("Welcome to the the game!");

            while (true) {
                String clientMessage = inputStream.readUTF();
                System.out.println("Received message from " + clientSocket.getInetAddress() + ": " + clientMessage);

                if (clientMessage.equals("3")) {
                    outputStream.writeUTF("Goodbye!");
                    break;
                }

                try {
                    outter: switch (clientMessage) {
                        case "1": {
                            while (true) {
                                String clientName = inputStream.readUTF();
                                System.out.println("Received message from " + clientSocket.getInetAddress() + ": " + clientName);

                                String clientUsername = inputStream.readUTF();
                                System.out.println("Received message from " + clientSocket.getInetAddress() + ": " + clientUsername);

                                String clientPassword = inputStream.readUTF();
                                System.out.println("Received message from " + clientSocket.getInetAddress() + ": " + clientPassword);

                                if (ServerSocket.addClient(clientName, clientUsername, clientPassword)) {
                                    outputStream.writeUTF("Registration Successfully!");
                                    outputStream.writeBoolean(true);
                                    break;
                                } else {
                                    outputStream.writeUTF("ERROR: Username already taken!");
                                    outputStream.writeBoolean(false);
                                }
                            }
                            break;
                        }
                        case "2": {
                            int validate = 0;
                            while (validate != 1) {
                                String clientUsername = inputStream.readUTF();
                                System.out.println("Received message from " + clientSocket.getInetAddress() + ": " + clientUsername);

                                String clientPassword = inputStream.readUTF();
                                System.out.println("Received message from " + clientSocket.getInetAddress() + ": " + clientPassword);

                                if ((validate = ServerSocket.validateClientCredentials(clientUsername, clientPassword, this)) == 1) {
                                    outputStream.write(validate);
                                    outputStream.writeUTF("Login Successfully!\n" + "--------------------\n");
                                    outputStream.writeUTF("1- Single Player\n2- Multiplayer\n3- Exit\n");
                                } else if (validate == 401) {
                                    outputStream.write(validate);
                                    outputStream.writeUTF("ERROR 401: ");
                                    outputStream.writeUTF("Wrong Password!");
                                } else if (validate == 404) {
                                    outputStream.write(validate);
                                    outputStream.writeUTF("ERROR 404: ");
                                    outputStream.writeUTF("Wrong Username!");
                                }
                            }
                            String GMC = null;
                            do {
                                String gameModeChoice = "";
                                gameModeChoice = inputStream.readUTF();
                                GMC = gameModeChoice;
                                switch (gameModeChoice) {
                                    case "1": {
                                        ServerSocket.hangmanGameSinglePlayer(this);
                                        break;
                                    }
                                    case "2": {
                                        boolean loop = false;
                                        outputStream.writeUTF("1- Invite a player\n2- Receive an invite\n3- Exit");

                                        while (!loop) {
                                            String multiPlayerChoice = "";
                                            multiPlayerChoice = inputStream.readUTF();
                                            System.out.println(multiPlayerChoice);
                                            switch (multiPlayerChoice) {
                                                case "1": {
                                                    outputStream.writeUTF("Please Choose a Player, enter his username! \n");
                                                    outputStream.writeUTF("Press '-' to return back to menu. \n");
                                                    outputStream.writeInt(ServerSocket.playableUsers(this).size());
                                                    for (int i = 0; i < ServerSocket.playableUsers(this).size(); i++) {
                                                        outputStream.writeUTF(i + 1 + "- " + ServerSocket.playableUsers(this).get(i));
                                                    }
                                                    String teamMate = inputStream.readUTF();
                                                    if (teamMate.equals("-"))
                                                        break;
                                                    else {
                                                        loop = true;
                                                        if (ServerSocket.gameInvitationMSG(this, teamMate)) {
                                                            outputStream.writeUTF("Waiting for other players.");
                                                            outputStream.writeBoolean(true);
                                                            do {
                                                                if (inviteAccepted) {
                                                                    while (ServerSocket.teams.size() != ServerSocket.maxPlayers) {
                                                                        
                                                                    }
                                                                    ServerSocket.hangmanGameMultiPlayer(ServerSocket.teams.get("Team1"),
                                                                            ServerSocket.teams.get("Team2"));
                                                                    break outter;
                                                                }
                                                            } while (true);
                                                        } else {
                                                            outputStream.writeUTF("ERROR: Invitation Failed");
                                                            outputStream.writeBoolean(false);
                                                            loop = false;
                                                        }
                                                        break;
                                                    }
                                                }
                                                case "2": {
                                                    outputStream.writeUTF("Please Choose an invite, enter his username! \n");
                                                    outputStream.writeUTF("Press '-' to return back to menu. \n");
                                                    outputStream.writeInt(ServerSocket.getInbox(this).size());
                                                    for (int i = 0; i < ServerSocket.getInbox(this).size(); i++) {
                                                        outputStream.writeUTF(i + 1 + "- " + ServerSocket.getInbox(this).get(i));
                                                    }
                                                    String teamMate = inputStream.readUTF();
                                                    if (teamMate.equals("-"))
                                                        break;
                                                    else {
                                                        loop = true;
                                                        if (ServerSocket.invitationAccept(this, teamMate)) {
                                                            outputStream.writeUTF("Waiting for other players.");
                                                            outputStream.writeBoolean(true);
                                                            while (true){

                                                            }
                                                        } else {
                                                            outputStream.writeUTF("ERROR: Accept Failed");
                                                            outputStream.writeBoolean(false);
                                                            loop = false;
                                                        }
                                                        break;
                                                    }
                                                }
                                                case "3": {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    case "3": {
                                        //SocketServer.userLogout(this);
                                        break;
                                    }
                                    default: {
                                        break;
                                    }
                                }
                            } while (!GMC.equals("3"));
                            break;
                        }
                        case "3": {
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    outputStream.writeUTF("Invalid input. Please enter a number or 'quit'.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (IOException e) {
            ServerSocket.userLogout(this);
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        }
        
        ServerSocket.userLogout(this);
    }
}