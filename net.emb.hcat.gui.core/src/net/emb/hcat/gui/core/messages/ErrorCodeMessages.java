package net.emb.hcat.gui.core.messages;

import java.lang.reflect.Field;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * Class to display error code messages in a translated way.
 *
 * @author OT Piccolo
 */
public class ErrorCodeMessages {

	private static final Logger log = LoggerFactory.getLogger(ErrorCodeMessages.class);

	/**
	 * Gets the translated error code message from the given error code.
	 *
	 * @param e
	 *            The error code exception to translate.
	 * @return The translated message.
	 */
	public static final String getErrorCodeMessage(final ErrorCodeException e) {
		if (e == null || e.getErrorCode() == null) {
			final NullPointerException npe = new NullPointerException("Null value error code exception."); //$NON-NLS-1$
			log.error(npe.getMessage(), npe);
			return ""; //$NON-NLS-1$
		}

		switch (e.getErrorCode()) {
		case SEQUENCE_WRONG_LENGTH:
			return getSeqWrongLength(e);

		case SEQUENCE_WRONG_NAME:
			return getSeqWrongName(e);

		default:
			return getDefaultMessage(e);
		}
	}

	private static final String getSeqWrongLength(final ErrorCodeException e) {
		final Sequence seq = (Sequence) e.getValues()[0];
		final int lineNumber = ((Integer) e.getValues()[1]).intValue() + 1;
		final Integer expLength = ((Integer) e.getValues()[2]);
		return MessageFormat.format(Messages.ErrorCodeException_SEQUENCE_WRONG_LENGTH, seq.getName(), lineNumber, expLength, seq.getLength());
	}

	private static final String getSeqWrongName(final ErrorCodeException e) {
		final String name = ((Sequence) e.getValues()[0]).getName();
		final int lineNumber = ((Integer) e.getValues()[1]).intValue() + 1;
		final Integer length = ((Integer) e.getValues()[2]);
		return MessageFormat.format(Messages.ErrorCodeException_SEQUENCE_WRONG_NAME, name, lineNumber, length);
	}

	private static final String getDefaultMessage(final ErrorCodeException e) {
		try {
			final Field field = Messages.class.getField("ErrorCodeException_" + e.getErrorCode().name()); //$NON-NLS-1$
			final String value = (String) field.get(null);
			return MessageFormat.format(value, e.getValues());
		} catch (final Exception e1) {
			log.error(e1.getMessage(), e1);
			return "!ErrorCodeException_" + e.getErrorCode().name() + "!"; //$NON-NLS-1$//$NON-NLS-2$
		}
	}

}
