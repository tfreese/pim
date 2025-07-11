// Created: 20.01.2017
package de.freese.pim.core.dao;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.sql.DataSource;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Basis-Implementierung eines DAOs.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDao implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JdbcTemplate jdbcTemplate;

    // private SequenceQueryExecutor sequenceQueryExecutor;

    private Function<String, String> sequenceQuery;

    @Override
    public void afterPropertiesSet() {
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate required");
        Objects.requireNonNull(sequenceQuery, "sequenceQuery required");

        // try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
        // SequenceQuery sequenceQuery = SequenceQuery.determineQuery(connection);
        // sequenceQueryExecutor = new SequenceQueryExecutor(sequenceQuery);
        // }
        //
        // Objects.requireNonNull(sequenceQueryExecutor, "sequenceQueryExecutor required");
    }

    @Resource
    public void setDataSource(final DataSource dataSource) {
        Objects.requireNonNull(dataSource, "dataSource required");

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Resource
    public void setSequenceQuery(final UnaryOperator<String> sequenceQuery) {
        this.sequenceQuery = Objects.requireNonNull(sequenceQuery, "sequenceQuery required");
    }

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected long getNextID(final String sequence) {
        final String sql = sequenceQuery.apply(sequence);

        final Long id = getJdbcTemplate().query(sql, rs -> {
            rs.next();
            return rs.getLong(1);
        });

        if (id == null) {
            throw new IllegalStateException("id is null");
        }

        return id;

        // try (Connection connection = getJdbcTemplate().getDataSource().getConnection();
        // Statement stmt = connection.createStatement();
        // ResultSet rs = stmt.executeQuery(sql)) {
        // rs.next();
        //
        // return rs.getLong(1);
        // }
        // catch (SQLException sex) {
        // throw new DataRetrievalFailureException("", sex);
        // }
    }
}
