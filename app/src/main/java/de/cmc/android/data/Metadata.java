package de.cmc.android.data;

import java.io.Serializable;

/**
 * Created by Deniz Kalem on 08.08.17.
 */

public class Metadata implements Serializable {

	public  String  email;
	public  Gender  gender;
	public  String  zipCode;
	public  String  firstName;
	public  String  lastName;
	private String  birthday;
	private boolean newsletter;
	private int numberOfRequestedVouchers = 0;

	public enum Gender {
		MALE,
		FEMALE;
	}

	public Metadata(String email, Gender gender, String zipCode, String firstName, String lastName, String birthday) {
		this.email = email;
		this.gender = gender;
		this.zipCode = zipCode;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}


}
