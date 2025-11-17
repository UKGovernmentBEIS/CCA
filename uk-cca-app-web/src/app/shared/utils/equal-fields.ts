export function equalFields(
  tuDetailsField: string | null | undefined,
  companiesHouseDetailsField: string | null | undefined,
): boolean {
  if (tuDetailsField && !companiesHouseDetailsField) return;
  return tuDetailsField?.trim().toLowerCase() === companiesHouseDetailsField?.trim().toLowerCase();
}

export function equalArrayFields(
  tuDetailsField: string[] | null,
  companiesHouseDetailsField: string[] | null,
): boolean {
  if (!tuDetailsField.length || !companiesHouseDetailsField.length) return;
  if (tuDetailsField.length !== companiesHouseDetailsField.length) return false;

  return tuDetailsField.every(
    (item, index) => item.trim().toLowerCase() === companiesHouseDetailsField[index]?.trim().toLowerCase(),
  );
}

export function equalAddressFields(targetUnitAddress: string[], companiesHouseAddress: string[]): boolean {
  if (!targetUnitAddress.length || !companiesHouseAddress.length) return;

  const str1 = targetUnitAddress.join('').replace(/\s+/g, ' ').trim().toLowerCase();
  const str2 = companiesHouseAddress.join('').replace(/\s+/g, ' ').trim().toLowerCase();

  return str1 === str2;
}
