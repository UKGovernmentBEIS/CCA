export enum SchemeVersionEnum {
  CCA_2 = 'CCA_2',
  CCA_3 = 'CCA_3',
}

export type SchemeVersion = keyof typeof SchemeVersionEnum;
export type SchemeVersions = (keyof typeof SchemeVersionEnum)[];
