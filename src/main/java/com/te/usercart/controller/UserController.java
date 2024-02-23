package com.te.usercart.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.te.usercart.dto.FileDto;
import com.te.usercart.dto.Response;
import com.te.usercart.dto.UserDto;
import com.te.usercart.exception.DataNotFoundException;
import com.te.usercart.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
@CrossOrigin("*")

public class UserController {

	private final UserService userService;
	private final ObjectMapper mapper;

	@PostMapping("/upload")
	public ResponseEntity<Response> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("data") String user) throws JsonMappingException, JsonProcessingException {
		System.err.println(user);
		UserDto userDto = mapper.readValue(user, UserDto.class);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Response(false, "User Uploaded Successfully", userService.addUser(userDto, file)));
	}

	@PutMapping("update/{userId}")
	public ResponseEntity<Response> updateUser(@PathVariable String userId, @RequestParam("file") MultipartFile file,
			@RequestParam("data") String user) throws JsonMappingException, JsonProcessingException {
		System.err.println(user);
		UserDto userOneDto = mapper.readValue(user, UserDto.class);
		return ResponseEntity.status(HttpStatus.OK).body(
				new Response(false, "Updated Successfully", userService.updateUserFile(userId, userOneDto, file)));

	}

	@DeleteMapping("delete/{userId}")
	public ResponseEntity<Response> deleteUser(@PathVariable String userId) {
		return ResponseEntity.status(HttpStatus.OK).body(new Response(false, "", userService.deleteUserFile(userId)));
	}

	@GetMapping("user/download/{userId}")
	public void downloadPdf(@PathVariable String userId,
			HttpServletResponse response) throws IOException {

		FileDto fileDto = userService.downloadUser(userId);
		File file = new File(fileDto.getFileUrl());
		response.setContentType("application/pdf");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + fileDto.getFileName() + ".pdf");

		InputStream inputStream = new FileInputStream(file);
		OutputStream outputStream = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		inputStream.close();
		outputStream.close();
	}

	@GetMapping("user/downloadPng/{userId}")
	public HttpServletResponse downloadPng(@PathVariable String userId, HttpServletResponse response)
			throws IOException {

		FileDto fileDto = userService.downloadUser(userId);
		File file = new File(fileDto.getFileUrl());

		response.setContentType("image/png");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + fileDto.getFileName() + ".png\"");
		response.setContentLength((int) file.length());

		InputStream inputStream = new FileInputStream(file);
		OutputStream outputStream = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		return response;

	}

	@GetMapping("user/downloadjpg/{userId}")
	public HttpServletResponse downloadjpg(@PathVariable String userId, HttpServletResponse response)
			throws IOException {

		FileDto fileDto = userService.downloadUser(userId);
		File file = new File(fileDto.getFileUrl());
		response.setContentType("image/jpeg");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + fileDto.getFileName() + ".jpg\"");
		response.setContentLength((int) file.length());

		InputStream inputStream = new FileInputStream(file);
		OutputStream outputStream = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		return response;

	}

	@GetMapping("user/downloaddocx/{userId}")
	public HttpServletResponse downloaddocx(@PathVariable String userId, HttpServletResponse response)
			throws IOException {

		FileDto fileDto = userService.downloadUser(userId);
		File file = new File(fileDto.getFileUrl());
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + fileDto.getFileName() + ".docx\"");
		response.setContentLength((int) file.length());
		InputStream inputStream = new FileInputStream(file);
		OutputStream outputStream = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		return response;

	}

	@GetMapping("user/AllFiles/{userId}")
	public HttpServletResponse downloadAllFile(@PathVariable String userId, HttpServletResponse response)
			throws IOException {

		FileDto fileDto = userService.downloadUser(userId);
		File file = new File(fileDto.getFileUrl());

		String fileExtension = getFileExtension(file.getName());
		String contentType = getContentType(fileExtension);

		response.setContentType(contentType);
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + fileDto.getFileName() + fileExtension + "\"");
		response.setContentLength((int) file.length());

		return response;
	}

	private String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex > -1) {
			return fileName.substring(dotIndex);
		}
		return "";
	}

	private String getContentType(String fileExtension) {
		switch (fileExtension.toLowerCase()) {
		case ".png":
			return "image/png";
		case ".jpg":
		case ".jpeg":
			return "image/jpeg";
		case ".pdf":
			return "application/pdf";
		case ".docx":
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		default:
			return "application/octet-stream";
		}
	}

	@GetMapping("user/downloadPdfSuggestion/{userId}")
	public void downloadPdf(@PathVariable String userId, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			FileDto fileDto = userService.downloadUser(userId);
			File file = new File(fileDto.getFileUrl());

			// Set the response headers
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileDto.getFileName() + ".pdf\"");
			response.setHeader("Content-Type", "application/pdf");
			response.setHeader("X-Content-Type-Options", "nosniff");
			response.setHeader("Content-Length", String.valueOf(file.length()));

			// Get the user agent from the request
			String userAgent = request.getHeader("User-Agent");

			// Check if the user agent is a web browser
			if (userAgent != null && userAgent.contains("Google")) {
				// Provide the suggestion message in the response body
				response.getWriter().write("Do you want to download the file?");
			} else {
				// Auto-download the file for non-web browser clients
				try (InputStream inputStream = new FileInputStream(file);
						OutputStream outputStream = response.getOutputStream()) {
					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
				}
			}

			// Flush and close the response writer
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException("Data Not Found");
		}
	}

}

/*
 * byte[] user = userService.getUser(userId); ByteArrayResource resource = new
 * ByteArrayResource(user); return ResponseEntity.ok()
 * .contentType(MediaType.APPLICATION_OCTET_STREAM)
 * .contentLength(resource.contentLength())
 * .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
 * .filename("whatever") .build().toString()) .body(resource);
 */
//@GetMapping("/download-file/{fileName:.+}")
//public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
//  // Load file as Resource
//  Resource resource = fileStorageService.loadFileAsResource(fileName);
//
//  // Try to determine file's content type
//  String contentType = null;
//  try {
//      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//  } catch (IOException ex) {
//      System.out.print("Could not determine file type.");
//  }
//
//  // Fallback to the default content type if type could not be determined
//  if(contentType == null) {
//      contentType = "application/octet-stream";
//  }
//
//  return ResponseEntity.ok()
//          .contentType(MediaType.parseMediaType(contentType))
//          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//          .body(resource);
//}
//	
//	byte[] userDto = userService.getUser(userId);
//// Step 2: Set response headers
//  HttpHeaders headers = new HttpHeaders();
//  headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" +userDto);
//  headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Set the Content-Type header to application/octet-stream
//  System.err.println(headers.toString());
//// Step 3: Create a ByteArrayResource from the file bytes
//  ByteArrayResource resource = new ByteArrayResource(userDto);
// 
//
//
//	return ResponseEntity.ok().headers(headers).body(resource);
//			
