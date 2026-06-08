package in.banking.aap.config;

 

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Socket.IO Server Configuration.
 * Configures Netty Socket.IO server for real-time communication.
 * Supports authentication, connection management, and event handling.
 */
@Slf4j
@Component
public class SocketIOConfig {
    
    @Value("${socketio.host}")
    private String host;
    
    @Value("${socketio.port}")
    private Integer port;
    
    @Value("${socketio.bossThreads}")
    private Integer bossThreads;
    
    @Value("${socketio.workerThreads}")
    private Integer workerThreads;
    
    @Value("${socketio.allowCustomRequests}")
    private Boolean allowCustomRequests;
    
    @Value("${socketio.upgradeTimeout}")
    private Integer upgradeTimeout;
    
    @Value("${socketio.pingTimeout}")
    private Integer pingTimeout;
    
    @Value("${socketio.pingInterval}")
    private Integer pingInterval;
    
    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        
        config.setBossThreads(bossThreads);
        config.setWorkerThreads(workerThreads);
        config.setAllowCustomRequests(allowCustomRequests);
        
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        
        // Enable CORS for all origins (restrict in production)
        config.setOrigin("*");
        
        // Authorization disabled for local development
        config.setAuthorizationListener(data -> true);
        
        log.info("Socket.IO server configured on {}:{}", host, port);
        
        return new SocketIOServer(config);
    }
}
