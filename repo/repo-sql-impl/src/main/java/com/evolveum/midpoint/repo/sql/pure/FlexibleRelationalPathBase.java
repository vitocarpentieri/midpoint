package com.evolveum.midpoint.repo.sql.pure;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.sql.RelationalPathBase;

/**
 * Extension of {@link RelationalPathBase} that provides midPoint specific features.
 * This allows to add metadata about Prism objects in addition to pure DB metadata.
 * <p>
 * Typical migration from originally generated Querydsl class:
 * <ul>
 * <li>extend from this class instead of {@code RelationalPathBase}</li>
 * <li>rename fields as needed (missing uppercase for words), also in related bean</li>
 * <li>rename static final field to capitalized table name (this will stand out + fix checkstyle)</li>
 * <li>simplify bean to public fields and no setters/getters</li>
 * <li>add PK-based equals/hashCode to beans (not critical, but handy for grouping transformations)</li>
 * <li>TODO: add prism-related metadata?</li>
 * <li>TODO: change types as needed, e.g. date/time-related? (also in bean)</li>
 * </ul>
 *
 * @param <T> entity type - typically a pure DTO bean for the table mapped by Q-type
 */
public class FlexibleRelationalPathBase<T> extends RelationalPathBase<T> {

    private static final long serialVersionUID = -3374516272567011096L;

    public FlexibleRelationalPathBase(
            Class<? extends T> type, String variable, String schema, String table) {
        super(type, variable, schema, table);
    }

    public FlexibleRelationalPathBase(
            Class<? extends T> type, PathMetadata metadata, String schema, String table) {
        super(type, metadata, schema, table);
    }
}