package messenger;

import javax.swing.JOptionPane;

public class QuickChatApp {
    private static Login userSession;
    private static boolean loggedIn = false;

    public static void main(String[] args) {
        // Welcome message
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

        // Login loop
        while (!loggedIn) {
            String username = JOptionPane.showInputDialog("Enter your username:");
            String password = JOptionPane.showInputDialog("Enter your password:");
            
            // For demo purposes - in real app you'd have proper user registration/loading
            userSession = new Login("Test", "User");
            userSession.registerUser("a_b", "Passw0rd!", "+27821234567");
            
            loggedIn = userSession.loginUser(username, password);
            if (!loggedIn) {
                JOptionPane.showMessageDialog(null, "Login failed. Please try again.");
            }
        }
        
        JOptionPane.showMessageDialog(null, 
            userSession.returnLoginStatus(true));

        // Main application loop
        boolean running = true;
        while (running) {
            String[] options = {
                "1) Send Messages", 
                "2) Show recently sent messages", 
                "3) Quit"
            };
            
            int choice = JOptionPane.showOptionDialog(null, 
                "Please select an option:", 
                "QuickChat Menu", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                options, 
                options[0]);
            
            switch (choice) {
                case 0: // Send Messages
                    handleSendMessages();
                    break;
                case 1: // Show recent messages
                    JOptionPane.showMessageDialog(null, "Coming Soon.");
                    break;
                case 2: // Quit
                    running = false;
                    JOptionPane.showMessageDialog(null, 
                        "Total messages sent: " + Message.returnTotalMessages());
                    break;
                default:
                    running = false;
            }
        }
    }

    private static void handleSendMessages() {
        String numMessagesStr = JOptionPane.showInputDialog(
            "How many messages would you like to send?");
        int numMessages;
        
        try {
            numMessages = Integer.parseInt(numMessagesStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            return;
        }
        
        for (int i = 0; i < numMessages; i++) {
            String recipient = JOptionPane.showInputDialog(
                "Enter recipient phone number (e.g., +27821234567):");
            String message = JOptionPane.showInputDialog("Enter your message (max 250 chars):");
            
            if (message.length() > 250) {
                JOptionPane.showMessageDialog(null, 
                    "Message exceeds 250 characters by " + (message.length() - 250) + 
                    ", please reduce size.");
                i--; // Retry this message
                continue;
            }
            
            Message newMessage = new Message(recipient, message);
            
            int cellCheck = newMessage.checkRecipientCell();
            if (cellCheck != 0) {
                JOptionPane.showMessageDialog(null, 
                    "Cell phone number is incorrectly formatted or does not contain " +
                    "an international code. Please correct the number and try again.");
                i--; // Retry this message
                continue;
            }
            
            String result = newMessage.sentMessage();
            JOptionPane.showMessageDialog(null, result);
            
            if (newMessage.getStatus().equals("Sent")) {
                // Display message details
                JOptionPane.showMessageDialog(null, 
                    "Message Details:\n" +
                    "ID: " + newMessage.getMessageId() + "\n" +
                    "Hash: " + newMessage.getMessageHash() + "\n" +
                    "To: " + newMessage.getRecipient() + "\n" +
                    "Content: " + newMessage.getMessageContent());
            }
        }
    }
}
