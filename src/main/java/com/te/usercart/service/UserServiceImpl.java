package com.te.usercart.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.te.usercart.controller.FileUrls;
import com.te.usercart.dto.FileDto;
import com.te.usercart.dto.UserDto;
import com.te.usercart.entity.User;
import com.te.usercart.exception.DataNotFoundException;
import com.te.usercart.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final FileUrls fileUrls;

	@Override
	public UserDto addUser(UserDto userDto, MultipartFile file) {
		User user = new User();
		BeanUtils.copyProperties(userDto, user);
//		user.setUserName(userDto.getUserName());

		user.setFileType(file.getContentType());
		user.setFileName(file.getOriginalFilename());
		if(user.getUserName()==userDto.getUserName()) {
		user.setFileUrl(fileUrls.addFile(userDto.getUserName(), file, userDto.getUserName()));
		}
		User save = userRepository.save(user);
		BeanUtils.copyProperties(save, userDto);

		return userDto;

	}

	@Override
	public byte[] getUser(String userId) throws IOException {
		try {
			User user = userRepository.findByUserId(userId)
					.orElseThrow(() -> new DataNotFoundException("Invalid Candidate Id"));
			String filePath = user.getFileUrl();
//			This URL represents the location or path of the file.
			return Files.readAllBytes(new File(filePath).toPath());

		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException("Data Not Found");
		}
	}

	@Override
	public FileDto downloadUser(String userId) throws IOException {
		try {
			User user = userRepository.findByUserId(userId)
					.orElseThrow(() -> new DataNotFoundException("Invalid Candidate Id"));

			FileDto fileDto = new FileDto();
			fileDto.setFileUrl(user.getFileUrl());
			fileDto.setFileName(user.getFileName());
			fileDto.setSize(user.getFileUrl().length());
			fileDto.setFile(Files.readAllBytes(new File(user.getFileUrl()).toPath()));
			return fileDto;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFoundException("Data Not Found");
		}
	}

	@Override
	public UserDto updateUserFile(String userId, UserDto userDto, MultipartFile file) {
		User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Data Not Found"));
		System.err.println(user);
		user.setUserName(userDto.getUserName());
		user.setFileName(file.getOriginalFilename());

		user.setFileType(file.getContentType());
		user.setFileUrl(fileUrls.addFile("Files", file, userDto.getUserName()));
		User save = userRepository.save(user);
		UserDto dto = new UserDto();
		BeanUtils.copyProperties(save, dto);
		return dto;
	}

	@Override
	public String deleteUserFile(String userId) {
		User user = userRepository.findByUserId(userId).orElseThrow(() -> new DataNotFoundException("Data Not Found"));
        String fileUrl = user.getFileUrl();
        if(fileUrl != null) {
        	File file = new File(fileUrl);
        	if(file.exists()) {
        		file.delete();
        	}else {
        		System.err.println("File Not Found");
        	}
        }
        user.setFileUrl(null);
        userRepository.delete(user);
        return "Deleted Successfully";
	}

}

//private String getContentTypeFfileromFileName(String fileName) {
//	String[] fileParts = fileName.split("\\.");
//	String fileExtension = fileParts[fileParts.length - 1].toLowerCase();
//
//	switch (fileExtension) {
//	case "jpg":
//		return "image/jpeg";
//	case "docx":
//		return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
//	case "pdf":
//		return "application/pdf";
//	case "xls":
//	case "xlsx":
//		return "application/vnd.ms-excel";
//	default:
//		return "application/octet-stream";
//	}
//}
