import { Component } from '@angular/core';

import { NavbarComponent } from '../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../shared/layout/footer/footer.component';
import { CommonModule } from '@angular/common';
import { TopbarComponent } from '../../../shared/layout/topbar/topbar.component';
@Component({
  standalone: true,
  selector: 'app-home',
  imports: [CommonModule,
    TopbarComponent,
    NavbarComponent,
    FooterComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
