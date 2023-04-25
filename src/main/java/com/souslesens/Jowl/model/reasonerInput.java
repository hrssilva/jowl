package com.souslesens.Jowl.model;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public class reasonerInput {
	private String filePath;
	private String url;
	private String ontologyContentEncoded64;
	
	public reasonerInput(String filePath , String url, String ontologyContentEncoded64) {
		this.filePath = filePath;
		this.url = url;
		this.ontologyContentEncoded64 = ontologyContentEncoded64;
	}
	public String getFilePath() {
		return filePath;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getOntologyContentEncoded64() {
		return ontologyContentEncoded64;
	}

}
