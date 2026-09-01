import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoliticianDetail } from './politician-detail';

describe('PoliticianDetail', () => {
  let component: PoliticianDetail;
  let fixture: ComponentFixture<PoliticianDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PoliticianDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(PoliticianDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
