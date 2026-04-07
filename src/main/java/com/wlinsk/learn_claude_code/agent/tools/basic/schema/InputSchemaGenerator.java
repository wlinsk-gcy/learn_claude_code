package com.wlinsk.learn_claude_code.agent.tools.basic.schema;

import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Generates a minimal JSON schema from tool argument types.
 *
 * @author wlinsk
 * @date 2026/4/4
 */
@Component
public class InputSchemaGenerator {

    public Map<String, Object> generate(Type argsType) {
        return toSchema(argsType);
    }

    private Map<String, Object> toSchema(Type type) {
        if (type instanceof Class<?> clazz) {
            return schemaForClass(clazz);
        }
        if (type instanceof ParameterizedType parameterizedType) {
            return schemaForParameterizedType(parameterizedType);
        }
        if (type instanceof WildcardType wildcardType) {
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 0) {
                throw unsupported(type);
            }
            return toSchema(upperBounds[0]);
        }
        if (type instanceof TypeVariable<?> typeVariable) {
            Type[] bounds = typeVariable.getBounds();
            if (bounds.length == 0) {
                throw unsupported(type);
            }
            return toSchema(bounds[0]);
        }
        throw unsupported(type);
    }

    private Map<String, Object> schemaForParameterizedType(ParameterizedType type) {
        Type rawType = type.getRawType();
        if (rawType instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
            Map<String, Object> schema = new LinkedHashMap<>();
            schema.put("type", "array");
            schema.put("items", toSchema(type.getActualTypeArguments()[0]));
            return schema;
        }
        throw unsupported(type);
    }

    private Map<String, Object> schemaForClass(Class<?> clazz) {
        if (String.class.equals(clazz) || Character.class.equals(clazz) || char.class.equals(clazz)) {
            return typeSchema("string");
        }
        if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            return typeSchema("boolean");
        }
        if (Integer.class.equals(clazz) || int.class.equals(clazz)
                || Long.class.equals(clazz) || long.class.equals(clazz)
                || Short.class.equals(clazz) || short.class.equals(clazz)
                || Byte.class.equals(clazz) || byte.class.equals(clazz)) {
            return typeSchema("integer");
        }
        if (Double.class.equals(clazz) || double.class.equals(clazz)
                || Float.class.equals(clazz) || float.class.equals(clazz)) {
            return typeSchema("number");
        }
        if (clazz.isEnum()) {
            Map<String, Object> schema = typeSchema("string");
            List<String> enumValues = Arrays.stream(clazz.getEnumConstants())
                    .map(Objects::toString)
                    .toList();
            schema.put("enum", enumValues);
            return schema;
        }
        if (List.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Raw List is not supported, use a parameterized List<T> instead.");
        }
        return objectSchema(clazz);
    }

    private Map<String, Object> objectSchema(Class<?> clazz) {
        Map<String, Object> schema = new LinkedHashMap<>();
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                    || java.lang.reflect.Modifier.isTransient(field.getModifiers())
                    || field.isSynthetic()) {
                continue;
            }

            Map<String, Object> fieldSchema = new LinkedHashMap<>(toSchema(field.getGenericType()));
            ToolParam toolParam = field.getAnnotation(ToolParam.class);
            if (toolParam != null && !toolParam.description().isBlank()) {
                fieldSchema.put("description", toolParam.description());
            }

            properties.put(field.getName(), fieldSchema);

            if (toolParam == null || toolParam.required()) {
                required.add(field.getName());
            }
        }

        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", required);
        schema.put("additionalProperties", false);
        return schema;
    }

    private Map<String, Object> typeSchema(String type) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", type);
        return schema;
    }

    private IllegalArgumentException unsupported(Type type) {
        return new IllegalArgumentException("Unsupported tool argument type: " + type.getTypeName());
    }
}
