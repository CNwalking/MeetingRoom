package com.walking.meeting;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class MeetingApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void bigDecimalTest(){
		System.out.println(new BigDecimal("1.00"));
	}

}
