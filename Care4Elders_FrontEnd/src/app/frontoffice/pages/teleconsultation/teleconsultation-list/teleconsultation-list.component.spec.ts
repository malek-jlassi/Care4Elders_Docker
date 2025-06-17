import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeleconsultationListComponent } from './teleconsultation-list.component';

describe('TeleconsultationListComponent', () => {
  let component: TeleconsultationListComponent;
  let fixture: ComponentFixture<TeleconsultationListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeleconsultationListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TeleconsultationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
