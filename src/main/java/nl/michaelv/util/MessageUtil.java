package nl.michaelv.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessageUtil implements MessageSourceAware {

	@Autowired
	private MessageSource messageSource;

	public String get(String label) {
		return get(label, (Object[]) null);
	}

	public String get(String label, String arg) {
		return get(label, new Object[]{arg});
	}

	public String get(String label, int arg) {
		return get(label, new Object[]{arg});
	}

	public String get(String label, Object[] args) {
		return messageSource.getMessage(label, args, LocaleContextHolder.getLocale());
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
