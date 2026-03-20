package com.example.skillsh.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "messages", indexes = {
        @Index(name = "idx_sender_receiver", columnList = "sender, receiver")
})
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String receiver;

    @Column(columnDefinition = "TEXT")
    private String content; // Тук пазим обикновения текст на чата

    // ----------------------------------------------------
    // 1. БИЗНЕС ОФЕРТИ (Запазват се в базата като текст/JSON)
    // ----------------------------------------------------
    @Column(columnDefinition = "TEXT")
    private String businessOffer; // Преименуваме го, за да не се бърка с WebRTC. Тук записваме JSON с цената/услугата.

    // Много важно поле! Ще определя какво е съобщението:
    // "CHAT", "BUSINESS_OFFER", "WEBRTC_OFFER", "WEBRTC_ANSWER", "WEBRTC_CANDIDATE"
    private String messageType;

    // ----------------------------------------------------
    // 2. WebRTC СИГНАЛИ (НЕ се запазват в базата)
    // ----------------------------------------------------
    @Transient
    private Object rtcOffer; // Използваме @Transient, за да не пълним базата с временни видео връзки

    @Transient
    private Object rtcAnswer;

    @Transient
    private Object rtcCandidate;

    private boolean edited = false;
    private boolean indicatorForDeletion = false;
    private Long replyToMessageId;
}