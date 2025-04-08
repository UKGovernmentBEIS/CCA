export function omit<T>(obj: T, ...props: (keyof T)[]) {
  const o = structuredClone(obj);
  props.forEach((k) => {
    delete o[k];
  });
  return o;
}
