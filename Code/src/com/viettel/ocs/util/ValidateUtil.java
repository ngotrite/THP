package com.viettel.ocs.util;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.text.ParseException;
import java.util.Date;

public class ValidateUtil {

	public static Boolean checkStringNullOrEmpty(String str) {

		if (str == null) {
			return true;
		}

		if (str.isEmpty()) {
			return true;
		}

		return false;
	}
	//Check Email ---------------------------
	private static Pattern pattern;
	private static Matcher matcher;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static void validateEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		String email = (String) value;
		String mess = "";
		Boolean required = false;
		if (component.getAttributes().get("message") != null && !"".equals(component.getAttributes().get("message"))) {
			mess = component.getAttributes().get("message").toString();
		}
		if (component.getAttributes().get("require") != null && !"".equals(component.getAttributes().get("require"))) {
			required = (Boolean) component.getAttributes().get("require");
		}
		if (required || !email.isEmpty()) {
			if (!isEmail(email)) {
				FacesMessage message = new FacesMessage();
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				message.setSummary(mess);
				message.setDetail(mess);
				// context.addMessage(null, message);
				throw new ValidatorException(message);
			}
		}
	}

	/**
	 * Validate hex with regular expression
	 *
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public static boolean isEmail(final String hex) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(hex);
		return matcher.matches();

	}
	//Check Time ---------------------------
	private static final SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
    public void validateDateTime(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        String mess = "";
        Boolean required = false;
        Date dateMin = null;
        Date dateMax = null;
        Date dValidate = (Date) value;

        if (component.getAttributes().get("message") != null && component.getAttributes().get("message").toString().trim().length() > 0) {
            mess = component.getAttributes().get("message").toString();
        }
        if (component.getAttributes().get("require") != null && component.getAttributes().get("require").toString().trim().length() > 0) {
            required = (Boolean) component.getAttributes().get("require");
        }

        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        message.setSummary(mess);
        message.setDetail(mess);

        if (dValidate != null || required) {

            try {
                if (component.getAttributes().get("min") != null && !"".equals(component.getAttributes().get("min"))) {
                    dateMin = (Date) component.getAttributes().get("min");
                    dateMin = sf1.parse(sf1.format(dateMin));
                }

                if (component.getAttributes().get("max") != null && !"".equals(component.getAttributes().get("max"))) {
                    dateMax = (Date) component.getAttributes().get("max");
                    dateMax = sf1.parse(sf1.format(dateMax));
                }
                
                if (dateMax != null || dateMin != null) {
                    if (dValidate == null) {
                        throw new ValidatorException(message);
                    }else {
                        dValidate = sf1.parse(sf1.format(dValidate));
                    } 
                    if (dateMin != null && dateMin.after(dValidate)) {
                        throw new ValidatorException(message);
                    } else if (dateMax != null && dateMax.before(dValidate)) {
                        throw new ValidatorException(message);
                    }
                }
            } catch (ParseException | ValidatorException ex) {
                throw new ValidatorException(message);
            }
        }
    }
  //Check TextSize ---------------------------
    public void validateTextSize(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String txtValue = (String) value;
        int maxlength = 0;
        int minLength = 0;
        String mess = "";
        Boolean required = false;

        if (component.getAttributes().get("message") != null && !"".equals(component.getAttributes().get("message"))) {
            mess = component.getAttributes().get("message").toString();
        }
        if (component.getAttributes().get("max") != null && !"".equals(component.getAttributes().get("max"))) {
            maxlength = Integer.parseInt(component.getAttributes().get("max").toString());
        }
        if (component.getAttributes().get("min") != null && !"".equals(component.getAttributes().get("min"))) {
            minLength = Integer.parseInt(component.getAttributes().get("min").toString());
        }
        if (component.getAttributes().get("require") != null && !"".equals(component.getAttributes().get("require"))) {
            required = (Boolean) component.getAttributes().get("require");
        }
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        message.setSummary(mess);
        message.setDetail(mess);
        if (!txtValue.isEmpty() || required) {
            if (minLength != 0 && maxlength == 0) {
                if (txtValue.length() < minLength) {
                    throw new ValidatorException(message);
                }
            } else if (maxlength != 0 && minLength == 0) {
                if (txtValue.length() > maxlength) {
                    throw new ValidatorException(message);
                }
            } else if (maxlength != 0 && minLength != 0) {
                if (txtValue.length() < minLength || txtValue.length() > maxlength) {
                    throw new ValidatorException(message);
                }
            }
        }
    }
}
