import type { ExtractPropTypes, StyleValue } from 'vue';
export declare const badgeProps: {
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
};
export declare type BadgeProps = ExtractPropTypes<typeof badgeProps>;
