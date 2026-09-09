import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankFilters } from './rank-filters';

describe('RankFilters', () => {
  let component: RankFilters;
  let fixture: ComponentFixture<RankFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RankFilters],
    }).compileComponents();

    fixture = TestBed.createComponent(RankFilters);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
