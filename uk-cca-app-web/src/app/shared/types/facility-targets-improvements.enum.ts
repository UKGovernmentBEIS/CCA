export enum Improvement {
  TP7 = 'TP7',
  TP8 = 'TP8',
  TP9 = 'TP9',
}

export type Improvements = (keyof typeof Improvement)[];
