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

    public int indexMove(String move, String[] moves) {
        int index = 0;
        for (int i = 0; i < moves.length; i++) {
            if (move.toUpperCase().equals(moves[i].toUpperCase())) {
                index = i;
            }
        }
        return index;
    }

    public int compareMoves(String userMove, String computerMove, String[] moves) {
        if (computerMove.equals(userMove)) {
            return 0;
        }
        int winIndexes = moves.length / 2;
        int indexComputerMove = indexMove(computerMove, moves);
        int indexUserMove = indexMove(userMove, moves);
        for (int i = 0; i < moves.length; i++) {
            if (indexUserMove < winIndexes + 1) {
                if (indexComputerMove < indexUserMove || (indexComputerMove > indexUserMove + winIndexes)) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (indexUserMove >= winIndexes + 1) {
                if ((indexComputerMove >= indexUserMove - winIndexes) && (indexComputerMove < indexUserMove)) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        return 0;
    }

    private class User {
        private Scanner input;
        public User() {
            input = new Scanner(System.in);
        }
        public String getMove(String[] moves) {
            System.out.println("Available moves: ");
            for (String move : moves) {
                System.out.println(move.toUpperCase());
            }
            System.out.println("EXIT");
            String userInput = input.nextLine().toUpperCase();
            if (userInput.equals("EXIT")) {
                System.exit(0);
            }
            for (String move : moves) {
                if (userInput.equals(move.toUpperCase())) {
                    return userInput;
                }
            }
            return getMove(moves);
        }
    }

    public String generate() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] values = new byte[32];
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
        public String getMove(String[] moves) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
            Random random = new Random();
            int index = random.nextInt(moves.length);
            setKey(generate());
            System.out.println("HMAC: " + secure(getKey(), moves[index]));
            return moves[index].toUpperCase();
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

    public void startGame(String[] moves) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        System.out.println("Start Game!");
        String computerMove = computer.getMove(moves);
        String userMove = user.getMove(moves);
        System.out.println("You " + userMove + ".");
        System.out.println("Comp  " + computerMove + ".");
        int compareMoves = compareMoves(userMove, computerMove, moves);
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

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        if ((args.length >= 3) && (args.length % 2 == 1)) {
            Main game = new Main();
            game.startGame(args);

        } else {
            System.out.println("Error: wrong number of arguments. Enter 3, 5, 7.. etc arguments");
        }
    }
}
