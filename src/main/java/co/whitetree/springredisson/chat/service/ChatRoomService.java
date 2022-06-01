package co.whitetree.springredisson.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RListReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService implements WebSocketHandler {

    private final RedissonReactiveClient redissonReactiveClient;

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String room = getChatRoomName(webSocketSession);
        RTopicReactive topic = redissonReactiveClient.getTopic(room, StringCodec.INSTANCE);

        // 누군가 새로운 채팅방에 들어왔을 떄 이전 기록들을 보여주게 하기 위함
        RListReactive<String> list = redissonReactiveClient.getList("history:" + room, StringCodec.INSTANCE);

        // subscribe
        webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> list.add(msg).then(topic.publish(msg)))
                .doOnError(e -> log.error("[subscriber error]", e))
                .doFinally(signalType -> log.info("[subscriber finally]" + signalType))
                .subscribe();

        // publisher
        Flux<WebSocketMessage> flux = topic.getMessages(String.class)
                .startWith(list.iterator())
                .map(webSocketSession::textMessage)
                .doOnError(e -> log.error("[publisher error]", e))
                .doFinally(signalType -> log.info("[publisher finally]" + signalType));
        return webSocketSession.send(flux);
    }

    private String getChatRoomName(WebSocketSession webSocketSession) {
        URI uri = webSocketSession.getHandshakeInfo().getUri();
        return UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap()
                .getOrDefault("room", "default");
    }
}
