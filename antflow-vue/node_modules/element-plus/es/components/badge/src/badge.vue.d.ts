import type { StyleValue } from 'vue';
declare const _default: import("vue").DefineComponent<{
    readonly value: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, "", boolean>;
    readonly max: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 99, boolean>;
    readonly isDot: BooleanConstructor;
    readonly hidden: BooleanConstructor;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "danger", boolean>;
    readonly showZero: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly color: StringConstructor;
    readonly dotStyle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => StyleValue & {}) | (() => StyleValue) | ((new (...args: any[]) => StyleValue & {}) | (() => StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly badgeStyle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => StyleValue & {}) | (() => StyleValue) | ((new (...args: any[]) => StyleValue & {}) | (() => StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly offset: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => [number, number]) | (() => [number, number]) | ((new (...args: any[]) => [number, number]) | (() => [number, number]))[], unknown, unknown, readonly [0, 0], boolean>;
    readonly dotClass: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly badgeClass: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, {
    props: Readonly<import("@vue/shared").LooseRequired<Readonly<import("vue").ExtractPropTypes<{
        readonly value: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, "", boolean>;
        readonly max: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 99, boolean>;
        readonly isDot: BooleanConstructor;
        readonly hidden: BooleanConstructor;
        readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "danger", boolean>;
        readonly showZero: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
        readonly color: StringConstructor;
        readonly dotStyle: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => StyleValue & {}) | (() => StyleValue) | ((new (...args: any[]) => StyleValue & {}) | (() => StyleValue))[], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly badgeStyle: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => StyleValue & {}) | (() => StyleValue) | ((new (...args: any[]) => StyleValue & {}) | (() => StyleValue))[], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly offset: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => [number, number]) | (() => [number, number]) | ((new (...args: any[]) => [number, number]) | (() => [number, number]))[], unknown, unknown, readonly [0, 0], boolean>;
        readonly dotClass: {
            readonly type: import("vue").PropType<string>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly badgeClass: {
            readonly type: import("vue").PropType<string>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
    }>> & {
        [x: string & `on${string}`]: ((...args: any[]) => any) | ((...args: unknown[]) => any) | undefined;
    }>>;
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
    content: import("vue").ComputedRef<string>;
    style: import("vue").ComputedRef<StyleValue>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly value: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, "", boolean>;
    readonly max: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 99, boolean>;
    readonly isDot: BooleanConstructor;
    readonly hidden: BooleanConstructor;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "danger", boolean>;
    readonly showZero: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly color: StringConstructor;
    readonly dotStyle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => StyleValue & {}) | (() => StyleValue) | ((new (...args: any[]) => StyleValue & {}) | (() => StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly badgeStyle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => StyleValue & {}) | (() => StyleValue) | ((new (...args: any[]) => StyleValue & {}) | (() => StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly offset: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => [number, number]) | (() => [number, number]) | ((new (...args: any[]) => [number, number]) | (() => [number, number]))[], unknown, unknown, readonly [0, 0], boolean>;
    readonly dotClass: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly badgeClass: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>>, {
    readonly type: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown>;
    readonly offset: [number, number];
    readonly value: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>;
    readonly hidden: boolean;
    readonly max: number;
    readonly showZero: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly isDot: boolean;
}>;
export default _default;
