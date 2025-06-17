import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CareRequestPageComponent } from './care-request-page.component';

describe('CareRequestPageComponent', () => {
  let component: CareRequestPageComponent;
  let fixture: ComponentFixture<CareRequestPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CareRequestPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CareRequestPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
