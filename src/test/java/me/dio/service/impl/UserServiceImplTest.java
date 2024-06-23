package me.dio.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.datatype.jdk8.OptionalDoubleSerializer;

import me.dio.controller.dto.AccountDto;
import me.dio.controller.dto.CardDto;
import me.dio.controller.dto.FeatureDto;
import me.dio.controller.dto.NewsDto;
import me.dio.controller.dto.UserDto;
import me.dio.domain.model.Account;
import me.dio.domain.model.Card;
import me.dio.domain.model.Feature;
import me.dio.domain.model.News;
import me.dio.domain.model.User;
import me.dio.domain.repository.UserRepository;
import me.dio.service.exception.BusinessException;
import me.dio.service.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	
	private User user;
	
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl service;
	
	@BeforeEach
	void setUp() {
		AccountDto account = new AccountDto(1L, "1111111", "4444", new BigDecimal(1000), new BigDecimal(1000));
		CardDto card = new CardDto(1L, "4444 4444 4444 4444", new BigDecimal(1000));
		FeatureDto feature = new FeatureDto(1L, "https://teste.teste", "Teste");
		ArrayList<FeatureDto> features = new ArrayList<FeatureDto>();
		features.add(feature);
		NewsDto news = new NewsDto(1L, "https://teste.teste", "Teste");
		ArrayList<NewsDto> newss = new ArrayList<NewsDto>();
		newss.add(news);
		
		UserDto userDto = new UserDto(1L, "Teste", account, card, features, newss);
		user = userDto.toModel();
	}
	
	@Test
	void testFindByIdWithSuccessful() {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findById(1L)).thenReturn(optionalUser);
		
		User userFinded = service.findById(1L);
		assertEquals(user, userFinded);
	}
	
	@Test
	void testFindByIdNotFind() {
		Optional<User> optionalUser = Optional.empty();
		when(userRepository.findById(1L)).thenReturn(optionalUser);
		
		assertThrows(NotFoundException.class, () -> service.findById(1L));
	}
	
	@Test
	void testFindAllSuccessful() {
		ArrayList<User> users = new ArrayList<>();
		users.add(user);
		when(userRepository.findAll()).thenReturn(users);
		
		List<User> usersFinded = service.findAll();
		assertTrue(users.contains(user));
	}
	
	@Test
	void testCreateWithSuccessful() {
		when(userRepository.existsByAccountNumber(anyString())).thenReturn(false);
		when(userRepository.existsByCardNumber(anyString())).thenReturn(false);
		when(userRepository.save(user)).thenReturn(user);
		
		User userCreated = service.create(user);
		assertEquals(user, userCreated);
	}
	
	@Test
	void testCreateWithUserNull() {
		user = new User();
		
		assertThrows(BusinessException.class, () -> service.create(user));
	}
	
	@Test
	void testCreateWithAccountNull() {
		user.setAccount(null);
		
		assertThrows(BusinessException.class, () -> service.create(user));
	}
	
	@Test
	void testCreateWithCardNull() {
		user.setCard(null);
		
		assertThrows(BusinessException.class, () -> service.create(user));
	}
	
	@Test
	void testCreateWithExistsAccountNumber() {
		when(userRepository.existsByAccountNumber(anyString())).thenReturn(true);
		
		assertThrows(BusinessException.class, () -> service.create(user));
	}
	
	@Test
	void testCreateWithExistsCardNumber() {
		when(userRepository.existsByCardNumber(anyString())).thenReturn(true);
		
		assertThrows(BusinessException.class, () -> service.create(user));
	}

	@Test
	void testUpdateWithSuccessful() {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findById(1L)).thenReturn(optionalUser);
		when(userRepository.save(user)).thenReturn(user);
		
		User updatedUser = service.update(1L, user);
		assertEquals(user.getId(), updatedUser.getId());
	}
	
	@Test
	void testUpdateWithDiferentsUserIds() {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findById(1L)).thenReturn(optionalUser);
		
		User userToUpdate = new User();
		userToUpdate.setId(2L);
		
		assertThrows(BusinessException.class, () -> service.update(1L, userToUpdate));
	}

	@Test
	void testDelete() {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findById(1L)).thenReturn(optionalUser);
		doNothing().when(userRepository).delete(any());

		service.delete(1L);
	}
}