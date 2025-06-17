import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CareRequestFormComponent } from '../care-request-form/care-request-form.component';
import { CareRequestListComponent } from '../care-request-list/care-request-list.component';
import { HttpClient } from '@angular/common/http';
import { TopbarComponent } from '../../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../../shared/layout/footer/footer.component';
import { CareRequest } from '../../../../models/care-request.model';
import { CareRequestService } from '../../../../services/care-request.service';
@Component({
  selector: 'app-care-request-page',
  standalone: true,
  imports: [CommonModule, CareRequestFormComponent, CareRequestListComponent,
      TopbarComponent,
      NavbarComponent,
      FooterComponent],
  templateUrl: './care-request-page.component.html',
  styleUrls: ['./care-request-page.component.css']
})
export class CareRequestPageComponent {
  @ViewChild(CareRequestListComponent) careList!: CareRequestListComponent;

  careRequests: any[] = [];

  constructor(private http: HttpClient, private careService: CareRequestService) {}

  ngOnInit(): void {
    this.refreshList();
  }

  // refreshList(): void {
  //   this.http.get('http://localhost:8083/careservicerequest').subscribe(data => {
  //     this.careRequests = data as any[];
  //   });
  // }
  refreshList(): void {
  this.careService.getAll().subscribe({
    next: data => this.careRequests = data,
    error: err => console.error('Error fetching care requests:', err)
  });
}
  onRequestCreated(): void {
    this.careList.refreshList();  // 👈 Directly trigger refresh on the child list
  }
  
  @ViewChild(CareRequestFormComponent) formComponent!: CareRequestFormComponent;

onEditRequest(request: CareRequest): void {
  this.formComponent.setRequestToEdit(request);
}

  
}
