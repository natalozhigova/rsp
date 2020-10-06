import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private enum Move {
        LIZARD, SCISSORS, ROCK, SPOCK, PAPER;

        public int compareMoves(Move otherMove) {

            if (this == otherMove)
                return 0;

            switch (this) {
                case ROCK:
                    return ((otherMove == SCISSORS) || (otherMove == LIZARD) ? 1 : -1);
                case PAPER:
                    return ((otherMove == ROCK) || (otherMove == SPOCK) ? 1 : -1);
                case SCISSORS:
                    return ((otherMove == PAPER) || (otherMove == LIZARD) ? 1 : -1);
                case LIZARD:
                    return ((otherMove == PAPER) || (otherMove == SPOCK) ? 1 : -1);
                case SPOCK:
                    return ((otherMove == SCISSORS) || (otherMove == ROCK) ? 1 : -1);
            }
            return 0;
        }
    }

    private class User {
        private Scanner input;

        public User() {
            input = new Scanner(System.in);
        }

        public Move getMove() {
            System.out.println("Available moves:");
            for (Move move : Move.values()) {
                System.out.println(move);
            }
            System.out.println("EXIT");
            String userInput = input.nextLine().toUpperCase();
            if (userInput.equals("ROCK") || userInput.equals("SCISSORS") || userInput.equals("PAPER") ||
                    userInput.equals("LIZARD") || userInput.equals("SPOCK") || userInput.equals("EXIT"))
                switch (userInput) {
                    case "ROCK":
                        return Move.ROCK;
                    case "SCISSORS":
                        return Move.SCISSORS;
                    case "PAPER":
                        return Move.PAPER;
                    case "LIZARD":
                        return Move.LIZARD;
                    case "SPOCK":
                        return Move.SPOCK;
                }
            if (userInput.equals("EXIT")) {
                System.exit(0);
            }
            return getMove();

        }
    }

    public String generate() throws NoSuchAlgorithmException {

        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] values = new byte[32]; // 256 бит
        random.nextBytes(values);

        StringBuilder sb = new StringBuilder();
        for (byte b : values) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String secure(String key, String message) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        Mac sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec s_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256.init(s_key);

        return Base64.encodeBase64String(sha256.doFinal(message.getBytes("UTF-8")));
    }

    private class Computer {

        public Move getMove() throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
            Move[] moves = Move.values();

            Random random = new Random();
            int index = random.nextInt(moves.length);
            setKey(generate());
            System.out.println("HMAC: " + secure(getKey(), moves[index].toString()));
            return moves[index];
        }
    }

    private User user;
    private Computer computer;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Main() {
        user = new User();
        computer = new Computer();

    }

    public void startGame() throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        System.out.println("Start Game!");
        Move computerMove = computer.getMove();
        Move userMove = user.getMove();

        System.out.println("You " + userMove + ".");
        System.out.println("Comp  " + computerMove + ".");
        int compareMoves = userMove.compareMoves(computerMove);
        switch (compareMoves) {
            case 0:
                System.out.println("Draw!");
                break;
            case 1:
                System.out.println(userMove + " beats " + computerMove + ". You win!");
                break;
            case -1:
                System.out.println(computerMove + " beats " + userMove + ". You lose.");
                break;
        }
        System.out.println("HMAC key: " + getKey());
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Main game = new Main();
        game.startGame();

    }
}
