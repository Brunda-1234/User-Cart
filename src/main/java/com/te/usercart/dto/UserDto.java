package com.te.usercart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class UserDto {
	private String userId;
	private String userName;
	private String fileUrl;

}
