package io.pallas.core.view.form;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import io.pallas.core.cdi.CdiBeans;
import io.pallas.core.http.HttpRequest;
import io.pallas.core.util.Json;
import io.pallas.core.validation.Validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Helper to manage HTML form description, submission and validation.
 */
public class Form<T> implements Map<String, String> {

	// -- Form utilities

	/**
	 * Instantiates a new form that wraps the specified class.
	 */
	public static <T> Form<T> from(final Class<T> clazz) {
		return new Form<T>(clazz);
	}

	/**
	 * Instantiates a new form that wraps the specified class.
	 */
	public static <T> Form<T> from(final String name, final Class<T> clazz) {
		return new Form<T>(name, clazz);
	}

	/**
	 * Instantiates a new form that wraps the specified class.
	 */
	public static <T> Form<T> from(final String name, final Class<T> clazz, final Class<?> group) {
		return new Form<T>(name, clazz, group);
	}

	/**
	 * Instantiates a new form that wraps the specified class.
	 */
	public static <T> Form<T> from(final Class<T> clazz, final Class<?> group) {
		return new Form<T>(null, clazz, group);
	}

	/**
	 * Defines a form element's display name.
	 */
	@Retention(RUNTIME)
	@Target({ ANNOTATION_TYPE })
	public static @interface Display {
		String name();

		String[] attributes() default {};
	}

	// --

	private final String rootName;
	private final Class<T> backedType;
	private final Map<String, String> data;
	private final Map<String, List<ValidationError>> errors;
	private final Optional<T> value;
	private final Class<?> groups;

	private T blankInstance() {
		try {
			return backedType.newInstance();
		} catch (final Exception e) {
			throw new RuntimeException("Cannot instantiate " + backedType + ". It must have a default constructor", e);
		}
	}

	/**
	 * Creates a new <code>Form</code>.
	 *
	 * @param clazz
	 *            wrapped class
	 */
	public Form(final Class<T> clazz) {
		this(null, clazz);
	}

	public Form(final String name, final Class<T> clazz) {
		this(name, clazz, new HashMap<String, String>(), new HashMap<String, List<ValidationError>>(), Optional.<T> absent(), null);
	}

	public Form(final String name, final Class<T> clazz, final Class<?> groups) {
		this(name, clazz, new HashMap<String, String>(), new HashMap<String, List<ValidationError>>(), Optional.<T> absent(), groups);
	}

	public Form(final String rootName, final Class<T> clazz, final Map<String, String> data, final Map<String, List<ValidationError>> errors, final Optional<T> value) {
		this(rootName, clazz, data, errors, value, null);
	}

	/**
	 * Creates a new <code>Form</code>.
	 *
	 * @param clazz
	 *            wrapped class
	 * @param data
	 *            the current form data (used to display the form)
	 * @param errors
	 *            the collection of errors associated with this form
	 * @param value
	 *            optional concrete value of type <code>T</code> if the form submission was successful
	 */
	public Form(final String rootName, final Class<T> clazz, final Map<String, String> data, final Map<String, List<ValidationError>> errors, final Optional<T> value,
	        final Class<?> groups) {
		this.rootName = rootName;
		this.backedType = clazz;
		this.data = data;
		this.errors = errors;
		this.value = value;
		this.groups = groups;
	}

	/**
	 * Retrieves the actual form data.
	 */
	public Map<String, String> data() {
		return data;
	}

	public String name() {
		return rootName;
	}

	/**
	 * Retrieves the actual form value.
	 */
	public Optional<T> value() {
		return value;
	}

	/**
	 * Populates this form with an existing value, used for edit forms.
	 *
	 * @param value
	 *            existing value of type <code>T</code> used to fill this form
	 * @return a copy of this form filled with the new data
	 */
	public Form<T> fill(final T value) {
		if (value == null) {
			throw new RuntimeException("Cannot fill a form with a null value");
		}
		return new Form(rootName, backedType, new HashMap<String, String>(), new HashMap<String, ValidationError>(), Optional.of(value));
	}

	/**
	 * Returns <code>true<code> if there are any errors related to this form.
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * Returns <code>true<code> if there any global errors related to this form.
	 */
	public boolean hasGlobalErrors() {
		return errors.containsKey("") && !errors.get("").isEmpty();
	}

	/**
	 * Retrieve all global errors - errors without a key.
	 *
	 * @return All global errors.
	 */
	public List<ValidationError> globalErrors() {
		List<ValidationError> e = errors.get("");
		if (e == null) {
			e = new ArrayList<ValidationError>();
		}
		return e;
	}

