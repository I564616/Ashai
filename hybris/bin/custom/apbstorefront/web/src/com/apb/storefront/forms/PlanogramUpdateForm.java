package com.apb.storefront.forms;

import org.springframework.web.multipart.MultipartFile;

public class PlanogramUpdateForm {

	private String code;
	
	private MultipartFile file;
	
	private String documentName;
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the pdfFile
	 */
	public MultipartFile getFile() {
		return file;
	}
	/**
	 * @param pdfFile the pdfFile to set
	 */
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	/**
	 * @return the documentName
	 */
	public String getDocumentName() {
		return documentName;
	}
	/**
	 * @param documentName the documentName to set
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

}
