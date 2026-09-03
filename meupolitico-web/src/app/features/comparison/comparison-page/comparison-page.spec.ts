import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComparisonPage } from './comparison-page';

describe('ComparisonPage', () => {
  let component: ComparisonPage;
  let fixture: ComponentFixture<ComparisonPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComparisonPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ComparisonPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
