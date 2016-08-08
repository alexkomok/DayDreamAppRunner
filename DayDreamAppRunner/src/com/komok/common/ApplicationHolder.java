package com.komok.common;

public class ApplicationHolder {
	private String uri;
	private String label;

	public ApplicationHolder(String label, String uri) {
		super();
		this.uri = uri;
		this.label = label;
	}

	public String getUri() {
		return uri;
	}

	public String getLabel() {
		return label;
	}

}
