import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InternalServerErrorComponent } from './internal-server-error.component';

describe('InternalServerErrorComponent', () => {
  let component: InternalServerErrorComponent;
  let fixture: ComponentFixture<InternalServerErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InternalServerErrorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(InternalServerErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTML elements', () => {
    const element: HTMLElement = fixture.nativeElement;
    const paragraphContents = Array.from(element.querySelectorAll<HTMLParagraphElement>('p')).map((el) =>
      el.textContent.trim(),
    );

    expect(element.querySelector('h1').textContent).toEqual('Sorry, there is a problem with the service');
    expect(paragraphContents).toEqual(['Try again later.', 'Contact the DESNZ helpdesk if you have any questions.']);
    expect(element.querySelector('a').href).toEqual('mailto:cca@environment-agency.gov.uk');
  });
});