	/**
	 * Retrieves the first global error (an error without any key), if it exists.
	 *
	 * @return An error or <code>null</code>.
	 */
	public ValidationError globalError() {
		final List<ValidationError> errors = globalErrors();
		if (errors.isEmpty()) {
			return null;
		} else {
			return errors.get(0);
		}
	}

	/**
	 * Returns all errors.
	 *
	 * @return All errors associated with this form.
	 */
	public Map<String, List<ValidationError>> errors() {
		return errors;
	}

	/**
	 * Retrieve an error by key.
	 */
	public ValidationError error(final String key) {
		final List<ValidationError> err = errors.get(key);
		if (err == null || err.isEmpty()) {
			return null;
		} else {
			return err.get(0);
		}
	}

	/**
	 * Returns the form errors serialized as Json.
	 */
	public org.codehaus.jackson.JsonNode errorsAsJson() {

		return errorsAsJson(Locale.getDefault());
	}

	/**
	 * @return JSON string of errors
	 */
	public String errorsAsJsonString() {
		return Json.create().stringify(errorsAsJson());
	}

	/**
	 * Returns the form errors serialized as Json using the given Lang.
	 */
	public org.codehaus.jackson.JsonNode errorsAsJson(final Locale lang) {
		final Map<String, List<String>> allMessages = new HashMap<String, List<String>>();

		for (final String key : errors.keySet()) {
			final List<ValidationError> errs = errors.get(key);
			if (errs != null && !errs.isEmpty()) {
				final List<String> messages = new ArrayList<String>();
				for (final ValidationError error : errs) {
					//messages.add(play.i18n.Messages.get(lang, error.message(), error.arguments()));
					messages.add(error.message());
				}
				allMessages.put(key, messages);
			}
		}
		return Json.create().toJson(allMessages);
	}

	/**
	 * Gets the concrete value if the submission was a success.
	 */
	public T get() {
		return value.get();
	}

	/**
	 * Adds an error to this form.
	 *
	 * @param error
	 *            the <code>ValidationError</code> to add.
	 */
	public void reject(final ValidationError error) {
		if (!errors.containsKey(error.key())) {
			errors.put(error.key(), new ArrayList<ValidationError>());
		}
		errors.get(error.key()).add(error);
	}

	/**
	 * Adds an error to this form.
	 *
	 * @param key
	 *            the error key
	 * @param error
	 *            the error message
	 * @param args
	 *            the error arguments
	 */
	public void reject(final String key, final String error, final List<Object> args) {
		reject(new ValidationError(key, error, args));
	}

	/**
	 * Adds an error to this form.
	 *
	 * @param key
	 *            the error key
	 * @param error
	 *            the error message
	 */
	public void reject(final String key, final String error) {
		reject(key, error, new ArrayList<Object>());
	}

	/**
	 * Adds a global error to this form.
	 *
	 * @param error
	 *            the error message
	 * @param args
	 *            the errot arguments
	 */
	public void reject(final String error, final List<Object> args) {
		reject(new ValidationError("", error, args));
	}

	/**
	 * Add a global error to this form.
	 *
	 * @param error
	 *            the error message.
	 */
	public void reject(final String error) {
		reject("", error, new ArrayList<Object>());
	}

	/**
	 * Discard errors of this form
	 */
	public void discardErrors() {
		errors.clear();
	}

	protected Map<String, String> requestData(final HttpRequest request) {

		Map<String, String[]> urlFormEncoded = new HashMap<String, String[]>();
		if (request.isFormUrlEncoded()) {
			urlFormEncoded = request.body().asFormUrlEncoded();
		}

		Map<String, String[]> multipartFormData = new HashMap<String, String[]>();
		if (request.isMultipartFormData()) {
			multipartFormData = request.body().asMultipartFormData().asFormUrlEncoded();
		}

		Map<String, String> jsonData = new HashMap<String, String>();
		if (request.isJson()) {
			jsonData = Json.create().toMap(request.body().asJson());
		}

		final Map<String, String[]> queryString = request.queryStringData();

		final Map<String, String> data = new HashMap<String, String>();

		for (final String key : urlFormEncoded.keySet()) {
			final String[] values = urlFormEncoded.get(key);
			if (key.endsWith("[]")) {
				final String k = key.substring(0, key.length() - 2);
				for (int i = 0; i < values.length; i++) {
					data.put(k + "[" + i + "]", values[i]);
				}
			} else {
				if (values.length > 0) {
					data.put(key, values[0]);
				}
			}
		}

		for (final String key : multipartFormData.keySet()) {
			final String[] values = multipartFormData.get(key);
			if (key.endsWith("[]")) {
				final String k = key.substring(0, key.length() - 2);
				for (int i = 0; i < values.length; i++) {
					data.put(k + "[" + i + "]", values[i]);
				}
			} else {
				if (values.length > 0) {
					data.put(key, values[0]);
				}
			}
		}

		for (final String key : jsonData.keySet()) {
			data.put(key, jsonData.get(key));
		}

		for (final String key : queryString.keySet()) {
			final String[] values = queryString.get(key);
			if (key.endsWith("[]")) {
				final String k = key.substring(0, key.length() - 2);
				for (int i = 0; i < values.length; i++) {
					data.put(k + "[" + i + "]", values[i]);
				}
			} else {
				if (values.length > 0) {
					data.put(key, values[0]);
				}
			}
		}

		return data;
	}

