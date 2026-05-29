package com.jsp.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.book.entity.BookedTicket;
import com.jsp.book.entity.User;

public interface TicketRepository extends JpaRepository<BookedTicket, Long> {
	List<BookedTicket> findByUser(User user);
	boolean existsByOrderId(String orderId);
}
