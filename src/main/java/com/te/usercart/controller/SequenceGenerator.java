package com.te.usercart.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class SequenceGenerator extends SequenceStyleGenerator {
	private String valuePrefix;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static final String NUMBER_FORMAT_PARAMETER = "";
	private String numberFormat;

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return valuePrefix + String.format(numberFormat, super.generate(session, object));
	}

	int x = 0;

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		LocalDate localDate = LocalDate.now();
		if (x != 0 && localDate.minusDays(1).getDayOfMonth() != LocalDate.now().getMonthValue())
			jdbcTemplate.update("update " + params.get("GENERATOR_NAME") + " set next_val = ?", 1);

		super.configure(LongType.INSTANCE, params, serviceRegistry);

		valuePrefix = ConfigurationHelper.getString(
				getEntityName(params.get("jpa_entity_name").toString()) + localDate.getYear()
						+ String.format("%02d", localDate.getMonthValue()),
				params, getEntityName(params.get("jpa_entity_name").toString()) + localDate.getYear()
						+ String.format("%02d", localDate.getMonthValue()));
		numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER, params, "%d");
		x = 1;
	}

	private String getEntityName(String entityName) {
		char[] ch = entityName.toCharArray();
		String s = "";
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] < 97 && ch[i] > 64) 
				s += ch[i];
		}
		return s;
	}

}
