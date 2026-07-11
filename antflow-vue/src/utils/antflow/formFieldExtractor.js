/**
 * 从 VForm formdata JSON 中提取字段元数据列表。
 * 对应后端 LfFormWidgetParser,用于外部表单模式下在前端解析多表单字段。
 *
 * 容器类型: card / tab / table / grid (category === 'container')
 * 非容器: 直接的字段控件,含 options.name / options.label / type 等。
 */

const CONTAINER_TYPES = new Set(['card', 'tab', 'table', 'grid']);

/**
 * 解析 formdata JSON,返回 { formFields: [...] } 结构,与 VForm designer.getFormFieldJson() 保持一致。
 * @param {string|Object} formdataJson - 表单 JSON 字符串或已解析对象
 * @returns {{ formFields: Array }}
 */
export function extractFormFields(formdataJson) {
  if (!formdataJson) {
    return { formFields: [] };
  }
  let obj = formdataJson;
  if (typeof formdataJson === 'string') {
    try {
      obj = JSON.parse(formdataJson);
    } catch (e) {
      console.warn('extractFormFields: parse formdata json failed', e);
      return { formFields: [] };
    }
  }
  const widgetList = obj?.widgetList;
  if (!Array.isArray(widgetList)) {
    return { formFields: [] };
  }
  const result = [];
  walkWidgets(widgetList, result);
  return { formFields: result };
}

function walkWidgets(widgetList, result) {
  if (!Array.isArray(widgetList)) return;
  for (const widget of widgetList) {
    if (!widget || !widget.type) continue;
    if (CONTAINER_TYPES.has(widget.type)) {
      //容器: 递归子 widgetList
      if (widget.type === 'card') {
        walkWidgets(widget.widgetList, result);
      } else if (widget.type === 'tab') {
        const tabs = widget.tabs || [];
        for (const tab of tabs) {
          walkWidgets(tab.widgetList, result);
        }
      } else if (widget.type === 'table') {
        const rows = widget.rows || [];
        for (const row of rows) {
          const cols = row.cols || [];
          for (const col of cols) {
            walkWidgets(col.widgetList, result);
          }
        }
      } else if (widget.type === 'grid') {
        const cols = widget.cols || [];
        for (const col of cols) {
          walkWidgets(col.widgetList, result);
        }
      }
    } else {
      //非容器: 字段控件
      result.push(widget);
    }
  }
}

/**
 * 批量解析多个表单,返回 lowCodeFormFieldsMulti 所需结构。
 * @param {Array} formdataList - BpmnConfLfFormdata 列表,每项含 { id, formCode, formName, formdata }
 * @returns {Array} - 每项 { formdataId, formCode, formName, formFields }
 */
export function extractFormFieldsMulti(formdataList) {
  if (!Array.isArray(formdataList)) return [];
  return formdataList.map(form => {
    const { formFields } = extractFormFields(form.formdata);
    return {
      formdataId: form.id,
      formCode: form.formCode,
      formName: form.formName,
      formFields,
    };
  });
}
