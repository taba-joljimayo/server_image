package com.example.demo.handler;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageWebSocketHandler implements WebSocketHandler{

    private  StringBuffer partialMessageBuffer = new StringBuffer();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket 연결 성공: " + session.getId());
        session.sendMessage(new TextMessage("서버와 연결되었습니다."));
    }

    @Override
    public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;

            partialMessageBuffer.append(textMessage.getPayload());
            // 메시지 수신 중인지 확인
            if (textMessage.isLast()) {
                String completePayload = partialMessageBuffer.toString();
                partialMessageBuffer.setLength(0); // 버퍼 초기화

                System.out.println("전체 메시지 수신 완료");
                System.out.println("수신된 Base64 데이터 길이: " + completePayload.length());

                byte[] imageBytes = Base64.getDecoder().decode(completePayload);
                Files.write(Paths.get("images/received_image.jpg"), imageBytes);
                System.out.println("이미지 저장 완료!");
            } else {
                System.out.println("부분 메시지 수신 중...");
            }
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket 에러 발생: " + exception.getMessage());
        if (!session.isOpen()) {
            System.out.println("클라이언트 연결이 끊어졌습니다.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("WebSocket 연결 종료: " + session.getId() + ", 이유: " + closeStatus.getReason());
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

}