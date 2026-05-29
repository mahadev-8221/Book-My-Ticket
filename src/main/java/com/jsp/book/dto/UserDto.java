package com.jsp.book.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

	@Size(min = 3, max = 25, message = "* Enter between 3 ~ 25 Characters")
	private String name;

	@NotBlank(message = "* Email is Required")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "* Enter Proper Email (e.g. user@example.com)")
	private String email;

	@DecimalMin(value = "6000000000", message = "* Enter Proper Mobile Number")
	@DecimalMax(value = "9999999999", message = "* Enter Proper Mobile Number")
	private Long mobile;

	@Pattern(regexp = "^.*(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Select a Stronger Password")
	private String password;

	@Pattern(regexp = "^.*(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Select a Stronger Password")
	private String confirmPassword;

	@AssertTrue(message = "* Select the Checkbox in order to continue")
	private boolean terms;
}
