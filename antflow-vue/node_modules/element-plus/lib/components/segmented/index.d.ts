export declare const ElSegmented: import("element-plus/es/utils").SFCWithInstall<import("vue").DefineComponent<{
    ariaLabel: StringConstructor;
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/types").Option[]) | (() => import("./src/types").Option[]) | ((new (...args: any[]) => import("./src/types").Option[]) | (() => import("./src/types").Option[]))[], unknown, unknown, () => never[], boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | BooleanConstructor | StringConstructor)[], unknown, unknown, undefined, boolean>;
    block: BooleanConstructor;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "default" | "small" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    id: StringConstructor;
    name: StringConstructor;
}, {
    props: Readonly<import("@vue/shared").LooseRequired<Readonly<import("vue").ExtractPropTypes<{
        ariaLabel: StringConstructor;
        options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/types").Option[]) | (() => import("./src/types").Option[]) | ((new (...args: any[]) => import("./src/types").Option[]) | (() => import("./src/types").Option[]))[], unknown, unknown, () => never[], boolean>;
        modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | BooleanConstructor | StringConstructor)[], unknown, unknown, undefined, boolean>;
        block: BooleanConstructor;
        size: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "default" | "small" | "large", never>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        disabled: BooleanConstructor;
        validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
        id: StringConstructor;
        name: StringConstructor;
    }>> & {
        onChange?: ((val: any) => any) | undefined;
        "onUpdate:modelValue"?: ((val: any) => any) | undefined;
    }>>;
    emit: ((event: "update:modelValue", val: any) => void) & ((event: "change", val: any) => void);
    ns: {
        namespace: import("vue").ComputedRef<string>;
        b: (blockSuffix?: string) => string;
        e: (element?: string | undefined) => string;
        m: (modifier?: string | undefined) => string;
        be: (blockSuffix?: string | undefined, element?: string | undefined) => string;
        em: (element?: string | undefined, modifier?: string | undefined) => string;
        bm: (blockSuffix?: string | undefined, modifier?: string | undefined) => string;
        bem: (blockSuffix?: string | undefined, element?: string | undefined, modifier?: string | undefined) => string;
        is: {
            (name: string, state: boolean | undefined): string;
            (name: string): string;
        };
        cssVar: (object: Record<string, string>) => Record<string, string>;
        cssVarName: (name: string) => string;
        cssVarBlock: (object: Record<string, string>) => Record<string, string>;
        cssVarBlockName: (name: string) => string;
    };
    segmentedId: import("vue").Ref<string>;
    segmentedSize: import("vue").ComputedRef<"" | "default" | "small" | "large">;
    _disabled: import("vue").ComputedRef<boolean>;
    formItem: import("..").FormItemContext | undefined;
    inputId: import("vue").Ref<string | undefined>;
    isLabeledByFormItem: import("vue").ComputedRef<boolean>;
    segmentedRef: import("vue").Ref<HTMLElement | null>;
    activeElement: import("vue").ComputedRef<HTMLElement | null | undefined>;
    state: {
        isInit: boolean;
        width: number;
        translateX: number;
        disabled: boolean;
        focusVisible: boolean;
    };
    handleChange: (item: import("./src/types").Option) => void;
    getValue: (item: import("./src/types").Option) => string | number | boolean | undefined;
    getLabel: (item: import("./src/types").Option) => string | number | boolean | undefined;
    getDisabled: (item: import("./src/types").Option) => boolean;
    getSelected: (item: import("./src/types").Option) => boolean;
    getOption: (value: any) => import("./src/types").Option;
    getItemCls: (item: import("./src/types").Option) => string[];
    updateSelect: () => void;
    segmentedCls: import("vue").ComputedRef<string[]>;
    selectedStyle: import("vue").ComputedRef<{
        width: string;
        transform: string;
        display: string;
    }>;
    selectedCls: import("vue").ComputedRef<string[]>;
    name: import("vue").ComputedRef<string>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (val: any) => boolean;
    change: (val: any) => boolean;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    ariaLabel: StringConstructor;
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/types").Option[]) | (() => import("./src/types").Option[]) | ((new (...args: any[]) => import("./src/types").Option[]) | (() => import("./src/types").Option[]))[], unknown, unknown, () => never[], boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | BooleanConstructor | StringConstructor)[], unknown, unknown, undefined, boolean>;
    block: BooleanConstructor;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "default" | "small" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    id: StringConstructor;
    name: StringConstructor;
}>> & {
    onChange?: ((val: any) => any) | undefined;
    "onUpdate:modelValue"?: ((val: any) => any) | undefined;
}, {
    modelValue: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | BooleanConstructor | StringConstructor)[], unknown, unknown>;
    disabled: boolean;
    validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    block: boolean;
    options: import("./src/types").Option[];
}>> & Record<string, any>;
export default ElSegmented;
export * from './src/segmented';
