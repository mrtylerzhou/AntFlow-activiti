declare type ModeType = 'none' | 'relativeName';
declare type CustomNameFn = (id: string) => string;
interface Options {
    mode?: ModeType | CustomNameFn;
    enableAutoExpose?: boolean;
}

export { CustomNameFn, ModeType, Options };
