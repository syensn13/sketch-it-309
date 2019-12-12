package SS_7.sketch_it.Util;

import SS_7.sketch_it.Entities.ChatEntity;
import SS_7.sketch_it.Repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Configures websocket listener events.
 */
@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private MongoRepository mongoRepository;

    /**
     * Handles connect events.
     * @param event
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        //System.out.println("User Joined");
    }

    /**
     * Handles disconnect events.
     * @param event
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            ChatEntity chatMessage = new ChatEntity(username + " has disconnected.", "Server", roomId, "disconnect");
            messagingTemplate.convertAndSend("/room/" + roomId + "/chat", chatMessage);
            messagingTemplate.convertAndSend("/room/menu/roomChange", true);
            mongoRepository.removeUserFromRoom(username, roomId);
        }
    }
}