	/**
	 * Binds request data to this form - that is, handles form submission.
	 *
	 * @return a copy of this form filled with the new data
	 */
	public Form<T> bindFromRequest(final String... allowedFields) {
		return bind(requestData(CdiBeans.of(HttpRequest.class)), allowedFields);
	}

	/**
	 * Binds request data to this form - that is, handles form submission.
	 *
	 * @return a copy of this form filled with the new data
	 */
	public Form<T> bindFromRequest(final HttpRequest request, final String... allowedFields) {
		return bind(requestData(request), allowedFields);
	}

	/**
	 * Binds request data to this form - that is, handles form submission.
	 *
	 * @return a copy of this form filled with the new data
	 */
	public Form<T> bindFromRequest(final Map<String, String[]> requestData, final String... allowedFields) {
		final Map<String, String> data = new HashMap<String, String>();
		for (final String key : requestData.keySet()) {
			final String[] values = requestData.get(key);
			if (key.endsWith("[]")) {
				final String k = key.substring(0, key.length() - 2);
				for (int i = 0; i < values.length; i++) {
					data.put(k + "[" + i + "]", values[i]);
				}
			} else {
				if (values.length > 0) {
					data.put(key, values[0]);
				}
			}
		}
		return bind(data, allowedFields);
	}

	/**
	 * Binds Json data to this form - that is, handles form submission.
	 *
	 * @param data
	 *            data to submit
	 * @return a copy of this form filled with the new data
	 */
	public Form<T> bind(final JsonNode data, final String... allowedFields) {
		return bind(Json.create().toMap(data), allowedFields);
	}

	/**
	 * Binds data to this form - that is, handles form submission.
	 *
	 * @param data
	 *            data to submit
	 * @return a copy of this form filled with the new data
	 */
	@SuppressWarnings("unchecked")
	public Form<T> bind(final Map<String, String> data, final String... allowedFields) {

		DataBinder dataBinder = null;
		Map<String, String> objectData = data;
		if (rootName == null) {
			dataBinder = new DataBinder(blankInstance());
		} else {
			dataBinder = new DataBinder(blankInstance(), rootName);
			objectData = new HashMap<String, String>();
			for (final String key : data.keySet()) {
				if (key.startsWith(rootName + ".")) {
					objectData.put(key.substring(rootName.length() + 1), data.get(key));
				}
			}
		}
		if (allowedFields.length > 0) {
			dataBinder.setAllowedFields(allowedFields);
		}

		final Validator validator = CdiBeans.of(Validation.class).validator();

		dataBinder.bind(new MutablePropertyValues(objectData));
		Set<ConstraintViolation<Object>> validationErrors;
		if (groups != null) {
			validationErrors = validator.validate(dataBinder.getTarget(), groups);
		} else {
			validationErrors = validator.validate(dataBinder.getTarget());
		}

		final BindingResult result = dataBinder.getBindingResult();

		for (final ConstraintViolation<Object> violation : validationErrors) {
			final String field = violation.getPropertyPath().toString();
			final FieldError fieldError = result.getFieldError(field);
			if (fieldError == null || !fieldError.isBindingFailure()) {
				try {
					result.rejectValue(field, violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
					        getArgumentsForConstraint(result.getObjectName(), field, violation.getConstraintDescriptor()), violation.getMessage());
				} catch (final NotReadablePropertyException ex) {
					throw new IllegalStateException("JSR-303 validated property '" + field + "' does not have a corresponding accessor for data binding - "
					        + "check your DataBinder's configuration (bean property versus direct field access)", ex);
				}
			}
		}

		if (result.hasErrors() || result.getGlobalErrorCount() > 0) {
			final Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
			for (final FieldError error : (List<FieldError>) result.getFieldErrors()) {
				String key = error.getObjectName() + "." + error.getField();
				if (key.startsWith("target.") && rootName == null) {
					key = key.substring(7);
				}
				if (!errors.containsKey(key)) {
					errors.put(key, new ArrayList<ValidationError>());
				}

				ValidationError validationError = null;
				if (error.isBindingFailure()) {
					final ImmutableList.Builder<String> builder = ImmutableList.builder();
					for (final String code : error.getCodes()) {
						builder.add(code.replace("typeMismatch", "error.invalid"));
					}
					validationError = new ValidationError(key, builder.build(), convertErrorArguments(error.getArguments()));
				} else {
					validationError = new ValidationError(key, error.getDefaultMessage(), convertErrorArguments(error.getArguments()));
				}
				errors.get(key).add(validationError);
			}

			final List<ValidationError> globalErrors = new ArrayList<ValidationError>();

			for (final ObjectError error : (List<ObjectError>) result.getGlobalErrors()) {
				globalErrors.add(new ValidationError("", error.getDefaultMessage(), convertErrorArguments(error.getArguments())));
			}

			if (!globalErrors.isEmpty()) {
				errors.put("", globalErrors);
			}

			return new Form<T>(rootName, backedType, data, errors, Optional.<T> absent(), groups);
		} else {
			Object globalError = null;
			if (result.getTarget() != null) {
				try {
					final java.lang.reflect.Method v = result.getTarget().getClass().getMethod("validate");
					globalError = v.invoke(result.getTarget());
				} catch (final NoSuchMethodException e) {
				} catch (final Throwable e) {
					throw new RuntimeException(e);
				}
			}
			if (globalError != null) {
				Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
				if (globalError instanceof String) {
					errors.put("", new ArrayList<ValidationError>());
					errors.get("").add(new ValidationError("", (String) globalError, new ArrayList<Object>()));
				} else if (globalError instanceof List) {
					for (final ValidationError error : (List<ValidationError>) globalError) {
						List<ValidationError> errorsForKey = errors.get(error.key());
						if (errorsForKey == null) {
							errors.put(error.key(), errorsForKey = new ArrayList<ValidationError>());
						}
						errorsForKey.add(error);
					}
				} else if (globalError instanceof Map) {
					errors = (Map<String, List<ValidationError>>) globalError;
				}
				return new Form<T>(rootName, backedType, data, errors, Optional.<T> absent(), groups);
			}
			return new Form<T>(rootName, backedType, new HashMap<String, String>(data), new HashMap<String, List<ValidationError>>(errors), Optional.of((T) result.getTarget()),
			        groups);
		}
	}

