
package messenger;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Message {
    private static int totalMessagesSent = 0;
    private static List<Message> sentMessages = new ArrayList<>();
    
    private String messageId;
    private int numSent;
    private String recipient;
    private String messageContent;
    private String messageHash;
    private String status;

    public Message(String recipient, String messageContent) {
        this.messageId = generateMessageId();
        this.numSent = ++totalMessagesSent;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = createMessageHash();
    }

    // Generate a random 10-digit message ID
    private String generateMessageId() {
        Random random = new Random();
        long id = 1000000000L + random.nextInt(900000000);
        return String.valueOf(id);
    }

    public boolean checkMessageId() {
        return messageId != null && messageId.length() == 10;
    }

    public int checkRecipientCell() {
        if (!recipient.startsWith("+27") && !recipient.startsWith("0")) {
            return -1; // Invalid format
        }
        
        String digits = recipient.startsWith("+27") ? 
                       recipient.substring(3) : 
                       recipient.substring(1);
        
        if (digits.length() != 9) {
            return -1; // Wrong length
        }
        
        for (char ch : digits.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return -1; // Contains non-digit
            }
        }
        
        return 0; // Valid
    }

    public String createMessageHash() {
        String[] words = messageContent.split(" ");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        return (messageId.substring(0, 2) + ":" + numSent + ":" + 
               firstWord.toUpperCase() + lastWord.toUpperCase());
    }

    public String sentMessage() {
        String[] options = {"Send Message", "Disregard Message", "Store Message to send later"};
        int choice = JOptionPane.showOptionDialog(null, 
                "What would you like to do with this message?", 
                "Message Options", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                options, 
                options[0]);
        
        switch (choice) {
            case 0: // Send
                status = "Sent";
                sentMessages.add(this);
                return "Message successfully sent.";
            case 1: // Disregard
                status = "Discarded";
                totalMessagesSent--; // Decrement since we're not counting this
                return "Press 0 to delete message.";
            case 2: // Store
                status = "Stored";
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }

    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message History:\n");
        for (Message msg : sentMessages) {
            sb.append("ID: ").append(msg.messageId)
              .append(", Hash: ").append(msg.messageHash)
              .append(", To: ").append(msg.recipient)
              .append(", Content: ").append(msg.messageContent)
              .append("\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public void storeMessage() {
        try {
            JSONObject messageJson = new JSONObject();
            messageJson.put("messageId", messageId);
            messageJson.put("numSent", numSent);
            messageJson.put("recipient", recipient);
            messageJson.put("messageContent", messageContent);
            messageJson.put("messageHash", messageHash);
            messageJson.put("status", status);

            JSONArray messagesArray;
            try {
                // Read existing file if it exists
                String fileContent = new String(java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get("messages.json")));
                messagesArray = new JSONArray(fileContent);
            } catch (IOException e) {
                // File doesn't exist, create new array
                messagesArray = new JSONArray();
            }

            messagesArray.put(messageJson);

            // Write back to file
            try (FileWriter file = new FileWriter("messages.json")) {
                file.write(messagesArray.toString(4)); // Indent for readability
                file.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters for unit testing
    public String getMessageId() { return messageId; }
    public String getRecipient() { return recipient; }
    public String getMessageContent() { return messageContent; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
}
