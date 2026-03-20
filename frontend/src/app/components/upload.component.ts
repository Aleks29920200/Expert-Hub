import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {FileService} from '../services/file.service';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mx-auto p-8">
      <h2 class="text-2xl font-bold mb-4">Upload Image</h2>
      <div class="bg-white p-6 rounded shadow">
        <input type="file" (change)="onFileSelected($event)" class="mb-4 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-violet-50 file:text-violet-700 hover:file:bg-violet-100"/>
        <button (click)="onUpload()" [disabled]="!selectedFile" class="bg-blue-600 text-white px-4 py-2 rounded">Submit</button>
      </div>
    </div>
  `
})
export class UploadComponent {
  selectedFile: File | null = null;

  constructor(private fileService:FileService) {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onUpload() {
    if (!this.selectedFile) return;

    // Използваме вече написаната логика, която правилно подава 'img'
    this.fileService.upload(this.selectedFile).subscribe({
      next: (res) => alert('Uploaded successfully!'),
      error: (err) => alert('Upload failed')
    });
  }
}
