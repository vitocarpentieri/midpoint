package com.myproject.mydomain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QMSystemConfiguration is a Querydsl query type for QMSystemConfiguration
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QMSystemConfiguration extends com.querydsl.sql.RelationalPathBase<QMSystemConfiguration> {

    private static final long serialVersionUID = -671857349;

    public static final QMSystemConfiguration mSystemConfiguration = new QMSystemConfiguration("M_SYSTEM_CONFIGURATION");

    public final StringPath nameNorm = createString("nameNorm");

    public final StringPath nameOrig = createString("nameOrig");

    public final StringPath oid = createString("oid");

    public final com.querydsl.sql.PrimaryKey<QMSystemConfiguration> constraintC1 = createPrimaryKey(oid);

    public final com.querydsl.sql.ForeignKey<QMObject> systemConfigurationFk = createForeignKey(oid, "OID");

    public QMSystemConfiguration(String variable) {
        super(QMSystemConfiguration.class, forVariable(variable), "PUBLIC", "M_SYSTEM_CONFIGURATION");
        addMetadata();
    }

    public QMSystemConfiguration(String variable, String schema, String table) {
        super(QMSystemConfiguration.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QMSystemConfiguration(String variable, String schema) {
        super(QMSystemConfiguration.class, forVariable(variable), schema, "M_SYSTEM_CONFIGURATION");
        addMetadata();
    }

    public QMSystemConfiguration(Path<? extends QMSystemConfiguration> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "M_SYSTEM_CONFIGURATION");
        addMetadata();
    }

    public QMSystemConfiguration(PathMetadata metadata) {
        super(QMSystemConfiguration.class, metadata, "PUBLIC", "M_SYSTEM_CONFIGURATION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(nameNorm, ColumnMetadata.named("NAME_NORM").withIndex(1).ofType(Types.VARCHAR).withSize(255));
        addMetadata(nameOrig, ColumnMetadata.named("NAME_ORIG").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(oid, ColumnMetadata.named("OID").withIndex(3).ofType(Types.VARCHAR).withSize(36).notNull());
    }

}

