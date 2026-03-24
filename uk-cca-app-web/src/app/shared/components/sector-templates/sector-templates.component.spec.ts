import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectorTemplatesComponent } from './sector-templates.component';

describe('SectorTemplatesComponent', () => {
  let component: SectorTemplatesComponent;
  let fixture: ComponentFixture<SectorTemplatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorTemplatesComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorTemplatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show table values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
