package boot.spring.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SSEController {

    // 使用 ConcurrentHashMap 存储 SseEmitter，key 为客户端的唯一标识符
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // 建立SSE连接，URL中包含客户端ID
    @GetMapping(value = "/sse/connect/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        SseEmitter emitter = new SseEmitter();

        emitters.put(clientId, emitter);
        System.out.println(clientId + " connected");
        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));

        return emitter;
    }

    // 生成文本并广播到所有SSE连接
    @GetMapping("/sse/send")
    public void sendMessage() {
        String[] texts = {
                "SSE（Server-Sent Events）是一种允许服务器向客户端推送实时更新的技术。",
                "它通过HTTP协议实现单向通信，即从服务器到客户端的单向数据流。",
                "与WebSocket不同，SSE是基于HTTP的，主要用于服务器向客户端推送数据，而不需要客户端频繁轮询服务器。",
                "SSE只支持从服务器到客户端的数据传输，不支持双向通信。",
                "这使得它非常适合需要实时更新的应用场景，如股票价格、社交媒体更新、通知系统等。",
                "SSE使用标准的HTTP协议进行通信，这意味着它可以利用现有的HTTP基础设施，如代理服务器、负载均衡器和缓存机制。"
        };

        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            SseEmitter emitter = entry.getValue();
            executorService.execute(() -> {
                for (String text : texts) {
                    try {
                        emitter.send(SseEmitter.event().data(text));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }
            });
        }
    }

    @GetMapping("/sse/send/{clientId}")
    public void sendMessageToClient(@PathVariable String clientId) {
        String[] texts = {
                "SSE（Server-Sent Events）是一种允许服务器向客户端推送实时更新的技术。",
                "它通过HTTP协议实现单向通信，即从服务器到客户端的单向数据流。",
                "与WebSocket不同，SSE是基于HTTP的，主要用于服务器向客户端推送数据，而不需要客户端频繁轮询服务器。",
                "SSE只支持从服务器到客户端的数据传输，不支持双向通信。",
                "这使得它非常适合需要实时更新的应用场景，如股票价格、社交媒体更新、通知系统等。",
                "SSE使用标准的HTTP协议进行通信，这意味着它可以利用现有的HTTP基础设施，如代理服务器、负载均衡器和缓存机制。"
        };

        SseEmitter emitter = emitters.get(clientId);
        executorService.execute(() -> {
            for (String text : texts) {
                try {
                    emitter.send(SseEmitter.event().data(text));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        });

    }
}