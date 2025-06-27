'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

require('../../../utils/index.js');
require('../../../hooks/index.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-aria/index.js');

const tooltipV2Strategies = ["absolute", "fixed"];
const tooltipV2Placements = [
  "top-start",
  "top-end",
  "top",
  "bottom-start",
  "bottom-end",
  "bottom",
  "left-start",
  "left-end",
  "left",
  "right-start",
  "right-end",
  "right"
];
const tooltipV2ContentProps = runtime.buildProps({
  arrowPadding: {
    type: runtime.definePropType(Number),
    default: 5
  },
  effect: {
    type: String,
    default: ""
  },
  contentClass: String,
  placement: {
    type: runtime.definePropType(String),
    values: tooltipV2Placements,
    default: "bottom"
  },
  reference: {
    type: runtime.definePropType(Object),
    default: null
  },
  offset: {
    type: Number,
    default: 8
  },
  strategy: {
    type: runtime.definePropType(String),
    values: tooltipV2Strategies,
    default: "absolute"
  },
  showArrow: {
    type: Boolean,
    default: false
  },
  ...index.useAriaProps(["ariaLabel"])
});

exports.tooltipV2ContentProps = tooltipV2ContentProps;
//# sourceMappingURL=content.js.map
