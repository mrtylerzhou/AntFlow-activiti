package org.openoa.engine.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public abstract class CloneUtils
{
	public static void copyFields(Object source, Object target, String... fieldNames)
	{
		assert source!=null;
		assert target!=null;
		assert source.getClass().equals(target.getClass());

		for (String fieldName : fieldNames)
		{
			try
			{
				Field field = FieldUtils.getField(source.getClass(), fieldName, true);
				field.setAccessible(true);
				field.set(target, field.get(source));
			}
			catch (Exception e)
			{
				Logger.getLogger(CloneUtils.class.getSimpleName()).warning(e.getMessage());
			}
		}
	}
}
