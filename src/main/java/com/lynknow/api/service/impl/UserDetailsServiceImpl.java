package com.lynknow.api.service.impl;

import com.lynknow.api.model.UserData;
import com.lynknow.api.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDataRepository userDataRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserData user = null;
        Page<UserData> pageUser = userDataRepo.getByUsername(
                username.toLowerCase(),
                PageRequest.of(0, 1, Sort.by("id").descending()));

        if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
            user = pageUser.getContent().get(0);
        }

        if (user != null) {
            return user;
        }

        throw new UsernameNotFoundException("User not found");
    }

}
