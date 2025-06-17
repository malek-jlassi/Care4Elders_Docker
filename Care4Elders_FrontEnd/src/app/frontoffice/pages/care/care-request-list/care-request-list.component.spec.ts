import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CareRequestListComponent } from './care-request-list.component';

describe('CareRequestListComponent', () => {
  let component: CareRequestListComponent;
  let fixture: ComponentFixture<CareRequestListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CareRequestListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CareRequestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
