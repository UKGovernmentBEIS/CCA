import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryHeaderComponent } from './summary-header.component';

describe('SummaryHeaderComponent', () => {
  let component: SummaryHeaderComponent;
  let fixture: ComponentFixture<SummaryHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SummaryHeaderComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
