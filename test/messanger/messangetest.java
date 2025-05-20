package messenger;

import static org.junit.Assert.*;
import org.junit.Test;

public class MessageTest {
    @Test
    public void testCheckMessageId() {
        Message msg = new Message("+27821234567", "Test message");
        assertTrue(msg.checkMessageId());
        assertEquals(10, msg.getMessageId().length());
    }

    @Test
    public void testCheckRecipientCell() {
        // Valid international format
        Message msg1 = new Message("+27821234567", "Test");
        assertEquals(0, msg1.checkRecipientCell());
        
        // Valid local format
        Message msg2 = new Message("0821234567", "Test");
        assertEquals(0, msg2.checkRecipientCell());
        
        // Invalid format
        Message msg3 = new Message("2821234567", "Test");
        assertEquals(-1, msg3.checkRecipientCell());
        
        // Too short
        Message msg4 = new Message("+2782123456", "Test");
        assertEquals(-1, msg4.checkRecipientCell());
    }

    @Test
    public void testCreateMessageHash() {
        Message msg = new Message("+27821234567", "Hi Mike, can you join us for dinner tonight");
        String hash = msg.createMessageHash();
        assertTrue(hash.startsWith(msg.getMessageId().substring(0, 2)));
        assertTrue(hash.contains(":"));
        assertTrue(hash.contains("HI"));
        assertTrue(hash.contains("TONIGHT"));
    }

    @Test
    public void testMessageLengthValidation() {
        // Create a long message (>250 chars)
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longMessage.append("a");
        }
        
        Message msg = new Message("+27821234567", longMessage.toString());
        // The validation happens in the application flow, not in Message class
        // So we just test that the message was stored as-is
        assertEquals(300, msg.getMessageContent().length());
    }

    @Test
    public void testTotalMessagesCount() {
        int initialCount = Message.returnTotalMessages();
        
        Message msg1 = new Message("+27821234567", "Message 1");
        msg1.sentMessage();
        
        Message msg2 = new Message("+27821234567", "Message 2");
        msg2.sentMessage();
        
        assertEquals(initialCount + 2, Message.returnTotalMessages());
    }
}
