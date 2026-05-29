package com.jsp.book.service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jsp.book.dto.UserDto;
import com.jsp.book.entity.BookedTicket;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

	private static final String USER_DTO_KEY = "dto-";
	private static final String OTP_KEY = "otp-";

	// We are using a simple in-memory map instead of Redis for ease of setup.
	// In a real production system, consider implementing TTL management for these maps, 
	// or use a caching library like Caffeine.
	private final Map<String, Object> cache = new ConcurrentHashMap<>();
	private final Map<String, Long> expiryMap = new ConcurrentHashMap<>();

	@Override
	@Async
	public void saveUserDto(String email, UserDto userDto) {
		cache.put(USER_DTO_KEY + email, userDto);
		expiryMap.put(USER_DTO_KEY + email, System.currentTimeMillis() + Duration.ofMinutes(15).toMillis());
	}

	@Override
	@Async
	public void saveOtp(String email, int otp) {
		cache.put(OTP_KEY + email, otp);
		expiryMap.put(OTP_KEY + email, System.currentTimeMillis() + Duration.ofMinutes(5).toMillis());
	}

	@Override
	public UserDto getUserDto(String email) {
		checkExpiry(USER_DTO_KEY + email);
		Object value = cache.get(USER_DTO_KEY + email);
		return (value instanceof UserDto dto) ? dto : null;
	}

	@Override
	public int getOtp(String email) {
		checkExpiry(OTP_KEY + email);
		Object value = cache.get(OTP_KEY + email);
		return (value instanceof Integer otp) ? otp : 0;
	}

	@Override
	public void saveTicket(String orderId, BookedTicket ticket) {
		cache.put(orderId, ticket);
		expiryMap.put(orderId, System.currentTimeMillis() + Duration.ofHours(1).toMillis());
	}

	@Override
	public BookedTicket getTicket(String orderId) {
		checkExpiry(orderId);
		Object value = cache.get(orderId);
		return (value instanceof BookedTicket ticket) ? ticket : null;
	}

	private void checkExpiry(String key) {
		Long expiry = expiryMap.get(key);
		if (expiry != null && System.currentTimeMillis() > expiry) {
			cache.remove(key);
			expiryMap.remove(key);
		}
	}

	@org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
	public void cleanup() {
		long now = System.currentTimeMillis();
		expiryMap.forEach((key, expiry) -> {
			if (now > expiry) {
				cache.remove(key);
				expiryMap.remove(key);
			}
		});
	}
}
