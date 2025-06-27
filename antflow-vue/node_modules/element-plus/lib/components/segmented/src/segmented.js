'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

require('../../../utils/index.js');
require('../../../hooks/index.js');
require('../../../constants/index.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');
var index$1 = require('../../../hooks/use-aria/index.js');
var event = require('../../../constants/event.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const segmentedProps = runtime.buildProps({
  options: {
    type: runtime.definePropType(Array),
    default: () => []
  },
  modelValue: {
    type: [String, Number, Boolean],
    default: void 0
  },
  block: Boolean,
  size: index.useSizeProp,
  disabled: Boolean,
  validateEvent: {
    type: Boolean,
    default: true
  },
  id: String,
  name: String,
  ...index$1.useAriaProps(["ariaLabel"])
});
const segmentedEmits = {
  [event.UPDATE_MODEL_EVENT]: (val) => shared.isString(val) || types.isNumber(val),
  [event.CHANGE_EVENT]: (val) => shared.isString(val) || types.isNumber(val)
};

exports.segmentedEmits = segmentedEmits;
exports.segmentedProps = segmentedProps;
//# sourceMappingURL=segmented.js.map
