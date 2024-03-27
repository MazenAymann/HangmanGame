import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardOpenOption.*;

public class ServerSocket {

    static ArrayList<String> allClients = new ArrayList<>();

    static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    static ArrayList<String> loggedInUsers = new ArrayList<>();

    static int numTeams = 0;

    static int numAttempts = 0;

    static ArrayList<String> teamNames = new ArrayList<>();

    static int maxPlayers = 0;

    static int minPlayers = 0;

    static HashMap<String, ArrayList<ClientHandler>> teams = new HashMap<>();

    public static void main(String[] args) throws IOException {

        File dataFile = new File("data.txt");
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }

        File sentenceFile = new File("sentences.txt");
        if (!sentenceFile.exists()) {
            sentenceFile.createNewFile();
        }

        File configFile = new File("config.txt");
        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        numAttempts = getConfigurations("config.txt")[0];
        minPlayers = getConfigurations("config.txt")[1];
        maxPlayers = getConfigurations("config.txt")[2];

        java.net.ServerSocket serverSocket = new java.net.ServerSocket(6660);
        System.out.println("Server started. Listening on port 6660...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            new Thread(clientHandler).start();
            String ID = String.valueOf(clientHandler.clientID);
            clientHandlers.add(clientHandler); // Index = ID -1
            allClients.add(ID);
            System.out.println(getClients());
        }
    }

    public static int[] getConfigurations(String filePath) throws IOException {
        int[] configs = new int[3];
        String[] configLine = new String[3];

        String line = "";
        Path path = Paths.get(filePath);

        InputStream inputStream = Files.newInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while ((line = bufferedReader.readLine()) != null) {
            configLine = line.split(",");
        }

        for (int i = 0; i < 3; i++) {
            configs[i] = Integer.valueOf(configLine[i]);
        }
        return configs;
    }

    public static ArrayList<String> getClients() {
        return allClients;
    }

    public static ClientHandler getClientHandler(String username) {
        for (int i = 0; i < allClients.size(); i++) {
            if (username.equals(allClients.get(i))) {
                return clientHandlers.get(i);
            }
        }
        return null;
    }

    public static ClientHandler getClientHandler(int id) {
        return clientHandlers.get(id - 1);
    }

    public static String getClientUsername(ClientHandler clientHandler) {
        return allClients.get(clientHandler.clientID - 1);
    }

    public static boolean addClient(String name, String username, String password) throws IOException {
        String line;
        boolean usernameFound = false;
        boolean processSuccessfully = false;

        Path path = Paths.get("data.txt");
        InputStream inputStream = Files.newInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while ((line = bufferedReader.readLine()) != null) {
            String[] account = line.split(",");
            String userName = account[1];
            if (userName.equals(username)) {
                usernameFound = true;
                break;
            }
        }

        if (usernameFound) {
            processSuccessfully = false;

        } else {
            OutputStream outputStream = Files.newOutputStream(path, APPEND);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(name + "," + username + "," + password + "," + 0);

            bufferedWriter.newLine();
            bufferedWriter.close();

            processSuccessfully = true;
        }
        return processSuccessfully;
    }

    public static String getClientName(ClientHandler clientHandler) throws IOException {
        String line = "";
        Path path = Paths.get("data.txt");
        String userName = getClientUsername(clientHandler);

        InputStream inputStream = Files.newInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while ((line = bufferedReader.readLine()) != null) {
            String[] account = line.split(",");
            if (account[1].equals(userName)) {
                return account[0];
            }
        }
        return null;
    }

    public static int validateClientCredentials(String username, String password, ClientHandler clientHandler) throws IOException {
        String line;
        boolean usernameFound = false;
        boolean passwordFound = false;
        int processSuccessfully = 1;

        Path path = Paths.get("data.txt");
        InputStream inputStream = Files.newInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            String[] account = line.split(",");
            String userName = account[1];
            String passWord = account[2];
            if (passWord.equals(password))
                passwordFound = true;
            if (userName.equals(username)) {
                usernameFound = true;
                break;
            }
        }
        if (!usernameFound) {
            processSuccessfully = 404;
        }
        if (usernameFound && !passwordFound) {
            processSuccessfully = 401;
        }
        if (usernameFound && passwordFound) {
            processSuccessfully = 1;
            int ID = clientHandler.clientID;
            allClients.set(ID - 1, username);
            loggedInUsers.add(username);
        }
        return processSuccessfully;
    }

    public static String getRandomSentence(String path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Random random = new Random();
        return lines.get(random.nextInt(lines.size()));
    }

    public static void editScore(String username, int newScore) throws IOException {
        String line = "";
        int userScore = 0;
        Path path = Paths.get("data.txt");

        InputStream inputStream = Files.newInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        OutputStream outputStream = Files.newOutputStream(path, WRITE);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

        while ((line = bufferedReader.readLine()) != null) {
            String[] account = line.split(",");
            String userName = account[1];
            if (userName.equals(username)) {
                userScore = Integer.valueOf(account[3]);
                userScore += newScore;
                account[3] = String.valueOf(userScore);
                line = account[0] + "," + account[1] + "," + account[2] + "," + account[3];
            }
            bufferedWriter.write(line + System.getProperty("line.separator"));
        }
        bufferedWriter.close();
    }

    public static ArrayList<String> playableUsers(ClientHandler clientHandler) {
        ArrayList<String> users = new ArrayList<>();
        for (int i = 0; i < loggedInUsers.size(); i++) {
            if (!allClients.get(clientHandler.clientID - 1).equals(loggedInUsers.get(i))) {
                users.add(loggedInUsers.get(i));
            }
        }
        return users;
    }

    public static boolean gameInvitationMSG(ClientHandler clientHandler, String username) throws IOException {
        for (int i = 0; i < allClients.size(); i++) {
            if (username.equals(allClients.get(i))) {
                clientHandlers.get(i).inBox.add(allClients.get(clientHandler.clientID - 1) + " invited you to play with him. ");
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> getInbox(ClientHandler clientHandler) {
        return clientHandler.inBox;
    }

    public static boolean invitationAccept(ClientHandler clientHandler, String username) {

        for (int i = 0; i < allClients.size(); i++) {
            if (username.equals(allClients.get(i))) {
                numTeams++;
                ArrayList<ClientHandler> players = new ArrayList<>();
                players.add(clientHandlers.get(i));
                players.add(clientHandler);
                teams.put("Team" + numTeams, players);
                teamNames.add("Team" + numTeams);
                getClientHandler(username).inviteAccepted = true;
                return true;
            }
        }
        return false;
    }

    public static ArrayList<ClientHandler> getTeamPlayers(String teamName) {
        return teams.get(teamName);
    }

    public static String getTeamNameOfPlayer(ClientHandler clientHandler) {
        for (int i = 0; i < teamNames.size(); i++) {
            for (int j = 0; j < maxPlayers; j++) {
                if (teams.get(teamNames.get(i)).get(j).equals(clientHandler))
                    return teamNames.get(i);
            }
        }
        return null;
    }

    public static ArrayList<char[]> formattedRandomPhrase() throws Exception {
        ArrayList<char[]> formattedWord = new ArrayList<>();
        String word = getRandomSentence("sentences.txt");
        System.out.println("Word is: " + word);
        char[] chars = word.toCharArray();
        char[] charsHidden = word.toCharArray();
        for (int i = 0; i < charsHidden.length; i++) {
            if (charsHidden[i] != ' ') {
                charsHidden[i] = '_';
            }
        }
        formattedWord.add(chars);
        formattedWord.add(charsHidden);
        return formattedWord;
    }

    public static void hangmanGameSinglePlayer(ClientHandler clientHandler) throws Exception {

        ArrayList<char[]> formattedRandomPhrase = formattedRandomPhrase();
        ArrayList<Character> prevChars = new ArrayList<>();
        prevChars.add('0');

        char[] chars = formattedRandomPhrase.get(0);
        char[] charsHidden = formattedRandomPhrase.get(1);
        char ch = 0;
        int numGuess = 0;
        int userScore = 0;
        boolean correctPhrase = false;
        boolean correctGuess, flag;

        try {
            do {
                correctGuess = false;
                clientHandler.outputStream.writeUTF(String.valueOf(charsHidden) + "   Attempts: "
                        + numGuess + "/" + numAttempts + "   Score: "
                        + userScore);
                clientHandler.outputStream.writeUTF("Guess a letter: ");
                clientHandler.outputStream.writeUTF(getClientName(clientHandler) + ": ");
                ch = clientHandler.inputStream.readUTF().charAt(0);


                for (int i = 0; i < chars.length; i++) {
                    if (Character.toLowerCase(ch) == Character.toLowerCase(chars[i])) {
                        boolean notFound = true;
                        for (int j = 0; j < prevChars.size(); j++) {
                            if (Character.toLowerCase(ch) == Character.toLowerCase(prevChars.get(j))) {
                                notFound = false;
                            }
                        }
                        if (notFound) {
                            charsHidden[i] = chars[i];
                            correctGuess = true;
                            userScore += 10;
                            editScore(getClientUsername(clientHandler), 10);
                        }
                    }
                }
                prevChars.add(ch);
                if (!correctGuess)
                    numGuess++;
                if (Arrays.equals(chars, charsHidden))
                    correctPhrase = true;

                flag = !correctPhrase && ch != '-' && numGuess <= numAttempts;
                clientHandler.outputStream.writeBoolean(flag);
            } while (flag);
            clientHandler.outputStream.writeBoolean(correctPhrase);
            if (correctPhrase)
                clientHandler.outputStream.writeUTF("YOU ARE A WINNER!!!");
            else {
                clientHandler.outputStream.writeUTF("YOU LOST!!! The Correct Phrase: " + String.valueOf(chars));
            }
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static void hangmanGameMultiPlayer(ArrayList<ClientHandler> team1, ArrayList<ClientHandler> team2) throws Exception {

        ArrayList<char[]> formattedRandomPhrase = formattedRandomPhrase();
        ArrayList<Character> prevChars = new ArrayList<>();
        prevChars.add('0');

        char[] chars = formattedRandomPhrase.get(0);
        char[] charsHidden = formattedRandomPhrase.get(1);
        char ch = 0;

        int[] numGuess = new int[]{0, 0, 0, 0};
        int[] teamScore = new int[]{0, 0, 0, 0};
        boolean[] correctPhrase = new boolean[]{false, false, false, false};
        boolean correctGuess, flag = true;
        int winnerTeam = -1;
        boolean additionalTurn = true;
        int isQuit = -1;

        String winnerMsg = "Y O U  W O N !!!  The Correct Phrase: " + String.valueOf(chars);
        String loserMsg = "Y O U  L O S T !!!  The Correct Phrase: " + String.valueOf(chars);
        String drawMsg = "Y O U  D R A W !!!  The Correct Phrase: " + String.valueOf(chars);

        ArrayList<ClientHandler> players = new ArrayList<>();

        for (int i = 0; i < team1.size(); i++) {
            players.add(team1.get(i));
            players.add(team2.get(i));
        }

        for (int i = 0; i < team1.size() * 2; i++) {
            players.get(i).outputStream.writeUTF("G A M E  S T A R T E D");
        }

        game:
        do {
            for (int i = 0; i < team1.size() * 2; i++) {
                if (numGuess[i] > 7)
                    continue;
                players.get(i).multiplayerTurn = true;

                for (int j = 0; j < team1.size() * 2; j++) {
                    players.get(j).outputStream.writeUTF(getClientName(players.get(i)) + "'s Turn");
                    players.get(j).outputStream.writeUTF(String.valueOf(charsHidden) + "   Attempts: "
                            + numGuess[i] + "/" + numAttempts + "   Score: "
                            + teamScore[i]);
                    players.get(j).outputStream.writeBoolean(players.get(j).multiplayerTurn);
                }
                correctGuess = false;
                players.get(i).outputStream.writeUTF("Guess a letter: ");
                players.get(i).outputStream.writeUTF(getClientName(players.get(i)) + ": ");
                ch = players.get(i).inputStream.readUTF().charAt(0);
                System.out.println(getClientName(players.get(i)) + ": " + ch);

                for (int j = 0; j < chars.length; j++) {
                    if (Character.toLowerCase(ch) == Character.toLowerCase(chars[j])) {
                        boolean notFound = true;
                        for (int k = 0; k < prevChars.size(); k++) {
                            if (Character.toLowerCase(ch) == Character.toLowerCase(prevChars.get(k))) {
                                notFound = false;
                            }
                        }
                        if (notFound) {
                            charsHidden[j] = chars[j];
                            correctGuess = true;
                            teamScore[i] += 10;
                            editScore(getClientUsername(players.get(i)), 10);
                        }
                    }
                }
                prevChars.add(ch);
                if (!correctGuess)
                    numGuess[i]++;

                for (int j = 0; j < team1.size() * 2; j++) {
                    if (j != i) {
                        players.get(j).outputStream.writeUTF(getClientName(players.get(i)) + " entered: " + ch);
                    }
                }
                if (ch == '-') {
                    for (int j = 0; j < team1.size() * 2; j++) {
                        players.get(j).outputStream.writeBoolean(false);
                    }
                    isQuit = i;
                    break game;
                }


                if (Arrays.equals(chars, charsHidden)) {
                    correctPhrase[i] = true;
                    if (i == 0 || i == 2)
                        winnerTeam = 0;
                    else
                        winnerTeam = 1;
                }
                if (numGuess[0] > 7 && numGuess[2] > numAttempts) {
                    additionalTurn = false;
                }
                if (numGuess[1] > 7 && numGuess[3] > numAttempts) {
                    additionalTurn = false;
                }

                flag = winnerTeam == -1 && additionalTurn;
                players.get(i).multiplayerTurn = false;
                for (int j = 0; j < team1.size() * 2; j++) {
                    players.get(j).outputStream.writeBoolean(flag);
                }
                if (!flag) {
                    break game;
                }
                if (i == 0) {
                    teamScore[2] = teamScore[0];
                } else if (i == 1) {
                    teamScore[3] = teamScore[1];
                } else if (i == 2) {
                    teamScore[0] = teamScore[2];
                } else if (i == 3) {
                    teamScore[1] = teamScore[3];
                }
            }


        } while (flag);
        if (isQuit == -1) {
            if (teamScore[0] > teamScore[1]) {
                players.get(0).outputStream.writeUTF(winnerMsg);
                players.get(2).outputStream.writeUTF(winnerMsg);
                players.get(1).outputStream.writeUTF(loserMsg);
                players.get(3).outputStream.writeUTF(loserMsg);
            } else if (teamScore[0] < teamScore[1]) {
                players.get(0).outputStream.writeUTF(loserMsg);
                players.get(2).outputStream.writeUTF(loserMsg);
                players.get(1).outputStream.writeUTF(winnerMsg);
                players.get(3).outputStream.writeUTF(winnerMsg);
            } else {
                players.get(0).outputStream.writeUTF(drawMsg);
                players.get(1).outputStream.writeUTF(drawMsg);
                players.get(2).outputStream.writeUTF(drawMsg);
                players.get(3).outputStream.writeUTF(drawMsg);
            }
        } else if (isQuit == 0) {
            players.get(0).outputStream.writeUTF(loserMsg);
            players.get(2).outputStream.writeUTF(loserMsg);
            players.get(1).outputStream.writeUTF(winnerMsg);
            players.get(3).outputStream.writeUTF(winnerMsg);
        } else if (isQuit == 1) {
            players.get(0).outputStream.writeUTF(winnerMsg);
            players.get(2).outputStream.writeUTF(winnerMsg);
            players.get(1).outputStream.writeUTF(loserMsg);
            players.get(3).outputStream.writeUTF(loserMsg);
        } else if (isQuit == 2) {
            players.get(0).outputStream.writeUTF(loserMsg);
            players.get(2).outputStream.writeUTF(loserMsg);
            players.get(1).outputStream.writeUTF(winnerMsg);
            players.get(3).outputStream.writeUTF(winnerMsg);
        } else if (isQuit == 3) {
            players.get(0).outputStream.writeUTF(winnerMsg);
            players.get(2).outputStream.writeUTF(winnerMsg);
            players.get(1).outputStream.writeUTF(loserMsg);
            players.get(3).outputStream.writeUTF(loserMsg);
        }


    }

    public static void userLogout(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        loggedInUsers.remove(getClientUsername(clientHandler));
    }
}