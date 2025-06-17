import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProduitMedicalComponent } from './produit-medical.component';

describe('ProduitMedicalComponent', () => {
  let component: ProduitMedicalComponent;
  let fixture: ComponentFixture<ProduitMedicalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProduitMedicalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProduitMedicalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
