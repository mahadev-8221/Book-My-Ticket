package com.jsp.book.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CloudinaryHelper {

	@Value("${cloudinary.url}")
	private String cloudinaryUrl;

	private static final String FALLBACK_IMAGE = "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=2059&auto=format&fit=crop";
	private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

	private boolean isDummy() {
		return cloudinaryUrl == null || cloudinaryUrl.contains("dummy_key");
	}

	public String generateImageLink(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return FALLBACK_IMAGE;
		}

		if (isDummy()) {
			return saveLocally(file);
		}

		try {
			Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
			Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
			return (String) uploadResult.get("secure_url");
		} catch (IOException e) {
			log.error("Cloudinary upload failed, falling back to local storage", e);
			return saveLocally(file);
		}
	}

	public String getTheaterImageLink(MultipartFile image) {
		return generateImageLink(image);
	}

	public String saveTicketQr(byte[] qr) {
		if (isDummy()) {
			return saveBytesLocally(qr, "qr-");
		}

		try {
			Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
			Map uploadResult = cloudinary.uploader().upload(qr, ObjectUtils.emptyMap());
			return (String) uploadResult.get("secure_url");
		} catch (IOException e) {
			log.error("Cloudinary QR upload failed", e);
			return saveBytesLocally(qr, "qr-");
		}
	}

	private String saveLocally(MultipartFile file) {
		try {
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			Path path = Paths.get(UPLOAD_DIR + fileName);
			Files.createDirectories(path.getParent());
			Files.write(path, file.getBytes());
			return "/uploads/" + fileName;
		} catch (IOException e) {
			log.error("Local save failed", e);
			return FALLBACK_IMAGE;
		}
	}

	private String saveBytesLocally(byte[] data, String prefix) {
		try {
			String fileName = prefix + UUID.randomUUID().toString() + ".png";
			Path path = Paths.get(UPLOAD_DIR + fileName);
			Files.createDirectories(path.getParent());
			Files.write(path, data);
			return "/uploads/" + fileName;
		} catch (IOException e) {
			log.error("Local bytes save failed", e);
			return "";
		}
	}
}
