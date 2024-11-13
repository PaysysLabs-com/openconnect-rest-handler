package com.paysyslabs.bootstrap.rest.generator;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class GenericStringSequenceGenerator implements IdentifierGenerator, Configurable {

    public static final String SEQUENCE_NAME = "sequence_name";
    public static final String SEQUENCE_PREFIX = "sequence_prefix";
    public static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    public static final String NUMBER_FORMAT_DEFAULT = "%d";

    private String sequencePrefix;
    private String sequenceCallSyntax;
    private String numberFormat;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        final JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        final Dialect dialect = jdbcEnvironment.getDialect();

        sequencePrefix = ConfigurationHelper.getString(SEQUENCE_PREFIX, params, "");

        sequenceCallSyntax = dialect.getSequenceNextValString(ConfigurationHelper
                .getString(SequenceStyleGenerator.SEQUENCE_PARAM, params, SequenceStyleGenerator.DEF_SEQUENCE_NAME));

        numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER, params, NUMBER_FORMAT_DEFAULT);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        long seqValue = ((Number) Session.class.cast(session).createNativeQuery(sequenceCallSyntax).uniqueResult())
                .longValue();
        return sequencePrefix + String.format(numberFormat, seqValue);
    }
}
