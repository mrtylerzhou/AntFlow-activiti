package org.openoa.engine.factory;

/**
 * this interface is used by antflow engine to parse tag for build up an adaptory factory
 * @param <TBean>
 * @param <TParam>
 * @author AntFlow
 * @since 0.5
 */
public interface TagParser<TBean,TParam> {
    TBean parseTag(TParam data);
}
