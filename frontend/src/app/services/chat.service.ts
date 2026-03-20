import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, Observable } from 'rxjs';
import { ChatMessage } from '../models/chat.model';
import { Client } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/messages';
  private brokerUrl = 'ws://localhost:8080/ws';
  private stompClient: Client | null = null;

  private messageSource = new Subject<ChatMessage>();
  public messages$ = this.messageSource.asObservable();

  // --- WebRTC Subjects ---
  private callOfferSource = new Subject<ChatMessage>();
  public callOffer$ = this.callOfferSource.asObservable();

  private callAnswerSource = new Subject<ChatMessage>();
  public callAnswer$ = this.callAnswerSource.asObservable();

  private callIceSource = new Subject<ChatMessage>();
  public callIce$ = this.callIceSource.asObservable();

  constructor(private http: HttpClient) { }

  initializeWebSocketConnection(username: string) {
    this.stompClient = new Client({
      brokerURL: this.brokerUrl,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        console.log('Connected to WebSocket as ' + username);

        // Standard Messages
        this.stompClient?.subscribe(`/user/${username}/queue/messages`, (message) => {
          if (message.body) this.messageSource.next(JSON.parse(message.body));
        });

        // WebRTC Subscriptions mapped to your ChatController
        this.stompClient?.subscribe(`/user/${username}/queue/call/offer`, (message) => {
          if (message.body) this.callOfferSource.next(JSON.parse(message.body));
        });

        this.stompClient?.subscribe(`/user/${username}/queue/call/answer`, (message) => {
          if (message.body) this.callAnswerSource.next(JSON.parse(message.body));
        });

        this.stompClient?.subscribe(`/user/${username}/queue/call/ice`, (message) => {
          if (message.body) this.callIceSource.next(JSON.parse(message.body));
        });
      },
      onStompError: (frame) => console.error('Broker reported error: ' + frame.headers['message'])
    });

    this.stompClient.activate();
  }

  getChatHistory(user1: string, user2: string): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/${user1}/${user2}`);
  }

  sendMessage(message: ChatMessage) {
    if (this.stompClient && this.stompClient.connected) {
      message.messageType = "CHAT"; // Задължително го добави!
      this.stompClient.publish({ destination: '/app/chat', body: JSON.stringify(message) });
    }
  }

  sendCallOffer(message: ChatMessage) {
    message.messageType = "WEBRTC_OFFER"; // За WebRTC
    this.stompClient?.publish({ destination: '/app/call/offer', body: JSON.stringify(message) });
  }
  sendCallAnswer(message: ChatMessage) {
    this.stompClient?.publish({ destination: '/app/call/answer', body: JSON.stringify(message) });
  }

  sendIceCandidate(message: ChatMessage) {
    this.stompClient?.publish({ destination: '/app/call/ice', body: JSON.stringify(message) });
  }
  editMessage(message: ChatMessage) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({ destination: '/app/edit', body: JSON.stringify(message) });
    }
  }

  deleteMessage(message: ChatMessage) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({ destination: '/app/delete', body: JSON.stringify(message) });
    }
  }
  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.deactivate();
    }
  }
  // Взима списък с уникалните контакти (Inbox) за даден потребител
  getRecentContacts(username: string): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/chat/contacts/${username}`);
  }
}
