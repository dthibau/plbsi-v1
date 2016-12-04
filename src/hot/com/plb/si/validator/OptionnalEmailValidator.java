package com.plb.si.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.plb.si.util.Labels;

@FacesValidator(value="optionalEmailValidator")
public class OptionnalEmailValidator implements Validator {
	public static String EMAIL_FORMAT="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		String sValue=(String)value;
		if ( value != null && sValue.length() > 0 && !sValue.matches(EMAIL_FORMAT)) {
			throw new ValidatorException(new FacesMessage(Labels.getString("error.invalidEmail")));
		}

	}

}
