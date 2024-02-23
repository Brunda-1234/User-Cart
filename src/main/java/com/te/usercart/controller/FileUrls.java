package com.te.usercart.controller;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.te.usercart.dto.UserDto;
import com.te.usercart.exception.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Component

public class FileUrls {

	private String dir = "/UploadedFiles";
	private AtomicInteger fileCounter = new AtomicInteger(1);

//This represents the base directory where the files will be stored.
	public String addFile(String folderName, MultipartFile multipartFile,String userName) {
		/*
		 * folderName, which specifies the name of the folder within the base directory,
		 * and multipartFile, which represents the file to be added.
		 */
		try {
			// calling the get path method to get absolute path to folder
			Path dirLocation = getPath(folderName);

			if (multipartFile != null) {
				// this code proceeds add the code to the folder
				Files.createDirectories(dirLocation); // This ensures that the folder is created before adding the file.
				String originalName = multipartFile.getOriginalFilename();// retrieve the original filename
                  String substring = originalName.substring(originalName.lastIndexOf('.'));
//                 String filename= userName+"_"+UUID.randomUUID().toString()+substring;
                 String filename= userName+"_"+substring;
                 
                
                  
//The file path within the folder is resolved	
				Path filePath = dirLocation.resolve(filename);
				// The contents of the multipartFile are copied to the specified file path
				Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				return filePath.toString();
			} else {
				throw new FileNotFoundException("File Not found");
			}
		} catch (Exception exception) {
			throw new DataNotFoundException(exception.getMessage());
		}
	}

//the getPath method is called to obtain the absolute path to the folder within the base directory.
	private Path getPath(String fileName) {
		return Paths.get(this.dir + "\\" + fileName).toAbsolutePath().normalize();
	}
	//The getPath method concatenates the base directory (dir) with the fileName parameter, separated by
	//"\" to form the relative file path. It then converts it to an absolute path using Paths.get(...).toAbsolutePath().
	//normalize() -- provide a standardized path representation.

	
	
}

