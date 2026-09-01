import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoliticianList } from './politician-list';

describe('PoliticianList', () => {
  let component: PoliticianList;
  let fixture: ComponentFixture<PoliticianList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PoliticianList],
    }).compileComponents();

    fixture = TestBed.createComponent(PoliticianList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
