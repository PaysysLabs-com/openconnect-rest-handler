package com.paysyslabs.bootstrap.rest.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paysyslabs.bootstrap.rest.entities.STANGenerator;
import com.paysyslabs.bootstrap.rest.enums.VariableFields;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;
import com.paysyslabs.bootstrap.rest.repo.STANGeneratorRepository;

import javassist.NotFoundException;

@Component
public class VariableFieldService {
	@Autowired
	STANGeneratorRepository stanRepo;

	public void getVariableFieldValue(ServiceRequest request, String field) throws Exception {
		switch (VariableFields.fromKey(field)) {
		case STAN:
		case REV_STAN:
			STANGenerator stan = new STANGenerator();
			stanRepo.deleteAll();
			stanRepo.save(stan);
			stanRepo.flush();
			request.getParameters().put(field, stan.getSTAN());
			break;
		case RRN:
		case REV_RRN:
			VariableFields stanField = VariableFields.STAN;
			if (field.equals(VariableFields.REV_RRN.getKey())) {
				stanField = VariableFields.REV_STAN;
			}

			if (!request.getParameters().containsKey(stanField.getKey())) {
				getVariableFieldValue(request, stanField.getKey());
			}

			String rrn = "";
			Date date = new Date();
			int year = Calendar.getInstance().get(Calendar.YEAR);
			DateFormat dayFormatter = new SimpleDateFormat("DDD");
			DateFormat hourFormatter = new SimpleDateFormat("HH");
			rrn = (year % 10) + dayFormatter.format(date) + hourFormatter.format(date)
					+ request.getParameters().get(stanField.getKey());
			request.getParameters().put(field, rrn);
			break;
		case CURRENT_DATE:
			long time = (new Date()).getTime();
			request.getParameters().put(field, Long.toString(time));
			break;
		default:
			throw new NotFoundException("Variable field '" + field + "' not found");
		}
		/*
		 * if (field.equals("stan")) { STANGenerator stan = new STANGenerator();
		 * stanRepo.deleteAll(); stanRepo.save(stan); request.getParameters().put(field,
		 * stan.getSTAN());
		 * 
		 * return; } else if (field.equals("rrn")) { if
		 * (!request.getParameters().containsKey("stan")) {
		 * getVariableFieldValue(request, "stan"); }
		 * 
		 * String rrn = ""; Date date = new Date(); int year =
		 * Calendar.getInstance().get(Calendar.YEAR); DateFormat dayFormatter = new
		 * SimpleDateFormat("DDD"); DateFormat hourFormatter = new
		 * SimpleDateFormat("HH"); rrn = (year % 10) + dayFormatter.format(date) +
		 * hourFormatter.format(date) + request.getParameters().get("stan");
		 * request.getParameters().put(field, rrn);
		 * 
		 * return; }
		 * 
		 * throw new NotFoundException("Variable field '" + field + "' not found");
		 */
	}
}
