// ObjectUtils.isObjectChanged(tar1, tar2)
// ObjectUtils.isObjectChangedSimple(tar1, tar2)
//import {  ObjectUtils } from '@/utils/antflow/ObjectUtils'
export class ObjectUtils {
    static getDataType(data) {
      const temp = Object.prototype.toString.call(data);
      const type = temp.match(/\b\w+\b/g);
      return (type.length < 2) ? 'Undefined' : type[1];
    }
    static iterable(data){
      return ['Object', 'Array'].includes(this.getDataType(data));
    }
    static  isObjectChangedSimple(source, comparison){
      const _source = JSON.stringify(source)
      const _comparison = JSON.stringify({...source,...comparison})
      return _source !== _comparison
    }
    /**
     * 比较两个对象是否相等
     * @param {*} source 
     * @param {*} comparison 
     * @returns true 不相等 false 相等
     */
    static isObjectChanged(source, comparison) { 
      if (!this.iterable(source)) {
        throw new Error(`source should be a Object or Array , but got ${this.getDataType(source)}`);
      }
      if (this.getDataType(source) !== this.getDataType(comparison)) {
        return true;
      }
      const sourceKeys = Object.keys(source);
      const comparisonKeys = Object.keys({...source, ...comparison});
      if (sourceKeys.length !== comparisonKeys.length) {
        return true;
      }
      return comparisonKeys.some(key => {
        if (this.iterable(source[key])) {
          return this.isObjectChanged(source[key], comparison[key]);
        } else {
          return source[key] !== comparison[key];
        }
      });
    }
  }

  