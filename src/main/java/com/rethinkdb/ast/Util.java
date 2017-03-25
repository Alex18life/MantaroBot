package com.rethinkdb.ast;

import com.rethinkdb.gen.ast.*;
import com.rethinkdb.gen.exc.ReqlDriverCompileError;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.model.Arguments;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.model.ReqlLambda;
import com.rethinkdb.serialization.SerializationStrategy;
import com.rethinkdb.serialization.Strategy;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map;

public class Util {
	/**
	 * Converts a POJO to a map of its public properties collected using bean introspection.<br>
	 * The POJO's class must be public, or a ReqlDriverError would be thrown.<br>
	 * Numeric properties should be Long instead of Integer
	 *
	 * @param pojo POJO to be introspected
	 * @return Map of POJO's public properties
	 */
	private static Map<String, Object> toMap(Object pojo) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			Class<?> pojoClass = pojo.getClass();

			if (pojoClass.isAnnotationPresent(Strategy.class)) {
				Strategy strategy = pojoClass.getAnnotation(Strategy.class);
				if (!strategy.serialization().equals(SerializationStrategy.class)) {
					return strategy.serialization().newInstance().serialize(pojo);
				}
			}

			if (!Modifier.isPublic(pojoClass.getModifiers())) {
				throw new IllegalAccessException(String.format("%s's class should be public", pojo));
			}

			BeanInfo info = Introspector.getBeanInfo(pojoClass);

			for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
				Method reader = descriptor.getReadMethod();

				if (reader == null || reader.isAnnotationPresent(Transient.class) && reader.getAnnotation(Transient.class).value())
					continue;

				Object value = reader.invoke(pojo);

				if (value instanceof Integer) {
					throw new IllegalAccessException(String.format(
						"Make %s of %s Long instead of Integer", reader.getName(), pojo));
				}

				map.put(descriptor.getName(), value);
			}

			return map;
		} catch (IntrospectionException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new ReqlDriverError("Can't convert %s to a ReqlAst: %s", pojo, e.getMessage());
		}
	}

	private static ReqlAst toReqlAst(Object val, int remainingDepth) {
		if (remainingDepth <= 0) {
			throw new ReqlDriverCompileError("Recursion limit reached converting to ReqlAst");
		}
		if (val instanceof ReqlAst) {
			return (ReqlAst) val;
		}

		if (val instanceof Object[]) {
			Arguments innerValues = new Arguments();
			for (Object innerValue : Arrays.asList((Object[]) val)) {
				innerValues.add(toReqlAst(innerValue, remainingDepth - 1));
			}
			return new MakeArray(innerValues, null);
		}

		if (val instanceof List) {
			Arguments innerValues = new Arguments();
			for (Object innerValue : (List) val) {
				innerValues.add(toReqlAst(innerValue, remainingDepth - 1));
			}
			return new MakeArray(innerValues, null);
		}

		if (val instanceof Map) {
			Map<String, ReqlAst> obj = new MapObject();
			for (Map.Entry<Object, Object> entry : (Set<Map.Entry>) ((Map) val).entrySet()) {
				if (!(entry.getKey() instanceof String)) {
					throw new ReqlDriverCompileError("Object keys can only be strings");
				}

				obj.put((String) entry.getKey(), toReqlAst(entry.getValue()));
			}
			return MakeObj.fromMap(obj);
		}

		if (val instanceof ReqlLambda) {
			return Func.fromLambda((ReqlLambda) val);
		}

		final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

		if (val instanceof LocalDateTime) {
			ZoneId zid = ZoneId.systemDefault();
			DateTimeFormatter fmt2 = fmt.withZone(zid);
			return Iso8601.fromString(((LocalDateTime) val).format(fmt2));
		}
		if (val instanceof ZonedDateTime) {
			return Iso8601.fromString(((ZonedDateTime) val).format(fmt));
		}
		if (val instanceof OffsetDateTime) {
			return Iso8601.fromString(((OffsetDateTime) val).format(fmt));
		}

		if (val instanceof Integer) {
			return new Datum((Integer) val);
		}

		if (val instanceof Number) {
			return new Datum((Number) val);
		}

		if (val instanceof Boolean) {
			return new Datum((Boolean) val);
		}

		if (val instanceof String) {
			return new Datum((String) val);
		}

		if (val == null) {
			return new Datum(null);
		}

		// val is a non-null POJO, let's introspect its public properties
		return toReqlAst(toMap(val));
	}

	/**
	 * Coerces objects from their native type to ReqlAst
	 *
	 * @param val val
	 * @return ReqlAst
	 */
	public static ReqlAst toReqlAst(Object val) {
		return toReqlAst(val, 100);
	}

	public static ReqlExpr toReqlExpr(Object val) {
		ReqlAst converted = toReqlAst(val);
		if (converted instanceof ReqlExpr) {
			return (ReqlExpr) converted;
		} else {
			throw new ReqlDriverError("Cannot convert %s to ReqlExpr", val);
		}
	}

	private Util() {
	}
}
