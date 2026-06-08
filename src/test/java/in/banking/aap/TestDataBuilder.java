package in.banking.aap;

import in.banking.aap.domain.entity.*;
import in.banking.aap.domain.enums.ConnectionStatus;
import in.banking.aap.domain.enums.MessageStatus;
import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.NotificationType;
import in.banking.aap.domain.enums.SenderType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test data builder utility class.
 * Provides factory methods to create test entities.
 */
public class TestDataBuilder {
    
    public static User buildTestUser() {
        return User.builder()
                .id("user123")
                .clerkId("clerk_user123")
                .email("user@example.com")
                .name("Test User")
                .phoneNumber("+1234567890")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static Client buildTestClient() {
        return Client.builder()
                .id("client456")
                .clerkId("clerk_client456")
                .email("client@example.com")
                .name("Test Client")
                .businessName("Test Hotel Group")
                .phoneNumber("+0987654321")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static Property buildTestProperty(String clientId) {
        return Property.builder()
                .id("property789")
                .clientId(clientId)
                .name("Test Hotel")
                .address("123 Test St")
                .city("Test City")
                .country("Test Country")
                .images(List.of("image1.jpg", "image2.jpg"))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static Connection buildTestConnection(String userId, String propertyId, String clientId) {
        return Connection.builder()
                .id("connection001")
                .userId(userId)
                .propertyId(propertyId)
                .clientId(clientId)
                .status(ConnectionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static ChatRoom buildTestChatRoom(String userId, String clientId, String propertyId, String connectionId) {
        return ChatRoom.builder()
                .id("chat001")
                .userId(userId)
                .clientId(clientId)
                .propertyId(propertyId)
                .connectionId(connectionId)
                .isActive(true)
                .unreadCountUser(0)
                .unreadCountClient(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static Message buildTextMessage(String chatId, String senderId, SenderType senderType) {
        return Message.builder()
                .id("message001")
                .chatId(chatId)
                .senderId(senderId)
                .senderType(senderType)
                .messageType(MessageType.TEXT)
                .content("Test message content")
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static Message buildImageMessage(String chatId, String senderId, SenderType senderType) {
        return Message.builder()
                .id("message002")
                .chatId(chatId)
                .senderId(senderId)
                .senderType(senderType)
                .messageType(MessageType.IMAGE)
                .imageUrl("https://s3.amazonaws.com/test.jpg")
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static Message buildBookingInquiryMessage(String chatId, String senderId) {
        return Message.builder()
                .id("message003")
                .chatId(chatId)
                .senderId(senderId)
                .senderType(SenderType.USER)
                .messageType(MessageType.BOOKING_INQUIRY)
                .content("I'd like to book a room for next weekend")
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static Notification buildTestNotification(String userId, NotificationType type) {
        return Notification.builder()
                .id("notif001")
                .userId(userId)
                .type(type)
                .title("Test Notification")
                .message("Test notification message")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
