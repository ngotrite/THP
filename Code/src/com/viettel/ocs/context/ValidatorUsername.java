package com.viettel.ocs.context;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.viettel.ocs.util.LocaleUtils;

@FacesValidator("ValidatorUsername")
public class ValidatorUsername implements Validator{

	private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{6,30}$";

	private Pattern pattern;
	private Matcher matcher;

	public ValidatorUsername(){
		  pattern = Pattern.compile(USERNAME_PATTERN);
	}

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		
		matcher = pattern.matcher(value.toString());
		if(!matcher.matches()){

			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			FacesMessage msg =new FacesMessage(LocaleUtils.getString(locale, "common.validationFail"), LocaleUtils.getString(locale, "user.formatUserName"));
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);

		}
	}
}