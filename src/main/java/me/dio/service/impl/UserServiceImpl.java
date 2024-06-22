package me.dio.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.dio.domain.model.User;
import me.dio.domain.repository.UserRepository;
import me.dio.service.UserService;
import me.dio.service.exception.BusinessException;
import me.dio.service.exception.NotFoundException;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private static final Long UNCHANGEABLE_USER_ID = 1L;
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(NotFoundException::new);
	}
	
	@Transactional(readOnly = true)
	public List<User> findAll() {
		return this.userRepository.findAll();
	}

	@Transactional
	public User create(User userToCreate) {
		ofNullable(userToCreate).orElseThrow(() -> new BusinessException("User to create must not be null."));
		ofNullable(userToCreate.getAccount()).orElseThrow(() -> new BusinessException("User account must not be null."));
		ofNullable(userToCreate.getCard()).orElseThrow(() -> new BusinessException("User card must not be null."));
		
		this.validateChangeableId(userToCreate.getId(), "created");
		if(userRepository.existsByAccountNumber(userToCreate.getAccount().getNumber())) 
			throw new BusinessException("This account number already exists.");
		if(userRepository.existsByCardNumber(userToCreate.getCard().getNumber())) 
			throw new BusinessException("This card number already exists.");
		return userRepository.save(userToCreate);
	}

	@Transactional
	public User update(Long id, User userToUpdate) {
		this.validateChangeableId(id, "updated");
		User dbUser = this.findById(id);
		if(!dbUser.getId().equals(userToUpdate.getId())) 
			throw new BusinessException("Update IDs must be the same.");
		
		dbUser.setName(userToUpdate.getName());
		dbUser.setAccount(userToUpdate.getAccount());
		dbUser.setCard(userToUpdate.getCard());
		dbUser.setFeatures(new ArrayList<>(userToUpdate.getFeatures()));
		dbUser.setNews(new ArrayList<>(userToUpdate.getNews()));
		return this.userRepository.save(dbUser);
	}

	@Transactional
	public void delete(Long id) {
		this.validateChangeableId(id, "deleted");
		User dbUser = this.findById(id);
		this.userRepository.delete(dbUser);
	}

	private void validateChangeableId(Long id, String operation) {
		if(UNCHANGEABLE_USER_ID.equals(id))
			throw new BusinessException("User with ID %d can not be %s.".formatted(UNCHANGEABLE_USER_ID, operation));
	}
}
