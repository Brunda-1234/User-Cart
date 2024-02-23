package com.te.usercart.dto;

import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileDto {

	private String fileName;
	private String fileUrl;
	private Integer size;
	private byte[] file;

}

