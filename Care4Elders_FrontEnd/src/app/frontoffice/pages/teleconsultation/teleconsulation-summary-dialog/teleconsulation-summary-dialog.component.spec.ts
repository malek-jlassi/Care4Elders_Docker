import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeleconsulationSummaryDialogComponent } from './teleconsulation-summary-dialog.component';

describe('TeleconsulationSummaryDialogComponent', () => {
  let component: TeleconsulationSummaryDialogComponent;
  let fixture: ComponentFixture<TeleconsulationSummaryDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeleconsulationSummaryDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TeleconsulationSummaryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
