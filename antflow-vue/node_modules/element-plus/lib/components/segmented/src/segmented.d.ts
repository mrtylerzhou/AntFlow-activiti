import type { Option } from './types';
import type { ExtractPropTypes } from 'vue';
import type Segmented from './segmented.vue';
export declare const segmentedProps: {
    ariaLabel: StringConstructor;
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Option[]) | (() => Option[]) | ((new (...args: any[]) => Option[]) | (() => Option[]))[], unknown, unknown, () => never[], boolean>;
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
};
export declare type SegmentedProps = ExtractPropTypes<typeof segmentedProps>;
export declare const segmentedEmits: {
    "update:modelValue": (val: any) => boolean;
    change: (val: any) => boolean;
};
export declare type SegmentedEmits = typeof segmentedEmits;
export declare type SegmentedInstance = InstanceType<typeof Segmented>;
