import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';

import { 
  MAT_DIALOG_DATA, 
  MatDialogRef, 
  MatDialogTitle, 
  MatDialogContent, 
  MatDialogActions, 
  MatDialogClose 
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText: string;
  cancelText: string;
}


@Component({
  selector: 'app-confirm-dialog',
  imports: [
    CommonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    //MatDialogClose,
    MatButtonModule
  ],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.css'
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
