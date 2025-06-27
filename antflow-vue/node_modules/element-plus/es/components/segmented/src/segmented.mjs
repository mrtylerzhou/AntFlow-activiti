import '../../../utils/index.mjs';
import '../../../hooks/index.mjs';
import '../../../constants/index.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { useSizeProp } from '../../../hooks/use-size/index.mjs';
import { useAriaProps } from '../../../hooks/use-aria/index.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';
import { isString } from '@vue/shared';
import { isNumber } from '../../../utils/types.mjs';

const segmentedProps = buildProps({
  options: {
    type: definePropType(Array),
    default: () => []
  },
  modelValue: {
    type: [String, Number, Boolean],
    default: void 0
  },
  block: Boolean,
  size: useSizeProp,
  disabled: Boolean,
  validateEvent: {
    type: Boolean,
    default: true
  },
  id: String,
  name: String,
  ...useAriaProps(["ariaLabel"])
});
const segmentedEmits = {
  [UPDATE_MODEL_EVENT]: (val) => isString(val) || isNumber(val),
  [CHANGE_EVENT]: (val) => isString(val) || isNumber(val)
};

export { segmentedEmits, segmentedProps };
//# sourceMappingURL=segmented.mjs.map
