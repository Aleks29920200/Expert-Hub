import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {AiService} from '../services/ai.service';
import {Router} from '@angular/router';
 // Провери пътя!

interface ChatMessage {
  text: string;
  sender: 'user' | 'ai';
  actionRoute?: string;
}

@Component({
  selector: 'app-ai-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <button *ngIf="!isOpen" (click)="toggleChat()" class="btn btn-primary rounded-circle shadow-lg d-flex justify-content-center align-items-center"
            style="position: fixed; bottom: 20px; right: 20px; width: 60px; height: 60px; z-index: 1050;">
      <i class="fas fa-robot fa-2x text-white"></i>
    </button>

    <div *ngIf="isOpen" class="card shadow-lg border-0"
         style="position: fixed; bottom: 20px; right: 20px; width: 350px; height: 450px; z-index: 1050; display: flex; flex-direction: column;">

      <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center py-3">
        <div class="fw-bold"><i class="fas fa-robot me-2"></i> Support Assistant</div>
        <button class="btn-close btn-close-white" (click)="toggleChat()"></button>
      </div>

      <div class="card-body bg-light overflow-auto p-3" style="flex-grow: 1;" #chatScroll>

        <div class="d-flex justify-content-start mb-3">
          <div class="bg-white p-2 rounded-3 shadow-sm" style="max-width: 80%; font-size: 0.9rem;">
            Hello! I'm your AI Assistant. How can I help you find an expert or use the platform?
          </div>
        </div>

        <div *ngFor="let msg of messages" class="mb-3 d-flex flex-column"
             [ngClass]="{'align-items-end': msg.sender === 'user', 'align-items-start': msg.sender === 'ai'}">

          <div class="p-2 rounded-3 shadow-sm" style="max-width: 80%; font-size: 0.9rem; word-wrap: break-word;"
               [ngClass]="{'bg-primary text-white': msg.sender === 'user', 'bg-white text-dark border': msg.sender === 'ai'}">
            {{ msg.text }}
          </div>

          <button *ngIf="msg.actionRoute"
                  (click)="navigateToAction(msg.actionRoute)"
                  class="btn btn-sm btn-warning fw-bold mt-2 shadow-sm rounded-pill px-3">
            <i class="fas fa-external-link-alt me-1"></i> Go to Page
          </button>

        </div>

        <div *ngIf="isLoading" class="d-flex justify-content-start mb-3">
          <div class="bg-white p-2 rounded-3 shadow-sm text-muted" style="font-size: 0.9rem;">
            <i class="fas fa-ellipsis-h fa-fade"></i> AI is typing...
          </div>
        </div>
      </div>

      <div class="card-footer bg-white p-2">
        <div class="input-group">
          <input type="text" class="form-control border-0 shadow-none bg-light"
                 placeholder="Type your message..."
                 [(ngModel)]="userInput"
                 (keyup.enter)="sendMessage()">
          <button class="btn text-primary fw-bold" (click)="sendMessage()" [disabled]="isLoading || !userInput.trim()">
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>
      </div>
    </div>
  `
})
export class AiChatComponent {
  isOpen = false;
  userInput = '';
  messages: ChatMessage[] = [];
  isLoading = false;

  @ViewChild('chatScroll') private chatScrollContainer!: ElementRef;

  constructor(private aiService: AiService, private router: Router) {}

  toggleChat() {
    this.isOpen = !this.isOpen;
  }

  sendMessage() {
    if (!this.userInput.trim()) return;

    const userMessage = this.userInput;
    this.messages.push({ text: userMessage, sender: 'user' });
    this.userInput = '';
    this.isLoading = true;
    this.scrollToBottom();

    this.aiService.askAssistant(userMessage).subscribe({
      next: (response) => {
        let replyText = response.reply;
        let routeObj = undefined;

        // --- МАГИЯТА ЗА ПРИХВАЩАНЕ НА ЛИНКОВЕ ---
        // Търсим дали AI е върнал нещо като ||ROUTE:/login||
        const routeMatch = replyText.match(/\|\|ROUTE:(.*?)\|\|/);

        if (routeMatch) {
          routeObj = routeMatch[1].trim(); // Взимаме самия линк (напр. /login)
          replyText = replyText.replace(routeMatch[0], '').trim(); // Изтриваме тага от съобщението
        }
        // ------------------------------------------

        // Записваме съобщението (с или без линк)
        this.messages.push({ text: replyText, sender: 'ai', actionRoute: routeObj });
        this.isLoading = false;
        this.scrollToBottom();
      },
      error: (err) => {
        this.messages.push({ text: 'Error connecting to the server.', sender: 'ai' });
        this.isLoading = false;
        this.scrollToBottom();
      }
    });
  }
  navigateToAction(route: string) {
    this.router.navigate([route]);
    this.isOpen = false; // Затваряме чата, за да не пречи на новата страница
  }

  // Метод, който автоматично скролира чата най-долу
  scrollToBottom() {
    setTimeout(() => {
      if (this.chatScrollContainer) {
        this.chatScrollContainer.nativeElement.scrollTop = this.chatScrollContainer.nativeElement.scrollHeight;
      }
    }, 100);
  }
}
