import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeleconsultationFormComponent } from './teleconsultation-form.component';

describe('TeleconsultationFormComponent', () => {
  let component: TeleconsultationFormComponent;
  let fixture: ComponentFixture<TeleconsultationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeleconsultationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TeleconsultationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
