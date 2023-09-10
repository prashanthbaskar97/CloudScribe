package com.prashanth.cloud.app.repository;

import com.hari.cloud.app.dao.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> { }
