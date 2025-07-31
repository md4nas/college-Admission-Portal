package com.m4nas.repository;

import com.m4nas.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDtls, String> {

    public boolean existsByEmail(String email);

    public UserDtls findByEmail(String email);

    public UserDtls findByEmailAndMobileNumber(String email,String mobileNumber);

    public UserDtls findByVerificationCode(String code);


}
