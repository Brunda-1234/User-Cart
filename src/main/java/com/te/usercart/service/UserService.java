package com.te.usercart.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.te.usercart.dto.FileDto;
import com.te.usercart.dto.UserDto;

public interface UserService {

	UserDto addUser(UserDto userDto, MultipartFile file);

	byte[] getUser(String userId) throws IOException;

	FileDto downloadUser(String userId) throws IOException;

	UserDto updateUserFile(String userId, UserDto userDto, MultipartFile file);
	
	String deleteUserFile(String userId);
}
