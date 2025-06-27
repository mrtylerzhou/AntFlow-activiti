import { AllowedComponentProps } from 'vue';
import { ComponentCustomProps } from 'vue';
import { ComponentOptionsMixin } from 'vue';
import { DefineComponent } from 'vue';
import Delta from 'quill-delta';
import { ExtractPropTypes } from 'vue';
import { PropType } from 'vue';
import Quill from 'quill';
import { QuillOptionsStatic } from 'quill';
import { Ref } from 'vue';
import { Sources } from 'quill';
import { VNodeProps } from 'vue';

declare type ContentPropType = string | Delta | undefined | null;

export { Delta }

declare type Module = {
    name: string;
    module: unknown;
    options?: object;
};

export { Quill }

export declare const QuillEditor: DefineComponent<    {
content: {
type: PropType<ContentPropType>;
};
contentType: {
type: PropType<"delta" | "html" | "text">;
default: string;
validator: (value: string) => boolean;
};
enable: {
type: BooleanConstructor;
default: boolean;
};
readOnly: {
type: BooleanConstructor;
default: boolean;
};
placeholder: {
type: StringConstructor;
required: false;
};
theme: {
type: PropType<"" | "snow" | "bubble">;
default: string;
validator: (value: string) => boolean;
};
toolbar: {
type: (StringConstructor | ObjectConstructor | ArrayConstructor)[];
required: false;
validator: (value: string | unknown) => boolean;
};
modules: {
type: PropType<Module | Module[]>;
required: false;
};
options: {
type: PropType<QuillOptionsStatic>;
required: false;
};
globalOptions: {
type: PropType<QuillOptionsStatic>;
required: false;
};
}, {
editor: Ref<Element | undefined>;
getEditor: () => Element;
getToolbar: () => Element;
getQuill: () => Quill;
getContents: (index?: number, length?: number) => string | Delta | undefined;
setContents: (content: ContentPropType, source?: Sources) => void;
getHTML: () => string;
setHTML: (html: string) => void;
pasteHTML: (html: string, source?: Sources) => void;
focus: () => void;
getText: (index?: number, length?: number) => string;
setText: (text: string, source?: Sources) => void;
reinit: () => void;
}, unknown, {}, {}, ComponentOptionsMixin, ComponentOptionsMixin, ("textChange" | "selectionChange" | "editorChange" | "update:content" | "focus" | "blur" | "ready")[], "textChange" | "selectionChange" | "editorChange" | "update:content" | "focus" | "blur" | "ready", VNodeProps & AllowedComponentProps & ComponentCustomProps, Readonly<ExtractPropTypes<    {
content: {
type: PropType<ContentPropType>;
};
contentType: {
type: PropType<"delta" | "html" | "text">;
default: string;
validator: (value: string) => boolean;
};
enable: {
type: BooleanConstructor;
default: boolean;
};
readOnly: {
type: BooleanConstructor;
default: boolean;
};
placeholder: {
type: StringConstructor;
required: false;
};
theme: {
type: PropType<"" | "snow" | "bubble">;
default: string;
validator: (value: string) => boolean;
};
toolbar: {
type: (StringConstructor | ObjectConstructor | ArrayConstructor)[];
required: false;
validator: (value: string | unknown) => boolean;
};
modules: {
type: PropType<Module | Module[]>;
required: false;
};
options: {
type: PropType<QuillOptionsStatic>;
required: false;
};
globalOptions: {
type: PropType<QuillOptionsStatic>;
required: false;
};
}>> & {
onTextChange?: ((...args: any[]) => any) | undefined;
onSelectionChange?: ((...args: any[]) => any) | undefined;
onEditorChange?: ((...args: any[]) => any) | undefined;
"onUpdate:content"?: ((...args: any[]) => any) | undefined;
onFocus?: ((...args: any[]) => any) | undefined;
onBlur?: ((...args: any[]) => any) | undefined;
onReady?: ((...args: any[]) => any) | undefined;
}, {
contentType: "delta" | "html" | "text";
enable: boolean;
readOnly: boolean;
theme: "" | "snow" | "bubble";
}>;

export { }
