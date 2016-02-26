package com.medinfi.utils;

import java.util.regex.Pattern;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;


public class Validation {
	// Regular Expression
	// you can change the expression based on your need
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_REGEX = "^[+]?[0-9]{10,13}$";
	//private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	// Error Messages
	private static final String REQUIRED_MSG = "required";
	private static final String EMAIL_MSG = "Invalid email";
	private static final String PHONE_MSG = "Enter vaild mobile no.";

	// call this method when you need to check email validation
	public static boolean isEmailAddress(EditText editText, boolean required,String msg) {
		return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required,msg);
	}
	
	public static boolean isEmailAddress(AutoCompleteTextView editText, boolean required,String msg) {
		return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required,msg);
	}

	// call this method when you need to check phone number validation
	public static boolean isPhoneNumber(EditText editText, boolean required,String msg) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required,msg);
	}
	
	public static boolean isPhoneNumber(AutoCompleteTextView editText, boolean required,String msg) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required,msg);
	}

	// return true if the input field is valid, based on the parameter passed
	public static boolean isValid(EditText editText, String regex,
			String errMsg, boolean required,String msg) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText(editText,msg))
			return false;

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(regex, text)) {
			editText.setError(errMsg);
			return false;
		}
		;

		return true;
	}
	
	
	public static boolean isValid(AutoCompleteTextView editText, String regex,
			String errMsg, boolean required,String msg) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText(editText,msg))
			return false;

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(regex, text)) {
			editText.setError(errMsg);
			return false;
		}
		;

		return true;
	}

	// check the input field has any text or not
	// return true if it contains text otherwise false
	public static boolean hasText(EditText editText,String message) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			editText.setError(message);
			return false;
		}

		return true;
	}
	
	public static boolean hasText(AutoCompleteTextView editText,String message) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			editText.setError(message);
			return false;
		}

		return true;
	}
	
	
	public static boolean hasText(TextView textView,String message) {

		String text = textView.getText().toString().trim();
		textView.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			textView.setError(message);
			return false;
		}

		return true;
	}
}