import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { CookiesPopUpComponent } from './cookies-pop-up.component';

describe('CookiesPopUpComponent', () => {
  let component: CookiesPopUpComponent;
  let fixture: ComponentFixture<CookiesPopUpComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [CookiesPopUpComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CookiesPopUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
