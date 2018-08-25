package com.iprogrammerr.simple.http.server.filter;

public class RequestUrlRule {

    private String url;
    private ValidationRule validationRule;

    private RequestUrlRule(String url, ValidationRule validationRule) {
	this.url = url;
	this.validationRule = validationRule;
    }

    public static RequestUrlRule createExact(String url) {
	return new RequestUrlRule(url, ValidationRule.EXACT);
    }

    public static RequestUrlRule createStartsWith(String url) {
	return new RequestUrlRule(url, ValidationRule.STARTS_WITH);
    }

    public static RequestUrlRule createAll() {
	return new RequestUrlRule("*", ValidationRule.ALL);
    }

    public boolean isCompliant(String url) {
	if (ValidationRule.ALL.equals(validationRule)) {
	    return true;
	}
	if (ValidationRule.EXACT.equals(validationRule)) {
	    return this.url.equals(url);
	}
	return url.startsWith(this.url);
    }

    private enum ValidationRule {
	EXACT, STARTS_WITH, ALL
    }
}
