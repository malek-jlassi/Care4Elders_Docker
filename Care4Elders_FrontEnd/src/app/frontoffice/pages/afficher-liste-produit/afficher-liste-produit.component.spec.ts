import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AfficherListeProduitComponent } from './afficher-liste-produit.component';

describe('AfficherListeProduitComponent', () => {
  let component: AfficherListeProduitComponent;
  let fixture: ComponentFixture<AfficherListeProduitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AfficherListeProduitComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AfficherListeProduitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
