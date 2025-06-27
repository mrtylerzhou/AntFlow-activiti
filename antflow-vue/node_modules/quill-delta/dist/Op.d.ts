import AttributeMap from './AttributeMap';
import Iterator from './Iterator';
interface Op {
    insert?: string | object;
    delete?: number;
    retain?: number;
    attributes?: AttributeMap;
}
declare namespace Op {
    function iterator(ops: Op[]): Iterator;
    function length(op: Op): number;
}
export default Op;