	private static final Set<String> internalAnnotationAttributes = new HashSet<String>(3);
	static {
		internalAnnotationAttributes.add("message");
		internalAnnotationAttributes.add("groups");
		internalAnnotationAttributes.add("payload");
	}

	protected Object[] getArgumentsForConstraint(final String objectName, final String field, final ConstraintDescriptor<?> descriptor) {
		final List<Object> arguments = new LinkedList<Object>();
		final String[] codes = new String[] { objectName + Errors.NESTED_PATH_SEPARATOR + field, field };
		arguments.add(new DefaultMessageSourceResolvable(codes, field));
		// Using a TreeMap for alphabetical ordering of attribute names
		final Map<String, Object> attributesToExpose = new TreeMap<String, Object>();
		for (final Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
			final String attributeName = entry.getKey();
			final Object attributeValue = entry.getValue();
			if (!internalAnnotationAttributes.contains(attributeName)) {
				attributesToExpose.put(attributeName, attributeValue);
			}
		}
		arguments.addAll(attributesToExpose.values());
		return arguments.toArray(new Object[arguments.size()]);
	}

	/**
	 * Convert the error arguments.
	 *
	 * @param arguments
	 *            The arguments to convert.
	 * @return The converted arguments.
	 */
	private List<Object> convertErrorArguments(final Object[] arguments) {
		final List<Object> converted = new ArrayList<Object>(arguments.length);
		for (final Object arg : arguments) {
			if (!(arg instanceof org.springframework.context.support.DefaultMessageSourceResolvable)) {
				converted.add(arg);
			}
		}
		return Collections.unmodifiableList(converted);
	}

	@Override
	public String toString() {
		return "Form(of=" + backedType + ", data=" + data + ", value=" + value + ", errors=" + errors + ")";
	}

	@Override
	public int size() {
		return data().size();
	}

	@Override
	public boolean isEmpty() {
		return data().isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return data().containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return data().containsValue(value);
	}

	@Override
	public String get(final Object key) {
		return data().get(key);
	}

	@Override
	public String put(final String key, final String value) {

		final Map<String, String> mapData = new HashMap<>();
		mapData.put(key, value);

		bind(mapData);

		return get(key);
	}

	@Override
	public String remove(final Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(final Map<? extends String, ? extends String> m) {

		final Map<String, String> mapData = new HashMap<>();
		mapData.putAll(m);

		bind(mapData);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		return data().keySet();
	}

	@Override
	public Collection<String> values() {
		return data().values();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return data().entrySet();
	}

}
